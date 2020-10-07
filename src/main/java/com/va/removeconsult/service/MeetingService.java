package com.va.removeconsult.service;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.va.removeconsult.bean.User;
import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.UploadKeyCertificate;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.clouddisk.util.FileBlockUtil;
import com.va.removeconsult.clouddisk.util.LogUtil;
import com.va.removeconsult.clouddisk.util.ServerTimeUtil;
import com.va.removeconsult.dao.DbDao;
import com.va.removeconsult.dao.DictItemDao;
import com.va.removeconsult.dao.FolderDao;
import com.va.removeconsult.dao.MeetingDao;
import com.va.removeconsult.dao.NodeDao;
import com.va.removeconsult.dto.AjaxResult;
import com.va.removeconsult.util.CutPicUtil;
import com.va.removeconsult.util.FileLoadClient;
import com.va.removeconsult.util.SendEmail;
import com.va.removeconsult.util.UUIDUtil;
import com.va.removeconsult.util.Utils;
import com.va.removeconsult.websocket.MyWebSocketHandler;

import web.util.SqlHelper;

@Service 
public class MeetingService {
	@Autowired  
    private MeetingDao meetingDao;
	
	@Autowired  
	private DictItemDao dictItemDao;
	
	@Autowired  
	private DbService db;
	@Autowired  
    private DbDao dbDao;
	@Autowired  
	private UserService userService;
	
	@Autowired  
	private AsyncService asyncService;
	
	@Autowired
    private MyWebSocketHandler myWebSocketHandler;
	
	@Resource
	private NodeDao fm;
	@Resource
	private FolderDao folderDao;
	@Resource
	private FileBlockUtil fbu;
	@Resource
	private FolderService folderService;
	@Resource
	private LogUtil lu;
	
	private static Map<String, UploadKeyCertificate> keyUploadMap = new HashMap<>();
	
	
    Logger logger = LoggerFactory.getLogger(MeetingService.class);
	
	public ArrayList<Map<String,String>> selectItems(String type){
		return dictItemDao.selectItems(type);
	}
	
	public String getNodeJSON(int type){
		String nodes;
		List<Map> items=db.getMeetingSearchStatus(type);
		List<Map<String,Object>> rows=new ArrayList<Map<String,Object>>();
		for(Map<String,String> item:items){
			Map<String,Object> row=new HashMap<String,Object>();
			String icon="layui-icon layui-icon-circle icon-status-"+String.valueOf(item.get("key"));
			row.put("text", item.get("value"));
			row.put("type", String.valueOf(item.get("key")));
			row.put("roletype", item.get("type"));
			row.put("icon", icon);
			rows.add(row);
		}
		nodes=JSON.toJSONString(rows);
		return nodes;
	}
	
	public ArrayList<Map<String,String>> getMeetings(Map<String,Object> param){
		ArrayList<Map<String,String>> rows=null;
		rows=meetingDao.getMeetings(param);
		return rows;
	}
	
	public String getMeetingCount(Map<String,Object> param){
		return meetingDao.getMeetingCount(param);
	}
	
	public int MeetingUserAction(Map meeting,String action,int userId){
		int code=0;
		int meetingId=(int) meeting.get("id");
		int status=(int) meeting.get("status");
		String role;
		if((int)meeting.get("user")==userId) role="apply";
		else role="attend";
		String logInfo = "";
		if(status==1){
			if(action.equals("cancle") && role.equals("apply")){
				code=db.updateMeetingStatus(meetingId,5);
				logInfo = "取消";
			}
			else if(action.equals("accept") && role.equals("attend")){
				code=db.addMeetingAccept(meeting,userId);
				logInfo = "加入";
			}
		}
		else if(status==2){
			if(action.equals("cancle") && role.equals("apply")){
				code=db.updateMeetingStatus(meetingId,5);
				logInfo = "取消";
			}
		}
		else if(status==6){
			if(action.equals("recheck") && role.equals("apply")){
				code=db.updateMeetingStatus(meetingId,1);
				logInfo = "取消";
			}
		}
		if(!StringUtils.isEmpty(logInfo)) {
			Map map = new HashMap();
			map.put("id", userId);
			db.addLog("会诊中心>"+logInfo+"会诊", map);
			
		}
		return code;
	}
	
	public int MeetingAdminAction(Map meeting,String action,HttpServletRequest request){
 		int code=0;
		int meetingId=(int) meeting.get("id");
		int status=(int) meeting.get("status");

		Map user = db.getUser((int)meeting.get("user"));
		String proposerEmail = (String)user.get("email");
		String proposerPhone = (String)user.get("phone");
		
		if(status==1){
			
			if(action.equals("check")){
				code=db.updateMeetingStatus(meetingId,2);
				
				//邮件通知参与人
				//主持人,申请人
				//String proposer = (String)meeting.get("user");
				
				//参与人邮箱
				String where = " user.id in("+meeting.get("attends")+")";
				Map<String, Object> pagination = new HashMap<String,Object>();
				pagination.put("page", 1);
				pagination.put("size", Integer.MAX_VALUE);
				List<Map> users = db.getUsers(where, pagination);
				StringBuffer partInPerson = new StringBuffer();
				StringBuffer partInPersonMail = new StringBuffer();
				StringBuffer partInPersonPhone = new StringBuffer();
				for (Map map : users) {
					partInPerson.append(map.get("name")+",");
					partInPersonMail.append(map.get("email")+",");
					partInPersonPhone.append(map.get("phone").toString().substring(12)+",");
				}
				Map<String,String> mailTemplate = db.getMailTemplate("mail_meeting_notify");
				meeting.put("proposer", user.get("name"));//主持人
				meeting.put("partInPerson", partInPerson.length()>0?partInPerson.substring(0,partInPerson.length()-1):partInPerson);//参与人
				String title = SendEmail.formatTemplate(mailTemplate.get("template_title"),meeting);
				String text = SendEmail.formatTemplate(mailTemplate.get("template_context"), meeting);
				String[] emails = partInPersonMail.append(proposerEmail).toString().split(",");
				for (String mailTo : emails) {
					System.err.println(mailTo);
					System.err.println(title);
					System.err.println(text);
					//SendEmail.send(mailTo, title, text);
					asyncService.sendMail(mailTo, title, text);
				}
				
				String[] phones = partInPersonPhone.append(proposerPhone).toString().split(",");
				for (String phone : phones) {
					try {
						String[] params=new String[]{
								meeting.get("proposer").toString(),
								meeting.get("topic").toString(),
								meeting.get("stime").toString(),
								meeting.get("etime").toString(),
								meeting.get("partInPerson").toString(),
								};
						asyncService.sendSms(phone, "SMS_TEMP_5", params);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				db.addLog("用户管理>同意操作", userService.getLoginInfo(request));
			}
			else if(action.equals("reject")){
				code=db.updateMeetingStatus(meetingId,6);
				db.addLog("用户管理>拒绝操作", userService.getLoginInfo(request));
				asyncService.sendMail(proposerEmail, "会诊被拒绝", "用户您"+user.get("name").toString()+"发起的会诊已被管理员拒绝！");
				
				try {
			    	if(user!=null && user.containsKey("phone") && user.get("phone")!=null) {
						String toPhones = user.get("phone").toString();
						String toPhone = toPhones.substring(12);
						String[] params=new String[]{
								user.get("user").toString(),
								meeting.get("topic").toString()
						};
						asyncService.sendSms(toPhone, "SMS_TEMP_11", params);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		}else if(status==3) {
			if(action.equals("stop")){
				code=db.updateMeetingStatus(meetingId,4);
				try {
					
					Map<String, Object> meetCound = db.getMeetingCount(meeting.get("no").toString());
					if(meetCound != null && meetCound.get("stear_time") != null && meetCound.get("code") != null ){
						int people = meeting.get("attends").toString().split(",").length + 1;
						long date = ServerTimeUtil.accurateToSecond(String.valueOf(meetCound.get("stear_time"))).getTime();
						long date1 = new Date().getTime();
						long t =date1 - date;
						long time = 0;
						if(t/1000%60>1){
							time = t/1000/60+1;
						}else{
							time = t/1000/60;
						}
						int cound = (int) (people*time);
						meetCound.put("type", 1);
						meetCound.put("end_time",ServerTimeUtil.getDateTime());
						meetCound.put("people", people);
						meetCound.put("duration", time);
						meetCound.put("duration_count", cound);
						db.updateMeetingBycode(meetCound, meetCound.get("code").toString());
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 String attends = (String)meeting.get("attends");
				 List<Map> attendsObject = this.getAttends(attends);
				 
				 if(meeting.get("user")!=null) {
					 String meetingUser =meeting.get("user").toString();
					 if(StringUtils.isNotEmpty(meetingUser)){
						 attendsObject.addAll(this.getAttends(meetingUser));
					 }
				 }
				 
				 for (Map map : attendsObject) {
					 String toUser = (String)map.get("user");
					 Map<String,String> msgDataMap = new HashMap<String,String>();
					 msgDataMap.put("type", "quitMeeting");
					 String transferMsg = JSON.toJSONString(msgDataMap);
					 TextMessage textMessage = new TextMessage(transferMsg);
					 //myWebSocketHandler.sendMessageToUser(toUser,textMessage);
					 myWebSocketHandler.sendMessageToUserForMeeting(toUser, textMessage,
								"quitMeeting", String.valueOf(meetingId));
					 
					 try {
				    	if(map!=null && map.containsKey("phone") && map.get("phone")!=null) {
							String toPhones = map.get("phone").toString();
							String toPhone = toPhones.substring(12);
							String[] params=new String[]{
									map.get("user").toString(),
									user.get("user").toString(),
							};
							asyncService.sendSms(toPhone, "SMS_TEMP_8", params);
						}
						} catch (Exception e) {
							e.printStackTrace();
						}
				 }
				 
				 db.addLog("用户管理>中断操作", userService.getLoginInfo(request));
				 asyncService.sendMail(proposerEmail, "会诊被中断", "用户您"+user.get("name").toString()+"发起的会诊已被管理员中断！");
				   
			}
		}
		return code;
	}

	public String getMeetingLog(List<Map> meetings) {
		for (Map row : meetings) {
			row.put("time", String.valueOf(row.get("stime"))+" "+String.valueOf(row.get("etime")));
			row.put("attends", db.getUserNames((String)row.get("attends")," "));
			row.put("meetingtype", row.get("type")==null?"":row.get("type").toString().equals("1")?"会诊":"直播");
		}
		return Utils.getLogJson(meetings, getMeetingTitle());
	}
	public Map<String,String> getMeetingTitle() {
		Map<String,String> titleMap = new HashMap<String,String>();
		titleMap.put("no", "会诊编号");
		titleMap.put("user_user", "创建账号");
		titleMap.put("user_name", "创建者");
		titleMap.put("time", "会诊时间");
		titleMap.put("topic", "会诊主题");
		titleMap.put("meetingtype", "会诊类型");
		titleMap.put("attends", "与会者");
		return titleMap;
	}
	
	public Map<String,Object> getMeeting(int id){
	    return meetingDao.selectMeetingById(id);
	}
	
	public int updateMeetingPresenter(int userId,String attends,int meetingId){
	    return meetingDao.updateUser(userId,attends, meetingId);
	}
	
	public List<Map> getAttends(String attends) {
		if(attends != null && attends.length()>0 && attends.trim().length()>0) {
			attends = attends.trim();
			if(",".equals(attends.substring(0, 1))) {
				attends = attends.substring(1, attends.length());
			} else if(",".equals(attends.substring(attends.length()-1, attends.length()))) {
				attends = attends.substring(0, attends.length()-1);
			}
		}
        String sql="select * from user where id in("+SqlHelper.Addslashes(attends)+ ")";
        return dbDao.select(sql);
    }
	
	/**
	 * 更新会诊状态
	 * @param meetingId 会诊ID
	 * @param statusId 状态ID
	 * @return
	 */
	public int updateMeetingStatus(int meetingId,int statusId) {
        String sql="update meeting set status="+statusId+" where id="+meetingId;
        return dbDao.update(sql);
    }
	
	/**
     * 更新会诊状态
     * @param meetingId 会诊ID
     * @param statusId 状态ID
     * @return
     */
    public int updateMeetingInfo(int meetingId,String meetingTitle,String meetingTime,String meetingAttends,
            String meetingAbsentee,String meetingConclusion) {
        String sql="update meeting set topic='"+meetingTitle +"', stime='"+meetingTime
                //+"', attends='" + meetingAttends+"', absentee='"+meetingAbsentee
                +"', conclusion='" + meetingConclusion
                +"' where id="+meetingId;
        return dbDao.update(sql);
    }
    
    /**
     * 获取专家列表
     * 不包含管理层，主持人和与会者的专家都被查找出来
     * 
     */
    public List<Map> getExperts(String users, Integer belong) {
		if(StringUtils.isNotBlank(users)) {
			users = users.trim();
		}
		String sql="select * from user where `group` <> 'admins' AND id not in("+SqlHelper.Addslashes(users)+ ")";
		StringBuilder sb = new StringBuilder(sql);
		if(belong != null){
			sb.append(" AND `belong` = ").append(belong);
		}
		System.out.println(sb.toString());
        return dbDao.select(sb.toString());
    }
    
    /**
     * 
     * 发送邀请邮件给专家
     *
     * @param meeting  会诊
     * @param userName 专家名称
     * @param meetingUserName 主持人名称  
     * @param userEmail 专家邮箱
     */
    public void sendMailByInviteExpert(Map meeting, String userName, String meetingUserName, String userEmail, String userPhone){
    	Map<String,String> mailTemplate = db.getMailTemplate("mail_meeting_invite_expert");
		meeting.put("proposer", meetingUserName);//主持人
		meeting.put("userName", userName);//专家名称
		String title = SendEmail.formatTemplate(mailTemplate.get("template_title"), meeting);
		String text = SendEmail.formatTemplate(mailTemplate.get("template_context"), meeting);
		System.err.println(userEmail);
		System.err.println(title);
		System.err.println(text);
		//SendEmail.send(mailTo, title, text);
		asyncService.sendMail(userEmail, title, text);
		
		try {
			String[] params=new String[]{
					userName,
					meetingUserName,
					meeting.get("topic").toString()
					};
			asyncService.sendSms(userPhone, "SMS_TEMP_7", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
    }
    
    /**
     * 获取会诊所有人员
     */
    public List<Map> getAllPeople(String ids, String currentUserId) {
		if(StringUtils.isNotBlank(ids)) {
			ids = ids.trim();
		}
		String sql="select * from user where `group` <> 'admins' AND id in("+SqlHelper.Addslashes(ids)+ ")";
		StringBuilder sb = new StringBuilder(sql);
		
		if(StringUtils.isNotBlank(currentUserId)){
			sb.append("AND id <> " + currentUserId);
		}
		
		System.out.println(sb.toString());
        return dbDao.select(sb.toString());
    }
    
    public void joinMeetingBroadcast(Map user, String ids, String meetingId){
    	//推送信息给所有当前会诊的人
        Map<String,String> msgDataMap = new HashMap<String,String>();
        msgDataMap.put("type", "joinMeetingBroadcast");
        msgDataMap.put("userId", user.get("id").toString());//专家信息
        msgDataMap.put("userName", user.get("name").toString());//专家信息
        String transferMsg = JSON.toJSONString(msgDataMap);
        TextMessage textMessage = new TextMessage(transferMsg);
        
        List<Map> allPeople = getAllPeople(ids, user.get("id").toString());
        for (Map map : allPeople) {
            //专家一进会诊就推送给在参加会诊的人
            String sendResultMsg = myWebSocketHandler.sendMessageToUserForMeeting(map.get("user").toString(),textMessage,"joinMeetingBroadcast",meetingId);
            logger.info(sendResultMsg +" -- " + map.get("user"));
		}
        

    }
    
    
    public Map<String,Object> getMeetingCode(String code){
	    return meetingDao.selectMeetingByCode(code);
	}
    
    public int updatMeetingeByAttaId(String attaId,int id){
	    return meetingDao.updatMeetingeByAttaId(attaId, id);
	}
    
    
	//update by and 
    public AjaxResult ScreenshotImg(HttpServletRequest request,Map<String, Object> currentUser,
    		String imageName,String path,String imgType,String meetingNo,String data,double ratio) {
    	 AjaxResult map = new AjaxResult();
         boolean flag = true;
         try {
        	 Thread.sleep(500);
        	 File captureScreen = CutPicUtil.captureScreen(imageName, path,imgType,data,ratio);
        	 if(null == captureScreen) {
        		 map.put("upload", false);
        	 }else {
        		 Map<String, Object> savePicRes = savePic(captureScreen,request,meetingNo,currentUser);
            	 map.put("upload", savePicRes.get("upload"));
            	 map.put("msg", savePicRes.get("msg"));
        	 }
		} catch (Exception e) {
			 e.printStackTrace();
	         flag = false;
	         map.put("msg", e.getMessage());
		}
         if(flag){
             map.put("state","0");//截屏成功
             map.put("path",path+imageName+"."+imgType);
             
         }else{
             map.put("state","1");//截屏失败
         }
         return map;
    }
    
    
    public Map<String, Object> savePic(File file , HttpServletRequest request,String meetingNo,Map<String, Object> currentUser) {
    	Map<String, Object> res = new HashMap<>();
    	boolean upload = true;
    	try {
    		if(StringUtils.isNotBlank(meetingNo) ) {
        	    String account = (String) request.getSession().getAttribute("ACCOUNT");
            	//查询目录
                 Map<String, Object> folderMap = new HashMap<>();
                 folderMap.put("fname", meetingNo);
                 folderMap.put("userId", currentUser.get("id"));
                 List<Folder> queryByNameAndUser = this.folderDao.queryByNameAndUser(folderMap);
                 String folderId = "";
                 if(CollectionUtils.isNotEmpty(queryByNameAndUser)) {
                	 Map<String, String> folderMap1 = new HashMap<>();
                	 folderMap1.put("folderName", "抓拍");
                	 folderMap1.put("parentId", queryByNameAndUser.get(0).getFolderId());
                	 Folder queryByParentIdAndFolderName = this.folderDao.queryByParentIdAndFolderName(folderMap1);
                	 if(null == queryByParentIdAndFolderName) {//新增snap文件夹
                		//创建目录
                         Folder f = new Folder();
                         f.setFolderUserId((Integer)currentUser.get("id"));//currentUser.get("id")
                 		 f.setFolderId(UUIDUtil.newUUID());
                 		 f.setFolderName("抓拍");
                 		 f.setFolderCreationDate(ServerTimeUtil.accurateToDay());
                 		 f.setFolderCreator(String.valueOf(currentUser.get("name")));
                 		 f.setFolderParent(queryByNameAndUser.get(0).getFolderId());
                 		 f.setFolderSize(0L);
                         this.folderDao.insertNewFolder(f);
                         folderId = f.getFolderId();
                	 }else {
                		 folderId = queryByParentIdAndFolderName.getFolderId();
                	 }
                 }
                 
                FileLoadClient fileLoadClient = new FileLoadClient();
                final String path = this.fbu.saveToFileBlocks(fileLoadClient.getMultipartFileByFile(file));
         		String imageName = "";
         		String fsize = "";
         		if (file.exists() && file.isFile()) {
         			imageName = file.getName();
         			fsize= String.valueOf(file.length());
         		}
         		List<String> namelistObj = Arrays.asList(imageName);
         		final List<String> pereFileNameList = new ArrayList<>();
         		for (final String fileName : namelistObj) {
        			if (folderId == null || folderId.length() <= 0 || fileName == null || fileName.length() <= 0) {
        				res.put("msg", "errorParameter");
        			}
        			final List<Node> files = this.fm.queryByParentFolderId(folderId);
        			if (files.stream().parallel().anyMatch((n) -> n.getFileName()
        					.equals(new String(fileName.getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8"))))) {
        				pereFileNameList.add(fileName);
        			}
        		}
         		
         		String sysDiskSizeStr = this.folderService.querySysConfValue(SysConfig.DISK_SIZE.getKey());
    			long sysDiskSize = Long.valueOf(sysDiskSizeStr);
    			
    			long rootFolderSize = this.folderService.queryRootFolderSize((Integer)currentUser.get("id"));
    			if(rootFolderSize + file.length() > sysDiskSize){
    				res.put("msg", "uploadlimiterror");
    			}
    			
         		final Node f2 = new Node();
    			f2.setFileId(UUIDUtil.newUUID());
    			f2.setFileCreator(String.valueOf(currentUser.get("name")));
    			f2.setFileUserId((Integer)currentUser.get("id"));
    			f2.setFileCreationDate(ServerTimeUtil.accurateToDay());
    			f2.setFileName(imageName);
    			f2.setFileParentFolder(folderId);
    			f2.setFilePath(path);
    			f2.setFileSize(fsize);
    			int i = 0;
    			while (true) {
    				try {
    					if (this.fm.insert(f2) > 0) {
    						this.lu.writeUploadFileEvent(f2, account);
    						upload = true;
    						break;
    					}else {
    						upload = false;
    					}
    				} catch (Exception e) {
    					f2.setFileId(UUID.randomUUID().toString());
    					i++;
    				}
    				if (i >= 10) {
    					break;
    				}
    			}
             }
		} catch (Exception e) {
			upload = false;
		}
    	//删除该文件
		if (file.isFile()) {
			boolean delFile = FileLoadClient.deleteDirectory(file.getParentFile().getPath());
		} 
		if(upload) {
			res.put("msg", "upload success");
		}else {
			res.put("msg", "upload error");
		}
    	res.put("upload", upload);
		return res;
    }

	public JSONObject findMeetingName(int meetingId) {
		// TODO Auto-generated method stub
		return meetingDao.findMeetingName(meetingId);
	}

}
