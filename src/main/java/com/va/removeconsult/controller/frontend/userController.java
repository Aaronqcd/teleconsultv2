package com.va.removeconsult.controller.frontend;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.va.removeconsult.util.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.va.removeconsult.bean.User;
import com.va.removeconsult.bean.UserMemoryData;
import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.clouddisk.util.DecryptionUtil;
import com.va.removeconsult.service.AsyncService;
import com.va.removeconsult.service.DbService;
import com.va.removeconsult.service.MeetingService;
import com.va.removeconsult.service.UserService;

import web.util.LoginRsaValid;
import web.util.RsaUtil;
import web.util.SqlHelper;

@Controller
public class userController {
	@Resource
    private UserService userService;
	@Resource
	private MeetingService meetingService;
	@Resource
	private DbService db;
	@Resource
	private FolderService folderService;
	@Autowired
	private AsyncService asyncService;

	@Autowired
	private StringRedisTemplate redisTemplate2;



	@RequestMapping("toDefault")
	public ModelAndView toDefault(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("Default");
		Map<String,Object> user=userService.getLoginInfo(request);
		if(MapUtils.isNotEmpty(user)){
			int user_id=(int) user.get("id");
			mv.addObject("currentUserId", user_id);
		}
		mv.addObject("logoUrl",folderService.querySysConfValue(SysConfig.SYS_LOGO.getKey()));
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		folderService.createRootFolder(request);
		return mv;
	}

	@RequestMapping("/Default")
	@ResponseBody
	public String Default(HttpServletRequest request,HttpSession session,HttpServletResponse response,@RequestBody String data){
		System.out.println("******************"+data);
		String username = "";
		String password = "";
		String code = "";
		Map<String,String> map = LoginRsaValid.valid(data);
		if (null != map && !map.isEmpty()){
			username = map.get("user") == null ? "" : map.get("user");
			password = map.get("password") == null ? "" : map.get("password");
			code = map.get("code") == null ? "" : map.get("code");
		}
		String codeName = session.getAttribute("CODE_TEST").toString();
		if(!code.equalsIgnoreCase(codeName)){
			return "2";
		}

		User users = userService.findUserList(username);
		if(users == null){
			return "1";
		}

		String key = CommonConf.LOGIN_STOCK+username;
		Boolean flag = redisTemplate2.hasKey(key);
		if(flag){
			int count = Integer.parseInt(redisTemplate2.opsForValue().get(key));
			if(count>=5){
				return "3";
			}
		}
		String passwod = password;
		if(!users.getPassword().equals(passwod)){
			if(flag){
				Long count = redisTemplate2.opsForValue().increment(key, 1);
				if(count>=5){
					redisTemplate2.expire(key,10,TimeUnit.MINUTES);
				}
			}else{
				redisTemplate2.opsForValue().set(key, "1");//第一次失败
				redisTemplate2.expire(key, 5, TimeUnit.MINUTES);//5分钟之内失败3次
			}
			return "4";
		}

		redisTemplate2.delete(key);
		session.setAttribute("userlist", users);

		return "5";
	}

	@RequestMapping("/MeetingManage")
	public ModelAndView MeetingManage(){
		ModelAndView mv = new ModelAndView("MeetingManage");
		String node=meetingService.getNodeJSON(0);

		Map<String, Object> applystatus=db.getMeetingStatus("apply");
		Map<String, Object> attendstatus=db.getMeetingStatus("attend");
		mv.addObject("node", node);
		mv.addObject("applystatus", JSON.toJSONString(applystatus));
		mv.addObject("attendstatus", JSON.toJSONString(attendstatus));

		List<Map> meetingaction=db.getMeetingStatus();
		mv.addObject("meetingaction",JSON.toJSONString(meetingaction));
		return mv;
	}

	@RequestMapping("/getMeetings")
	public ModelAndView getMeetings(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("json");
		Map<String,Object> result=new HashMap<String,Object>();
		List<Map<String,Object>>data=new ArrayList<>();
		List<Map>rows=null;
		String code="0";
		String msg="";
		int count;

		Map<String,Object> user=userService.getLoginInfo(request);
		int user_id=(int) user.get("id");
		String where=" (meeting.user="+user_id+" or find_in_set("+user_id+",meeting.attends)) ";

		Map<String,Object>pagination=new HashMap<String,Object>();
		String seachValue=(String) request.getParameter("seachValue");
		String username=(String) request.getParameter("username");
		String status=(String) request.getParameter("status");
		String type=(String) request.getParameter("type");
		int page= Integer.parseInt(request.getParameter("page"));
		int limit=Integer.parseInt(request.getParameter("limit"));
		limit=limit ==0 ?10:limit;

		if(status!=null && !status.equals("")){
			if(status.equals("2")){
				status=SqlHelper.Addslashes(status);
				if(type.equals("2"))where=" meeting.user="+user_id+" and meeting.`status`=\""+status+"\" ";
				else if(type.equals("3"))where=" find_in_set("+user_id+",meeting.attends) and meeting.`status`=\""+status+"\"";
				else where+=" and 0";
			}
			else{
				status=SqlHelper.Addslashes(status);
				where+=" and meeting.`status`=\""+status+"\"";
			}
		}

		if(username!=null && !username.equals("")){
			username=SqlHelper.Addslashes(username);
			where+=" and upper(user.`user`) like upper(trim(\"%"+username+"%\"))";
		}
		//页面搜索条件
		if(seachValue!=null && !seachValue.trim().equals("")){
			seachValue=SqlHelper.Addslashes(seachValue);
			StringBuffer str = new StringBuffer();
			str.append("ifnull(meeting.no,''),ifnull(user.user,''),ifnull(user.name,''),ifnull(meeting.topic,''),ifnull(meeting.stime,''),ifnull(meeting.etime,''),");
			str.append("ifnull(meeting.conclusion,'无'),");
			str.append("(select a.value from dictitem a where a.type='meetingtype' and a.pid=0 and a.`key`=meeting.type limit 1),");
			str.append("(select b.attendname from meeting_status b where b.id=meeting.status limit 1),");
			str.append("(select replace(group_concat(c.name),',',';') from user c where find_in_set(c.id,meeting.attends))");
			where+=" and upper(concat("+str+")) like upper(\"%"+seachValue.trim()+"%\")";
		}

		pagination.put("size", limit);
		pagination.put("page", page);
		rows=db.getMeetings(where, pagination);
		count=db.getMeetingsCount(where);
		int flag =1;
		for(Map<String,Object>row:rows){
			boolean show = true;
			 int length = row.get("attends").toString().length();
		   if(!row.get("user").toString().trim().equals(user.get("id").toString().trim()) && length ==0  ) {
				show = false;
			}
			if(show) {
				Map<String,Object>d=new HashMap<String,Object>();
				d.put("id",row.get("id"));
				d.put("encode", row.get("no"));
				d.put("account", row.get("user_user"));
				d.put("username", row.get("user_name"));
				d.put("title", row.get("topic"));
				d.put("time", String.valueOf(row.get("stime"))+" "+String.valueOf(row.get("etime")));
				d.put("record", "");
				Map attr = db.getAttr((String)row.get("attaId"));
				d.put("sysName", attr!=null?attr.get("sysName"):"");
				d.put("note", row.get("note"));
				d.put("status", String.valueOf(row.get("status")));
				d.put("meetingtype", row.get("meetingtype"));
				String attends="";
				//update by and
				if(length >0) {
					attends = db.getUserNames((String)row.get("attends"),";");
				}
				if(attends.trim().endsWith("；")) {
					attends = attends.trim().substring(0,attends.trim().length()-1);
				}
				d.put("members", attends);

				if(user_id==(int)row.get("user"))d.put("role", "applybutton");
				else d.put("role", "attendbutton");
				data.add(d);
			}

		}
		result.put("code", code);
		result.put("msg", msg);
		result.put("count", count);
		result.put("data", data);
		String json=JSON.toJSONString(result);
		mv.addObject("json", json);
		db.addLog("会诊中心>列表查询", userService.getLoginInfo(request));
		return mv;
	}

	@RequestMapping("/dropMeeting")
	public void dropMeeting(String ids,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> user=userService.getLoginInfo(request);
		Map<String, Object> pagination = new HashMap<String, Object>();
    	pagination.put("page", 1);
    	pagination.put("size", Integer.MAX_VALUE);
		String userId = user.get("id").toString();
		List<Map> meetings = db.getMeetings("meeting.id in("+ids+") and find_in_set("+userId+",meeting.attends)", pagination);

		//update by and
		String log = "";
		JSONObject data=new JSONObject();
		int code = 0;
		boolean flag =true;
		if(meetings.size() == 0) {
			flag = false;
		}
		for (Map map : meetings) {
			int status = (int) map.get("status");
			if( status != 3) {
				if(map.get("user").toString().equalsIgnoreCase(user.get("id").toString())) {
					code=db.dropUserMeetings(map.get("id").toString(),(int)user.get("id"));

					try {
						String attends = map.get("attends").toString();
						String[] split = attends.split(",");
						for (int i = 0; i < split.length; i++) {
							String id = split[i];
							Map byUser = db.getByUser(id);
							if (null != byUser) {
								if(byUser.containsKey("phone") && byUser.get("phone")!=null) {
									String toPhones = byUser.get("phone").toString();
									String toPhone = toPhones.substring(12);
									String[] params=new String[]{map.get("no").toString()};
									asyncService.sendSms(toPhone, "SMS_TEMP_1", params);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}else {
					//更新标记
					String removeAttendsStrArrayByKey = StringUtil.removeStrArrayByKey(map.get("attends").toString(), user.get("id").toString());
					code = db.updateIsDelMetting(Integer.valueOf(map.get("id").toString()),removeAttendsStrArrayByKey);
				}
			}else {
				flag = false;
			}
		}
		data.put("msg", flag);
		data.put("code", code);
		if(code > 0) {
			log = meetingService.getMeetingLog(meetings);
		} else {
			log="失败";
		}
		db.addLog("会诊中心>删除会诊:"+log, userService.getLoginInfo(request));
		try {
			response.getOutputStream().print(data.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * @RequestMapping("/ResourceManage") public ModelAndView ResourceManage(){
	 * ModelAndView mv = new ModelAndView("ResourceManage"); return mv; }
	 */
	@RequestMapping("/MeetingDiskManage")
	public ModelAndView MeetingDiskManage(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("MeetingDiskManage");
		Map<String, Object> loginInfo = userService.getLoginInfo(request);
		mv.addObject("user", loginInfo);
		Folder rootFolder = folderService.queryRootFolder(request);
		mv.addObject("rootFolder",rootFolder);
		mv.addObject("maxUploadSize",folderService.querySysConfValue(SysConfig.MAX_UPLOAD_SIZE.getKey()));
		mv.addObject("diskSize", folderService.querySysConfValue(SysConfig.DISK_SIZE.getKey()));
		return mv;
	}
	@RequestMapping("/MeetingTmpManage")
	public ModelAndView MeetingTmpManage(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("MeetingTmpManage");
		Map<String, Object> loginInfo = userService.getLoginInfo(request);
		mv.addObject("user", loginInfo);
		Folder rootFolder = folderService.queryRootFolder(request);
		mv.addObject("rootFolder",rootFolder);
		mv.addObject("maxUploadSize",folderService.querySysConfValue(SysConfig.MAX_UPLOAD_SIZE.getKey()));
		mv.addObject("diskSize", folderService.querySysConfValue(SysConfig.DISK_SIZE.getKey()));
		return mv;
	}

	@RequestMapping("/Personal")
	public ModelAndView Personal(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("Personal");
		Map<String,Object> form=userService.getLoginInfo(request);
		form = userService.getUserByUser(form.get("user").toString());
		form.put("phone", form.get("phone").toString().substring(12));
		form.put("sex"+form.get("sex"), "checked");
		form.put("videoRole_"+form.get("videoRole"), "checked");
		mv.addObject("form",form);
		return mv;
	}

	@RequestMapping("/PersonalSave")
	public ModelAndView PersonalSave(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("json");
		Map<String,Object> result=new HashMap<String,Object>();
		Map<String,Object> userinfo=userService.getLoginInfo(request);
		String fields[]={"name","avatar","sex","job","special","phone","email", "videoRole"};
		for(String f:fields){
			userinfo.put(f, request.getParameter(f));
		}

		//判断邮箱名已存在
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("email", request.getParameter("email"));
		Map<String,Object> debarMap = new HashMap<String,Object>();
		debarMap.put("id", userinfo.get("id"));
		if(db.isExists("user", paramMap,debarMap)) {
			userinfo.put("code", -2);
		} else {
			userinfo.put("phone", "AC5615684452"+userinfo.get("phone"));
			System.out.println(userinfo.get("phone").toString()+"------------------");
			int code=userService.updateUser(userinfo);
			userService.updateLogin(request);
			userinfo.put("code",code);
		}

		String json=JSON.toJSONString(userinfo);
		mv.addObject("json", json);
		db.addLog("个人中心>修改", userService.getLoginInfo(request));
		return mv;
	}

	@RequestMapping("/PasswordForm")
	public ModelAndView PasswordForm(){
		ModelAndView mv = new ModelAndView("PasswordForm");
		return mv;
	}
	
	//个人中心-》修改密码
	@RequestMapping("/PasswordFormPost")
	public ModelAndView PasswordFormPost(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("json");
		Map<String,Object> result=new HashMap<String,Object>();
		Map<String,Object> userinfo=userService.getLoginInfo(request);
		String oldpassword=MD5Utils.string2MD5(request.getParameter("oldpassword"));
		int code=0;
		if(oldpassword.equals(userinfo.get("password"))){
			userinfo.put("password", MD5Utils.string2MD5(request.getParameter("password")));
			if(userService.updateUser(userinfo)>0){
				userService.updateLogin(request);
				code=0;

				try {
			    	if(userinfo!=null && userinfo.containsKey("phone") && userinfo.get("phone")!=null) {
						String toPhones = userinfo.get("phone").toString();
						String toPhone = toPhones.substring(12);
						String[] params=new String[]{
								userinfo.get("user").toString(),
						};
						asyncService.sendSms(toPhone, "SMS_TEMP_10", params);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			else{
				code=2;
			}
		}
		else{
			code=1;
		}
		userinfo.put("code",code);
		String json=JSON.toJSONString(userinfo);
		db.addLog("修改密码", userService.getLoginInfo(request));
		mv.addObject("json", json);
		return mv;
	}

	//第一次修改密码
	@RequestMapping("/PasswordFormPosts")
	@ResponseBody
	public String PasswordFormPosts(String username,String oldpassword,String password){
		String rule = PassWordCheck.checkPasswordRule(password);
		if(rule.equals("密码长度不符合规则")) return "2";
		if(rule.endsWith("密码过于简单")) return "4";
		User users = userService.findUserList(username);
		if(users != null){
			String passwords = MD5Utils.string2MD5(oldpassword);//旧密码加密
			if(passwords.equals(users.getPassword())){//加密过后对比数据库中的旧密码
				String string2md5 = MD5Utils.string2MD5(password);//新密码加密
				userService.updatePassword(string2md5,users.getId());//将加密的新密码修改数据库
				try {
					String toPhones = users.getPhone();
					String toPhone = toPhones.substring(12);
					String[] params=new String[]{
							users.getUser(),
					};
					asyncService.sendSms(toPhone, "SMS_TEMP_10", params);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "1";
			}
		}
			return "3";


	}

	@RequestMapping("/Login")
	public ModelAndView Login(HttpServletRequest resquest){
		ModelAndView mv = new ModelAndView("Login");
		Cookie[] cookies=resquest.getCookies();
		Map cook=new HashMap<String,Object>();
		if(cookies!=null){
			for(Cookie cookie:cookies){
			    cook.put(cookie.getName(), cookie.getValue());
			}
		}
		mv.addObject("cookies", cook);
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		return mv;
	}

	@RequestMapping("/Logout")
	public ModelAndView Logout(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mv = new ModelAndView("Logout");
		request.getSession().invalidate();
		return mv;
	}

	@RequestMapping("/LoginPost")
	public ModelAndView LoginPost(HttpServletRequest request,HttpSession session,HttpServletResponse response,@RequestBody String data) throws IOException {
		System.out.println("******************"+data);
		//登录接口rsa验签
		String user = "";
		String password = "";
		String remember = "";
		String type = "";
		String code = "";
		Map<String,String> keyMap = LoginRsaValid.valid(data);
		if (keyMap != null && !keyMap.isEmpty()){
			user = keyMap.get("username") == null ? keyMap.get("user") : keyMap.get("username");
			password = keyMap.get("password");
			type = keyMap.get("type");
			code = keyMap.get("code") == null ? "" : keyMap.get("code");
		}

		ModelAndView mv = new ModelAndView("json");
		String codeName = session.getAttribute("CODE_TEST").toString();
		Map<String,Object>result=new HashMap<String,Object>();
		if(!code.equalsIgnoreCase(codeName)){
			result.put("resType","3");
			String json=JSON.toJSONString(result);
			mv.addObject("json", json);
			return mv;
		}
		User users = userService.findUserLoginTypes(user);


		boolean ok=userService.Login(user, password,remember, request,response);
		boolean boolType = false;
		String resType = "0";
		if(!"".equals(type) && null != type){
			if("1".equals(type) && "admin".equals(user)){//admin
			    boolType = true;
            }else if("2".equals(type) && "root".equals(user)){//root
                boolType = true;
            }else if("3".equals(type) && !"admin".equals(user)){
                boolType = true;
            }
		}
        if(boolType){
            if(ok){
            	String codeStr = GenerateCodeUtil.generateCode();
				request.getSession().setAttribute("CODE_TEST", codeStr);
                Map login=userService.getLoginInfo(request);
                db.addLog("登录", login);
                Cookie cookie;
//                if(remember.equals("1")){
//                    cookie= new Cookie("lastloginuser",user);
//                    cookie.setMaxAge(3600*24*30*12);
//                }
//                else{
//                    cookie= new Cookie("lastloginuser",null);
//                    cookie.setMaxAge(0);
//                }
//                response.addCookie(cookie);

                try {
                    if(login!=null && login.containsKey("phone") && login.get("phone")!=null) {
                        String toPhones = login.get("phone").toString();
                        String toPhone = toPhones.substring(12);
                        String[] params=new String[]{
                                user
                        };
                        asyncService.sendSms(toPhone, "SMS_TEMP_9", params);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            ok = false;
            resType = "1";
        }

		if(users!=null){
			Integer logintype = users.getLogintype();
			result.put("logintype", logintype);
		}

		result.put("user", user);
		result.put("status", ok);
		result.put("resType",resType);
		String json=JSON.toJSONString(result);
		mv.addObject("json", json);
		return mv;
	}

	//update by and
	@RequestMapping("/fronCheckLogined")

	public void fronCheckLogined(HttpServletRequest request,HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		Map<String,Object> user=userService.getLoginInfo(request);
		String name = user.get("user").toString();
		String nameMsg = UserMemoryData.getSessionIDMap().get(name);
		if(StringUtils.isNotBlank(nameMsg)) {
			//HttpSession session = (HttpSession)UserMemoryData.USR_SESSION.get(name);
			//session.setAttribute(name, "用户"+name+"已被删除");
			request.getSession().setAttribute("msg", "用户"+name+"已被删除");
		}
		//取得该用户的session中得msg值，如果不等于null代表已被别人登陆
		String str  = (String)request.getSession().getAttribute("msg");
		out.print(str == null ? "normal" : str);
		out.flush();
		out.close();
	}

	@RequestMapping("/CheckLogined")
	public void CheckLogined(HttpServletRequest request,HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		//取得该用户的session中得msg值，如果不等于null代表已被别人登陆
		String str  = (String)request.getSession().getAttribute("msg");
		out.print(str == null ? "normal" : str);
		out.flush();
		out.close();
	}

	@RequestMapping("/PasswordReset")
	public ModelAndView PasswordReset(){
		ModelAndView mv = new ModelAndView("PasswordReset");
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		return mv;
	}

	@RequestMapping("/MeetingForm")
	public ModelAndView MeetingForm(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("MeetingForm");
		Map<String,Object> form=new HashMap<String,Object>();

		Map<String,Object> login=userService.getLoginInfo(request);
		Map<String,Object> user=new HashMap<String,Object>();
		user.put("value", login.get("user"));
		form.put("user", user);

		Map<String,Object> username=new HashMap<String,Object>();
		username.put("value", login.get("name"));
		form.put("username", username);

		Map<String,Object> type=new HashMap<String,Object>();
		List<Map> typeItems=db.getDictItems("meetingtype", 0);
		type.put("items", typeItems);
		form.put("type", type);

		Map<String,Object> attend=new HashMap<String,Object>();
		List<Map>attends=db.getAttends((int)login.get("id"));
		attend.put("items", attends);
		form.put("attend", attend);

		mv.addObject("form", form);
		return mv;
	}

	@RequestMapping("/MeetingFormPost")
	public ModelAndView MeetingFormPost(HttpServletRequest request){
		ModelAndView mv = new ModelAndView("json");
		Map<String,Object> data=new HashMap<String,Object>();
		String[] fields={"stime","etime","topic","type","attends","attaId"};
		for(String f:fields){
			data.put(f, request.getParameter(f));
		}
		Map<String,Object> login=userService.getLoginInfo(request);
		data.put("user", login.get("id"));
		String temp_str="";

		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    temp_str=sdf.format(dt);

		String no="M"+temp_str;
		data.put("no", no);
		int result=db.AddMeeting(data);
		this.folderService.createMeetingFolder(request, no, request.getParameter("attaId"));


		//提出会诊申请
		try {
			List<Map> adminUsers=userService.findByGroups("admins");
			for (Map user : adminUsers) {
				try {
					if(user!=null && user.containsKey("phone") && user.get("phone")!=null) {
						String toPhones = user.get("phone").toString();
						String toPhone = toPhones.substring(12);
						String[] params=new String[]{
								login.get("user").toString()
						};
						asyncService.sendSms(toPhone, "SMS_TEMP_12", params);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		data.put("code", result);
		String json=JSON.toJSONString(data);
		mv.addObject("json", json);
		db.addLog("会诊中心>申请会诊", userService.getLoginInfo(request));
		return mv;
	}

	@RequestMapping("/MeetingAction")
	public void MeetingAction(int id,String action,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> user=userService.getLoginInfo(request);
		Map meeting=db.getMeeting(String.valueOf(id));
		int userId=(int) user.get("id");
		JSONObject result=new JSONObject();
		int code=0;

		code=meetingService.MeetingUserAction(meeting,action,userId);

		result.put("code", code);
		try {
			response.getOutputStream().print(result.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 发送邮件验证码
	 * @param email
	 * @param response
	 * @param request
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@RequestMapping("/sendEailCode")
	public void sendEailCode(String email,HttpServletResponse response,HttpServletRequest request,String ImgCode) throws IOException, GeneralSecurityException{
    	JSONObject json = new JSONObject();
		HttpSession session=request.getSession();
    	response.setCharacterEncoding("utf-8");
    	int code = (int)((Math.random()*9+1)*100000);
		String codeName = session.getAttribute("CODE_TEST").toString();
		if(!ImgCode.equalsIgnoreCase(codeName)){
			json.put("code", "3");
			response.getWriter().write(json.toString());
			return;
		}
        Map<String, Object> user = db.getByEmail(email);
        if (user == null) {
        	user = db.getByUser(email);
        	if (user == null) {
        		json.put("code", "-20");
        		json.put("data", "无效账号!");
        		response.getWriter().write(json.toString());
        		return;
			}else{
				email = user.get("email").toString();
			}
		}else {
			email = user.get("email").toString();
		}
    	String head = "远程会诊管理系统";
    	String text = "您的验证码为："+code;

		try {
			if(user!=null && user.containsKey("phone") && user.get("phone")!=null) {
				String toPhones = user.get("phone").toString();
				String toPhone = toPhones.substring(12);

				String[] params=new String[]{String.valueOf(code)};
				asyncService.sendSms(toPhone, "SMS_TEMP_3", params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	String emailAddress = this.folderService.querySysConfValue(SysConfig.EMAIL_ADDRESS.getKey());
		String emailPwd = this.folderService.querySysConfValue(SysConfig.EMAIL_AUTHORIZATION_CODE.getKey());
		json.put("code", "1");
		Map<String, Object> map = new HashMap<>();
		map.put("code", code);
		System.out.println(JSONObject.toJSONString(redisTemplate2.opsForValue().get(email)));
		if(null==redisTemplate2.opsForValue().get(email)||JSONObject.parseObject(redisTemplate2.opsForValue().get(email)).get("dataTime")==null){
			map.put("dataTime", System.currentTimeMillis());
			map.put("size",1);
		}else{
			map=JSONObject.parseObject(redisTemplate2.opsForValue().get(email),Map.class);
			map.put("code", code);
			//Map<String,Object> mapMap=(Map<String,Object>) session.getAttribute(email);
			if((System.currentTimeMillis()-Long.valueOf(map.get("dataTime").toString()))/1000/3600>=24){
				map.put("size",1);
				map.put("dataTime", System.currentTimeMillis());
			}else {
				Integer size = Integer.valueOf(map.get("size").toString());
				if (size == 10) {
					json.put("code", "2");
					response.getWriter().write(json.toString());
					return;
				} else {
					map.put("size", size + 1);
				}
			}
		}
		redisTemplate2.opsForValue().set(email,JSONObject.toJSONString(map));
		map.put("time", System.currentTimeMillis());
		map.put("code", code);
		session.setAttribute(email, map);
    	boolean result = SendEmail.send(email, head, text,emailAddress,emailPwd);
		if (result) {
			json.put("code", "1");
        } else {
            json.put("code", "-10");
        }
		response.getWriter().write(json.toString());
	}
	/**
	 * 忘记密码
	 * @param email
	 * @param code
	 * @param response
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/resetPassword")
	public ModelAndView resetPassword(String email,String code,HttpServletRequest request) throws IOException{
		ModelAndView mv = new ModelAndView("json");
		JSONObject json = new JSONObject();
		DecryptionUtil.validation(request);
		Map<String, Object> user = db.getByEmail(email);
		if (user == null) {
			user = db.getByUser(email);
			if (user == null) {
				json.put("code", "-20");
				json.put("data", "无效账号!");
				mv.addObject("json", json.toJSONString());
				return mv;
			}else{
				email = user.get("email").toString();
			}
		}
		Map<String, Object> map =JSONObject.parseObject(redisTemplate2.opsForValue().get(email),Map.class);
		Map<String, Object> mapSession = (Map<String, Object>) request.getSession().getAttribute(email);
		if (mapSession == null) {
			json.put("code", "-10");
			json.put("data", "验证码不正确！");
			mv.addObject("json", json.toJSONString());
			return mv;
		}
		long time = Long.valueOf(String.valueOf(mapSession.get("time"))).longValue();
		Long min = (System.currentTimeMillis() - time) / (1000 * 60);
		String checkCode = mapSession.get("code").toString();
		if (min < 60L) {
			if (code.equals(checkCode)) {
				//随机密码生成
				//String sbs = MD5Utils.getPsw(8);
				String sbs = (int) (100000 + Math.random() * (10000000 - 100000)) + "";
				db.updatePwdByEmail(email, MD5Utils.string2MD5(sbs));
				try {
					String emailAddress = this.folderService.querySysConfValue(SysConfig.EMAIL_ADDRESS.getKey());
					String emailPwd = this.folderService.querySysConfValue(SysConfig.EMAIL_AUTHORIZATION_CODE.getKey());
					SendEmail.send(email, "远程会诊管理系统", "重置密码成功！新密码为："+sbs,emailAddress,emailPwd);

					try {
						if(user!=null && user.containsKey("phone") && user.get("phone")!=null) {
							String toPhones = user.get("phone").toString();
							String toPhone = toPhones.substring(12);
							String[] params=new String[]{sbs};
							asyncService.sendSms(toPhone, "SMS_TEMP_2", params);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					json.put("code", "1");
					json.put("data", "操作成功，新密码已发送至邮箱！");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				json.put("code", "-12");
				json.put("data", "验证码错误！");
			}
		} else {
			json.put("code", "-11");
			json.put("data", "验证码已过期！");
		}
		mv.addObject("json", json.toJSONString());
		return mv;
	}

	@RequestMapping("/isLastLoginFlag")
	public ModelAndView isLastLoginFlag(HttpServletRequest request) throws IOException{
		ModelAndView mv = new ModelAndView("json");
		JSONObject json = new JSONObject();
		if(!userService.isLastLoginFlag(request)) {
			json.put("code", "2");
			json.put("data", "已在其它地方登录"+userService.getLoginInfo(request).toString());
		} else {
			json.put("code", "1");
		}
		mv.addObject("json", json.toJSONString());
		return mv;
	}


	/**
	 * 点击查看文件弹窗
	 * @param ids
	 * @param request
	 * @param response
	 */
	@RequestMapping("/pathform")
	public ModelAndView PathForm(HttpServletRequest request,
			Integer id,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("json");//返回到对应的jsp页面
		int status = 200;
		List<Map<String,Object>> datas =new ArrayList<>();//创建返回列表对象
		Map<String,Object> result = new HashMap<String,Object>();//创建返回Json对象
		JSONObject data=new JSONObject();
		List<Map> rows=getAttrByMeetingId(id);
		for (Map row : rows) {
			Map<String,Object> d = new HashMap<String,Object>();//创建返回对象
			if( row == null )
			{
				continue;
			}
			d.put("sysName", row.get("sysName"));
			d.put("fileName", row.get("fileName"));
			d.put("path", row.get("path"));
			d.put("id", row.get("id"));
			d.put("isShare", row.get("is_share"));

			String dcmURL = (String) row.get("dcmURL" );

			if( dcmURL != null && dcmURL.length() > 10  )
			{
				d.put("dcmURL", dcmURL);
			}

			datas.add(d);

		}
		result.put("data", datas);
		result.put("status", status);
		String json = JSON.toJSONString(result);//转化为json字符串
		mv.addObject("json", json);
		return mv;
	}

	@RequestMapping("/setsharefile")
	public ModelAndView setShareFile(HttpServletRequest request,
			Integer id,String fileId,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("SetShareFile");

		List<Map<String,Object>> users=new ArrayList<Map<String,Object>>();

		List<String> sids=new ArrayList<String>();
		Map amap=db.getAttr(fileId);
		if(amap.get("share_user")!=null){
			String[] sus=amap.get("share_user").toString().split(",");
			for (String su : sus) {
				sids.add(su);
			}
		}

		Map<String,Object> meeting = meetingService.getMeeting(id);
		String attendstr=meeting.get("attends").toString();
		String[] attends=attendstr.split(",");
		for (String uid : attends) {
			Map<String,Object> user=userService.getUserById(Integer.parseInt(uid));
			if(user!=null){
				if(sids.size()>0){
					if(sids.contains(String.valueOf(uid))){
						user.put("isck", "1");
					}else{
						user.put("isck", "0");
					}
				}else{
					user.put("isck", "1");
				}
				users.add(user);
			}
		}



		mv.addObject("fileId", fileId);
		mv.addObject("mid", id);
		mv.addObject("users", users);
		return mv;
	}




	public List<Map> getAttrByMeetingId(Integer id){
		List<Map> rows=new ArrayList<Map>();

	    Map<String,Object> meeting = meetingService.getMeeting(id);
		String attaId=meeting.get("attaId")!=null ?meeting.get("attaId").toString():"";
	    if(StringUtils.isNotEmpty(attaId)){
	    	if(attaId.endsWith(",")){
	    		attaId=attaId.substring(0, attaId.length()-1);
	    	}
	    	String[] aids=attaId.split(",");
	    	for (String aid : aids) {
	    		rows.add(db.getAttr(aid));
			}
	    }
		return rows;
	}


	@RequestMapping("/toLogin")
	public ModelAndView toLogin(HttpServletRequest resquest){
			ModelAndView mv = new ModelAndView("Login2");
		Cookie[] cookies=resquest.getCookies();
		Map cook=new HashMap<String,Object>();
		if(cookies!=null){
			for(Cookie cookie:cookies){
			    cook.put(cookie.getName(), cookie.getValue());
			}
		}
		mv.addObject("cookies", cook);
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		return mv;
	}


	@RequestMapping("toupdatepssword")
	public String toupdatepssword(String username,Model model){
		model.addAttribute("username", username);
		System.out.println(username);
		return "LoginPasswordForm";
	}

	/*@RequestMapping("CodeTest")
	@ResponseBody
	public void codetest1(HttpServletRequest request,HttpServletResponse response){
		CodeTest codeTest = new CodeTest();
		codeTest.get(request, response);
	}*/

	@RequestMapping("CodeTest")
	@ResponseBody
	public String CodeTest(HttpServletRequest request)throws Exception{
		String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < 4; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        request.getSession().setAttribute("CODE_TEST", val);
        return RsaUtil.encrypt(val);
       /* VerifyCode verifyCode=new VerifyCode();
        String UUID=UUID.randomUUID().toString()+".jpg";
        verifyCode.output(verifyCode.getImage(), new FileOutputStream(url));
        HttpSession session=request.getSession();
        session.setAttribute("CODE_TEST", verifyCode.getText());
        *//*if(null==session.getAttribute("verifyCodeUrl")){
            List<String> list=new ArrayList<>();
            list.add(url);
        }else{
            List<String> list=(List<String>)session.getAttribute("verifyCodeUrl");
            list.add(url);
        }*//*
		String base64=FileUtil.ImageToBase64(url);
		File file=new File(url);
        FileUtil.deleteAllFilesOfDir(file);
		//System.out.println(FileUtil.deleteFile(url));
		return base64;*/
	}

}
