package com.va.removeconsult.controller.frontend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSON;
import com.va.removeconsult.dto.AjaxResult;
import com.va.removeconsult.pojo.SelectPojo;
import com.va.removeconsult.service.AsyncService;
import com.va.removeconsult.service.DbService;
import com.va.removeconsult.service.MeetingService;
import com.va.removeconsult.service.UserService;
import com.va.removeconsult.util.ErrorMsgEnum;
import com.va.removeconsult.websocket.MyWebSocketHandler;

/**
 * 会诊工具条的控制器 (1) 抓拍视频功能 (2) 邀请专家 以及 专家确认同意或拒绝 (3) 与会人申请当主持人 以及 主持人确认同意或拒绝
 */
@RequestMapping("/MeetingTools")
@Controller
public class MeetingToolsController {
	@Autowired
	private UserService userService;
	@Autowired
	private MeetingService meetingService;
	@Autowired
	private MyWebSocketHandler myWebSocketHandler;
	@Resource
	private DbService db;
	// @Autowired
	// private EmailService emailService;
	@Autowired
	private AsyncService asyncService;

	Logger logger = LoggerFactory.getLogger(MeetingToolsController.class);

	// 会诊最大人数限制
	private final int MEETING_PEOPLE_LIMIT = 8;

	/**
	 * 与会人申请当主持人
	 * 
	 * @param request
	 * @param meetingId 会诊id
	 * @param applyUser 申请人id
	 * @return
	 */
	@RequestMapping(value = "/applyHost", method = RequestMethod.POST)
	public @ResponseBody AjaxResult applyHost(HttpServletRequest request, int meetingId) {

		try {
			// 获取当前用户信息
			Map<String, Object> user = userService.getLoginInfo(request);
			if (MapUtils.isEmpty(user)) {
				return AjaxResult.error(ErrorMsgEnum.USER_REPLACE.getMsg());
			}
			String loginId = (String) user.get("user");
			int userId = (int) user.get("id");

			// 获取当前会诊主持人信息
			Map<String, Object> meeting = meetingService.getMeeting(meetingId);
			if (MapUtils.isEmpty(meeting))
				return AjaxResult.error(ErrorMsgEnum.NOT_FIND_MEETING.getMsg());
			int meetingUserId = (int) meeting.get("user");
			Map<String, Object> meetingUser = userService.getUserById(meetingUserId);
			if (MapUtils.isEmpty(meetingUser))
				return AjaxResult.error(ErrorMsgEnum.NOT_FIND_MEETING_USER.getMsg());

			// 向当前会诊主持人发送申请消息
			Map<String, String> msgDataMap = new HashMap<String, String>();
			msgDataMap.put("type", "applyHost");
			msgDataMap.put("meetingId", "" + meetingId);
			msgDataMap.put("fromUser", "" + loginId);
			msgDataMap.put("fromUserId", "" + userId);
			String applyMsg = JSON.toJSONString(msgDataMap);
			TextMessage textMessage = new TextMessage(applyMsg);
			String sendResultMsg = myWebSocketHandler.sendMessageToUserForMeeting(meetingUser.get("user").toString(),
					textMessage, "applyHost", String.valueOf(meetingId));
			if ("true".equals(sendResultMsg)) {
				return AjaxResult.success("发送成功");
			} else {
				return AjaxResult.error(sendResultMsg);
			}
		} catch (Exception e) {
			logger.error("applyHost func error " + e.getMessage(), e);
			return AjaxResult.error(ErrorMsgEnum.UNKNOWN_ERROR.getMsg());
		}

	}

	/**
	 * 主持人确认转让 如果同意则主持人变成非主持人，申请者变成主持人，2人的界面替换并刷新。 如果不同意则推送一条消息通知申请者
	 * 
	 * @param request
	 * @param meetingId 当前会诊id
	 * @param toUser    申请者
	 * @param toUserId  申请者id
	 * @return
	 */
	@RequestMapping(value = "/confirmTransfer", method = RequestMethod.POST)
	public @ResponseBody AjaxResult acceptPresenter(HttpServletRequest request, int isAssent, int meetingId,
			String toUser, int toUserId) {
		StringBuffer sbOthers = new StringBuffer();
		// 如果主持人同意转让
		if (isAssent == 1) {
			// 获取当前会诊
			Map<String, Object> meeting = meetingService.getMeeting(meetingId);
			// 获取当前主持人
			Map<String, Object> user = userService.getLoginInfo(request);
			int userId = (int) user.get("id");
			// 获取当前会诊的与会者们
			String attends = (String) meeting.get("attends");
			String[] split = attends.split(",");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < split.length; i++) {
				String appendData = "";
				// 如果申请者存在会诊中
				if (split[i].equals("" + toUserId)) {
					// 将主持人换为与会者
					appendData = "" + userId;
				} else {
					appendData = split[i];
					sbOthers.append(split[i] + ",");
				}
				if (i != split.length - 1) {
					sb.append(appendData + ",");
				} else {
					sb.append(appendData);
				}
			}
			// 更新此会诊的主持人为申请者
			// 原先的主持人变为与会者
			meetingService.updateMeetingPresenter(toUserId, sb.toString(), meetingId);
		}
		// 发送信息给转让人
		Map<String, String> msgDataMap = new HashMap<String, String>();
		msgDataMap.put("type", "confirmTransfer");
		msgDataMap.put("isAssent", Integer.toString(isAssent));
		msgDataMap.put("applyUserId", Integer.toString(toUserId));
		String transferMsg = JSON.toJSONString(msgDataMap);
		TextMessage textMessage = new TextMessage(transferMsg);
		String sendResultMsg = myWebSocketHandler.sendMessageToUserForMeeting(toUser, textMessage, "confirmTransfer",
				String.valueOf(meetingId));

		// 通知其余与会人主持人已变更
		if (sbOthers.length() > 0) {
			sbOthers.delete(sbOthers.length() - 1, sbOthers.length());
			List<Map> otherAttends = meetingService.getAttends(sbOthers.toString());
			msgDataMap = new HashMap<String, String>();
			msgDataMap.put("type", "updatePresenter");
			msgDataMap.put("presenter", String.valueOf(toUserId));
			for (Map item : otherAttends) {
				myWebSocketHandler.sendMessageToUserForMeeting(item.get("user").toString(), textMessage,
						"updatePresenter", String.valueOf(meetingId));
			}
		}

		if ("true".equals(sendResultMsg)) {
			return AjaxResult.success("发送成功");
		} else {
			return AjaxResult.error(sendResultMsg);
		}
	}

	/**
	 * 主持人邀请 只有主持人才能邀请专家，非会诊主持人没有该权限。 被邀请的专家一定是除目前会诊与会人之外的专家。
	 * 若被邀请专家的客户端处于登录状态，则在被邀请专家的客户端弹出：“xxx要求您参加会诊”“同意+拒绝”提示框。 被邀请专家点击同意按钮，之后加入会诊；
	 * 如果点击拒绝，在邀请方的客户端弹出“对方已拒绝”。 若被邀请专家的客户端处于非登录状态，则在待加入的会诊列表里边添加一条待参加的会诊。
	 * 同时给被邀请人发送一个邮件。 并提示邀请方，被邀请方不在线。
	 * 
	 * @param request
	 * @param meetingId
	 * @param toUsers
	 * @return
	 */
	@RequestMapping(value = "/inviteExpert", method = RequestMethod.POST)
	public @ResponseBody AjaxResult inviteExpert(HttpServletRequest request, int meetingId, String toUser) {
		System.out.println("会诊id = " + meetingId + " 专家名: " + toUser);

		// 获取当前会诊
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);

		// 获取当前登录User
		Map<String, Object> user = userService.getLoginInfo(request);
		String loginId = (String) user.get("user");
		int userId = (int) user.get("id");

		// 推送消息给专家
		Map<String, String> msgDataMap = new HashMap<String, String>();
		msgDataMap.put("type", "inviteExpert");
		msgDataMap.put("meetingId", "" + meetingId); // 当前会诊id
		msgDataMap.put("fromUser", loginId); // 邀请人名称
		msgDataMap.put("fromUserId", "" + userId); // 邀请人id
		String transferMsg = JSON.toJSONString(msgDataMap);
		TextMessage textMessage = new TextMessage(transferMsg);

		String attends = (String) meeting.get("attends"); // 参与会诊人员
		// 默认当前会诊人数1人
		int currentMeetingPeopNum = 1;
		// 当前会诊人数
		if (attends.indexOf(",") != -1) {
			currentMeetingPeopNum += attends.split(",").length;
			// 等于8人，说明会诊满员了
			if (currentMeetingPeopNum >= MEETING_PEOPLE_LIMIT) {
				return AjaxResult.error("会诊人数最多只能" + MEETING_PEOPLE_LIMIT + "人");
			}
		}
		logger.info("未邀请专家时，当前会诊人数 = " + currentMeetingPeopNum);
		// 如果toUser参数中有逗号,说明有多个专家需要邀请
		if (toUser.indexOf(",") != -1) {
			boolean flag = true;
			StringBuilder errorMsg = new StringBuilder();
			String[] expertUsers = StringUtils.split(toUser, ",");
			if ((expertUsers.length + currentMeetingPeopNum) > MEETING_PEOPLE_LIMIT) {
				return AjaxResult.error("当前会诊人数" + currentMeetingPeopNum + ",会诊人数最多只能" + MEETING_PEOPLE_LIMIT + "人");
			}
			StringBuffer sb = null;
			// 获取当前会诊的与会者们
			Object o = meeting.get("attends");
			// 消息推送给专家
			for (String expertName : expertUsers) {
				Map<String, Object> expertUser = userService.getUserByUser(expertName);
				String sendResultMsg = myWebSocketHandler.sendMessageToUserForMeeting(expertName, textMessage,
						"inviteExpert", String.valueOf(meetingId));

				if (!"true".equals(sendResultMsg)) {
					errorMsg.append("【").append(expertUser.get("name").toString()).append("】").append(sendResultMsg)
							.append("<br/>");
					// 若被邀请专家的客户端处于非登录状态，则在待加入的会诊列表里边添加一条待参加的会诊。

					if (sb == null) {
						sb = new StringBuffer("");
						// 将专家加入会诊中
						if (o != null) {
							sb.append(o.toString()).append(",").append(expertUser.get("id").toString());
						} else {
							sb.append(expertUser.get("id").toString());
						}
					} else {
						sb.append(",").append(expertUser.get("id"));
					}
					// 同时给被邀请人发送一个邮件。并提示邀请方，被邀请方不在线。
					meetingService.sendMailByInviteExpert(meeting, expertUser.get("name").toString(),
							user.get("name").toString(), expertUser.get("email").toString(),expertUser.get("phone").toString());
					flag = false;
				} else {
					errorMsg.append("【").append(expertName).append("】").append("已通知").append("<br/>");
				}
			}
			if (flag) {

				return AjaxResult.success("发送成功");
			} else {
				if (sb != null) {
					// 更新会诊与会者列表
					System.out.println(userId + " update " + sb.toString() + " meeting = " + meetingId);
					meetingService.updateMeetingPresenter(userId, sb.toString(), meetingId);
				}

				return AjaxResult.error(errorMsg.toString());
			}
		} else {
			if ((1 + currentMeetingPeopNum) > MEETING_PEOPLE_LIMIT) {
				return AjaxResult.error("当前会诊人数" + currentMeetingPeopNum + ",会诊人数最多只能" + MEETING_PEOPLE_LIMIT + "人");
			}
			String sendResultMsg = myWebSocketHandler.sendMessageToUserForMeeting(toUser, textMessage, "inviteExpert",
					String.valueOf(meetingId));
			if ("true".equals(sendResultMsg)) {
				return AjaxResult.success("发送成功");
			} else {
				Map<String, Object> expertUser = userService.getUserByUser(toUser);
				// 获取当前会诊的与会者们
				Object o = meeting.get("attends");
				StringBuffer sb = null;
				// 将专家加入会诊中
				if (o != null) {
					sb = new StringBuffer(o.toString()).append(",").append(expertUser.get("id"));
				} else {
					sb = new StringBuffer(expertUser.get("id").toString());
				}
				// 更新会诊与会者列表
				meetingService.updateMeetingPresenter(userId, sb.toString(), meetingId);
				meetingService.sendMailByInviteExpert(meeting, expertUser.get("name").toString(),
						user.get("name").toString(), expertUser.get("email").toString(),expertUser.get("phone").toString().substring(12));
				return AjaxResult.error(sendResultMsg);
			}
		}
	}

	/**
	 * 专家确认加入会诊 如果同意则专家加入会诊,主持人收到通知 如果不同意则推送一条消息通知主持人
	 * 
	 * @param request
	 * @param meetingId 当前会诊id
	 * @param toUser    主持人名称
	 * @param toUserId  主持人id
	 * @return
	 */
	@RequestMapping(value = "/confirmInvite", method = RequestMethod.POST)
	public @ResponseBody AjaxResult confirmInvite(HttpServletRequest request, int isAssent, int meetingId,
			String toUser, int toUserId) {

		System.out.println(meetingId + " toUser = " + toUser + " --- toUserId = " + toUserId);

		// 获取当前会诊
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		// 获取当前专家
		Map<String, Object> user = userService.getLoginInfo(request);
		int userId = (int) user.get("id");

		// 如果专家同意邀请
		if (isAssent == 1) {
			// 获取当前会诊的与会者们
			Object o = meeting.get("attends");
			StringBuffer sb = null;
			// 将专家加入会诊中
			if (o != null) {
				sb = new StringBuffer(o.toString()).append(",").append(userId);
			} else {
				sb = new StringBuffer(userId);
			}
			// 更新会诊与会者列表
			meetingService.updateMeetingPresenter(toUserId, sb.toString(), meetingId);
		}
		// 发送信息给主持人
		Map<String, String> msgDataMap = new HashMap<String, String>();
		msgDataMap.put("type", "confirmInvite");
		msgDataMap.put("isAssent", Integer.toString(isAssent));
		msgDataMap.put("inviteUserId", Integer.toString(toUserId)); // 主持人
		msgDataMap.put("userId", "" + userId);// 专家信息
		msgDataMap.put("userName", user.get("name").toString());// 专家信息
		String transferMsg = JSON.toJSONString(msgDataMap);
		TextMessage textMessage = new TextMessage(transferMsg);
		// 专家同不同意邀请都推送通知给主持人
		String sendResultMsg = myWebSocketHandler.sendMessageToUserForMeeting(toUser, textMessage, "confirmInvite",
				String.valueOf(meetingId));
		if ("true".equals(sendResultMsg)) {
			return AjaxResult.success("发送成功");
		} else {
			return AjaxResult.error(sendResultMsg);
		}
	}

	/**
	 * 重新加载专家列表
	 * 
	 * @param request
	 * @param meetingId 当前会诊id
	 * @return
	 */
	@RequestMapping(value = "/reloadExpertList", method = RequestMethod.POST)
	public @ResponseBody AjaxResult reloadExpertList(HttpServletRequest request, int meetingId) {

		// 获取当前会诊
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);

		// 获取当前会诊所有人员列表
		String userStr = (String) meeting.get("attends");
		StringBuilder sb = new StringBuilder(userStr);
		sb.append(",").append(meeting.get("user"));
		List<Map> expertsList = meetingService.getAllPeople(sb.toString(), null);
		AjaxResult ajaxResult = new AjaxResult();
		ajaxResult.put("msg", "加载专家列表成功");
		ajaxResult.put("code", 0);
		ajaxResult.put("expertList", expertsList);
		return ajaxResult;
	}

	/**
	 * 重新加载邀请专家的下拉框列表
	 * 
	 * @param request
	 * @param meetingId 当前会诊id
	 * @return
	 */
	@RequestMapping(value = "/reloadExpertSelectList", method = RequestMethod.GET)
	public @ResponseBody AjaxResult reloadExpertSelectList(HttpServletRequest request, int meetingId) {

		// 获取当前会诊
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);

		// 专家列表
		String userStr = (String) meeting.get("attends");
		StringBuilder sb = new StringBuilder(userStr);
		sb.append(",").append(meeting.get("user"));
		List<Map> expertsList = meetingService.getExperts(sb.toString(), null);

		List<SelectPojo> datas = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(expertsList)) {
			for (Map map : expertsList) {
				SelectPojo selectPojo = new SelectPojo();
				selectPojo.setName(map.get("name").toString());
				selectPojo.setValue(map.get("user").toString());
				datas.add(selectPojo);
			}
		}
		AjaxResult ajaxResult = new AjaxResult();
		ajaxResult.put("msg", "重新加载邀请专家下拉框列表成功");
		ajaxResult.put("code", 0);
		ajaxResult.put("data", datas);
		return ajaxResult;
	}

	/**
	 * 重新加载与会人列表
	 * 
	 * @param meetingId 当前会诊id
	 */
	@RequestMapping(value = "/reloadAttendList")
	public @ResponseBody AjaxResult reloadAttendList(int meetingId) {
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		String userStr = (String) meeting.get("attends");
		List<Map> userList = meetingService.getAllPeople(userStr, null);

		List<SelectPojo> datas = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(userList)) {
			for (Map map : userList) {
				SelectPojo selectPojo = new SelectPojo();
				selectPojo.setName(map.get("name").toString());
				selectPojo.setValue(map.get("user").toString());
				datas.add(selectPojo);
			}
		}
		
		AjaxResult ajaxResult = new AjaxResult();
		ajaxResult.put("msg", "加载与会人列表成功");
		ajaxResult.put("code", 0);
		ajaxResult.put("data", datas);
		return ajaxResult;
	}
	
	 /**
     * 抓拍
     * @param request  
     * @return
     *//*
    @RequestMapping(value="/cutPic",method=RequestMethod.POST)
    public @ResponseBody AjaxResult cutPic(HttpServletRequest request,int meetingId,int x,int y,int width,int height,String fileName,String fileType){
    	Map<String, Object> meeting = meetingService.getMeeting(meetingId);
    	int userId = (int) meeting.get("user");
    	Map<String, Object> currentUser = userService.getUserById(userId);
    	String number = (String) meeting.get("no");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
    	Date dt = new Date(); 
	    String temp_str=sdf.format(dt);   
        String basePath = request.getRealPath("/")+number;
        if(!StringUtils.isNotBlank(fileName)) {
        	fileName = temp_str;
        }
        if(!StringUtils.isNotBlank(fileType)) {
        	fileType= "jpg";
        }
        return meetingService.ScreenshotImg(request,fileName,basePath, fileType,x,y,width,height,currentUser,number);
    }*/
	
	/**
     * 抓拍
     * @param request  
     * @return
     */
    @RequestMapping(value="/cutPic",method=RequestMethod.POST)
    public @ResponseBody AjaxResult cutPic(HttpServletRequest request,int meetingId,String data,String fileName,String fileType,double ratio){
    	Map<String, Object> meeting = meetingService.getMeeting(meetingId);
    	int userId = (int) meeting.get("user");
    	Map<String, Object> currentUser = userService.getUserById(userId);
    	String number = (String) meeting.get("no");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
    	Date dt = new Date(); 
	    String temp_str=sdf.format(dt);   
	    String basePath = request.getSession().getServletContext()  
                .getRealPath("/")+number;  
	    System.out.println("*************************basePath="+basePath);
        if(!StringUtils.isNotBlank(fileName)) {
        	fileName = temp_str;
        }
        if(!StringUtils.isNotBlank(fileType)) {
        	fileType= "jpg";
        }
        return meetingService.ScreenshotImg(request,currentUser,fileName,basePath, fileType,number, data,ratio);
    }
	
}
