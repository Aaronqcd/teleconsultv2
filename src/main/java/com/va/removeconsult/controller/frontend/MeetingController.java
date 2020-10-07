package com.va.removeconsult.controller.frontend;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.clouddisk.util.ServerTimeUtil;
import com.va.removeconsult.dto.AjaxResult;
import com.va.removeconsult.pojo.RecordVideo;
import com.va.removeconsult.service.AsyncService;
import com.va.removeconsult.service.DbService;
import com.va.removeconsult.service.MeetingService;
import com.va.removeconsult.service.RecordService;
import com.va.removeconsult.service.UserService;
import com.va.removeconsult.util.SendEmail;
import com.va.removeconsult.websocket.MyWebSocketHandler;

import web.util.Helper;

@Controller
public class MeetingController {
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

	@Autowired
	private FolderService folderService;

	@Autowired
	private RecordService recordService;

	@RequestMapping("/Meeting")
	public ModelAndView Meeting(HttpServletRequest request, int meetingId) {
		ModelAndView mv = new ModelAndView("Meeting");
		Map<String, Object> user = userService.getLoginInfo(request);
		int userId = (int) user.get("id");
		String loginId = (String) user.get("user");

		Map<String, Object> currentUserMap = userService.getUserByUser(user.get("user").toString());
		int currentOrgId = (int) currentUserMap.get("belong");
		mv.addObject("currentOrgId", currentOrgId);
		mv.addObject("loginId", loginId);
		mv.addObject("currentMeetingId", meetingId);
		String isteudborad = folderService.querySysConfValue(SysConfig.CONF_TEUDBORAD.getKey());
		mv.addObject("isteudborad", isteudborad);
		
		String basePath;
		if(request.getServerPort() == 80 || request.getServerPort() == 443 ) {
			basePath = request.getServerName()+request.getContextPath();
		}else {
			basePath = request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		}
		mv.addObject("basePath", basePath);

		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		mv.addObject("meetingInfo", meeting);
		Date startTime = (Date) meeting.get("stime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mv.addObject("meetingStartTime", sdf.format(startTime));
		// 若当前用户与会诊发起者是同一个人，则返回true 当主持人发起会议时记录会议信息
		if ((int) meeting.get("user") == userId) {
			mv.addObject("isPresenter", true);
			Map<String, Object> meetingCound = db.getMeetingCount(meeting.get("no").toString());
			if(meetingCound == null){
				meetingCound = new HashMap<String, Object>(); 
				meetingCound.put("code", meeting.get("no"));
				meetingCound.put("type", 0);
				meetingCound.put("stear_time",ServerTimeUtil.getDateTime());
				db.AddMeetingCode(meetingCound);
			}
		} else {
			mv.addObject("isPresenter", false);
		}

		String attendsStr = (String) meeting.get("attends");
		List<Map> attendsList = meetingService.getAttends(attendsStr);
		mv.addObject("attends", attendsList);

		// 专家列表
		String userStr = (String) meeting.get("attends");
		StringBuilder sb = new StringBuilder(userStr);
		sb.append(",").append(meeting.get("user"));
		List<Map> expertsList = meetingService.getExperts(sb.toString(), null);
		mv.addObject("experts", expertsList);

		// 与会人名称
		String[] attendsNames = new String[attendsList.size()];
		for (int i = 0; i < attendsList.size(); i++) {
			attendsNames[i] = (String) attendsList.get(i).get("name");
		}
		String attendsNamesStr = StringUtils.join(attendsNames, ",");
		mv.addObject("attendsNames", attendsNamesStr);

		// 已签到的人员
		String attendacceptsStr = (String) meeting.get("attendaccepts");
		attendacceptsStr = attendacceptsStr + "," + userId;
		List<Map> attendacceptsList = meetingService.getAttends(attendacceptsStr);
		String attendacceptsNameStr = new String();
		for (Map attendacceptUser : attendacceptsList) {
			attendacceptsNameStr = attendacceptsNameStr + (String) attendacceptUser.get("name") + ",";
		}
		attendacceptsNameStr = attendacceptsNameStr.length() > 0
				? attendacceptsNameStr.substring(0, attendacceptsNameStr.length() - 1)
				: attendacceptsNameStr;
		mv.addObject("attendacceptsName", attendacceptsNameStr);

		// 查询缺席人
		// String absenteeStr = (String)meeting.get("absentee");
		String absenteeStr = Helper.findNotExistsStringIds(attendsStr, attendacceptsStr);
		String absenteeNameStr = new String();
		if (StringUtils.isNotEmpty(absenteeStr)) {
			List<Map> absenteeList = meetingService.getAttends(absenteeStr);
			mv.addObject("absentee", absenteeList);
			for (Map absenteeUser : absenteeList) {
				absenteeNameStr = absenteeNameStr + (String) absenteeUser.get("name") + ",";
			}
			absenteeNameStr = absenteeNameStr.length() > 0 ? absenteeNameStr.substring(0, absenteeNameStr.length() - 1)
					: absenteeNameStr;
		} else {
			mv.addObject("absentee", new ArrayList<Map>());
		}
		mv.addObject("absenteeName", absenteeNameStr);

		// 当状态为待主持，待参加时，进入会诊变为正在开会
		if (2 == (Integer) meeting.get("status")) {
			// 更新会诊状态为正在开会
			meetingService.updateMeetingStatus(meetingId, 3);
		}
		// 设置当前人出席会诊
		db.addMeetingAccept(meeting, userId);
		// 推送消息给当前参加会诊的人,提示"xxx已进入会诊"

		// 如果是刷新操作，也就是window.location.reload 则不广播，有可能是转换主持人权限
		String refererHeader = request.getHeader("Referer");
		String currentUrl = request.getRequestURL().toString();
		System.out.println("referer = " + refererHeader);
		System.out.println("currentUrl = " + currentUrl);
		if (!StringUtils.equalsIgnoreCase(currentUrl, refererHeader)) {
			StringBuilder strBuild = new StringBuilder(attendsStr);
			strBuild.append(",").append(meeting.get("user"));
			meetingService.joinMeetingBroadcast(user, strBuild.toString(), String.valueOf(meetingId));
		}
		folderService.createMeetingFolder(request, meeting.get("no").toString(),
				meeting.get("attaId") == null ? null : meeting.get("attaId").toString());
		return mv;
	}

	@RequestMapping("/currentMeeting")
	public @ResponseBody AjaxResult getCurrentMeeting(HttpServletRequest request, int meetingId) {
		AjaxResult result = new AjaxResult();
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		int presenterUserId = (int) meeting.get("user");
		Map<String, Object> presenterUser = userService.getUserById(presenterUserId);
		String loginId = (String) presenterUser.get("user");
		String userName = (String) presenterUser.get("name");
		result.put("loginId", loginId);
		result.put("userName", userName);
		result.put("userAvatar", presenterUser.get("avatar"));
		result.put("meetingInfo", meeting);

		Date startTime = (Date) meeting.get("stime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result.put("meetingStartTime", sdf.format(startTime));

		// 参与人信息
		String attendsStr = (String) meeting.get("attends");
		String[] attendsIdArray = attendsStr.split(",");
		StringBuffer loginIdArrayStr = new StringBuffer();
		StringBuffer nameArrayStr = new StringBuffer();
		StringBuffer avatarArrayStr = new StringBuffer();
		for (int i = 0; i < attendsIdArray.length; i++) {
			Map<String, Object> userById = userService.getUserById(Integer.parseInt(attendsIdArray[i]));
			String attendUserLoginId = (String) userById.get("user");
			String attendUserName = (String) userById.get("name");
			String avatar = (String) userById.get("avatar");
			if (i != attendsIdArray.length - 1) {
				loginIdArrayStr.append(attendUserLoginId + ",");
				nameArrayStr.append(attendUserName + ",");
				avatarArrayStr.append(avatar + ",");
			} else {
				loginIdArrayStr.append(attendUserLoginId);
				nameArrayStr.append(attendUserName);
				avatarArrayStr.append(avatar);
			}
		}

		result.put("attendsLoginIds", loginIdArrayStr);
		result.put("attendNames", nameArrayStr);
		result.put("attendIds", attendsStr);
		result.put("attendAvatars", avatarArrayStr);

		// 若当前用户与会诊发起者是同一个人，则返回true
		Map<String, Object> user = userService.getLoginInfo(request);
		int userId = (int) user.get("id");
		if (presenterUserId == userId) {
			result.put("isPresenter", true);
		} else {
			result.put("isPresenter", false);
		}

		result.put("currentUser", user);

		// 已签到的人员
		String attendacceptsStr = (String) meeting.get("attendaccepts");
		attendacceptsStr = attendacceptsStr + "," + userId;
		List<Map> attendacceptsList = meetingService.getAttends(attendacceptsStr);
		String attendacceptsNameStr = new String();
		for (Map attendacceptUser : attendacceptsList) {
			attendacceptsNameStr = attendacceptsNameStr + (String) attendacceptUser.get("name") + ",";
		}
		attendacceptsNameStr = attendacceptsNameStr.length() > 0
				? attendacceptsNameStr.substring(0, attendacceptsNameStr.length() - 1)
				: attendacceptsNameStr;
		result.put("attendacceptsName", attendacceptsNameStr);

		// 查询缺席人
		// String absenteeStr = (String)meeting.get("absentee");
		String absenteeStr = Helper.findNotExistsStringIds(attendsStr, attendacceptsStr);
		String absenteeNameStr = new String();
		if (StringUtils.isNotEmpty(absenteeStr)) {
			List<Map> absenteeList = meetingService.getAttends(absenteeStr);
			for (Map absenteeUser : absenteeList) {
				absenteeNameStr = absenteeNameStr + (String) absenteeUser.get("name") + ",";
			}
			absenteeNameStr = absenteeNameStr.length() > 0 ? absenteeNameStr.substring(0, absenteeNameStr.length() - 1)
					: absenteeNameStr;
		}
		result.put("absentee", absenteeStr);
		result.put("absenteeName", absenteeNameStr);

		return result;
	}

	@RequestMapping("/currentMeetingByUserID")
	public @ResponseBody AjaxResult getCurrentMeetingByUserID(HttpServletRequest request, int meetingId, int userid) {
		AjaxResult result = new AjaxResult();
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		int presenterUserId = (int) meeting.get("user");
		Map<String, Object> presenterUser = userService.getUserById(presenterUserId);
		String loginId = (String) presenterUser.get("user");
		String userName = (String) presenterUser.get("name");
		result.put("loginId", loginId);
		result.put("userName", userName);
		result.put("userAvatar", presenterUser.get("avatar"));
		result.put("meetingInfo", meeting);
		// 参与人信息
		String attendsStr = (String) meeting.get("attends");
		String[] attendsIdArray = attendsStr.split(",");
		StringBuffer loginIdArrayStr = new StringBuffer();
		StringBuffer nameArrayStr = new StringBuffer();
		StringBuffer avatarArrayStr = new StringBuffer();
		for (int i = 0; i < attendsIdArray.length; i++) {
			Map<String, Object> userById = userService.getUserById(Integer.parseInt(attendsIdArray[i]));
			String attendUserLoginId = (String) userById.get("user");
			String attendUserName = (String) userById.get("name");
			String avatar = (String) userById.get("avatar");
			if (i != attendsIdArray.length - 1) {
				loginIdArrayStr.append(attendUserLoginId + ",");
				nameArrayStr.append(attendUserName + ",");
				avatarArrayStr.append(avatar + ",");
			} else {
				loginIdArrayStr.append(attendUserLoginId);
				nameArrayStr.append(attendUserName);
				avatarArrayStr.append(avatar);
			}
		}

		result.put("attendsLoginIds", loginIdArrayStr);
		result.put("attendNames", nameArrayStr);
		result.put("attendAvatars", avatarArrayStr);

		// 若当前用户与会诊发起者是同一个人，则返回true
		Map<String, Object> user = userService.getUserById(userid);
		int userId = (int) user.get("id");
		if (presenterUserId == userId) {
			result.put("isPresenter", true);
		} else {
			result.put("isPresenter", false);
		}

		result.put("currentUser", user);

		return result;
	}

	@RequestMapping(value = "/transferPresenter", method = RequestMethod.POST)
	public @ResponseBody AjaxResult transferPresenter(HttpServletRequest request, int meetingId, String toUser) {
		Map<String, Object> user = userService.getLoginInfo(request);
		String loginId = (String) user.get("user");
		int userId = (int) user.get("id");
		Map<String, String> msgDataMap = new HashMap<String, String>();
		msgDataMap.put("type", "transferPresenter");
		msgDataMap.put("meetingId", "" + meetingId);
		msgDataMap.put("fromUser", "" + loginId);
		msgDataMap.put("fromUserId", "" + userId);
		String transferMsg = JSON.toJSONString(msgDataMap);
		TextMessage textMessage = new TextMessage(transferMsg);
		String sendResultMsg = myWebSocketHandler.sendMessageToUserForMeeting(toUser, textMessage, "transferPresenter",
				String.valueOf(meetingId));
		if ("true".equals(sendResultMsg)) {
			return AjaxResult.success("发送成功");
		} else {
			return AjaxResult.error(sendResultMsg);
		}
	}

	@RequestMapping(value = "/acceptPresenter", method = RequestMethod.POST)
	public @ResponseBody AjaxResult acceptPresenter(HttpServletRequest request, int meetingId, String toUser,
			int toUserId) {
		// 更新此会诊的主讲人
		Map<String, Object> user = userService.getLoginInfo(request);
		int userId = (int) user.get("id");
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		String attends = (String) meeting.get("attends");
		String[] split = attends.split(",");

		StringBuffer sb = new StringBuffer();
		StringBuffer sbOthers = new StringBuffer();
		for (int i = 0; i < split.length; i++) {
			String appendData = "";
			if (split[i].equals("" + userId)) {
				appendData = "" + toUserId;
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
		meetingService.updateMeetingPresenter(userId, sb.toString(), meetingId);

		// 发送信息给转让人
		Map<String, String> msgDataMap = new HashMap<String, String>();
		msgDataMap.put("type", "acceptPresenter");
		msgDataMap.put("accepted", "true");
		String transferMsg = JSON.toJSONString(msgDataMap);
		TextMessage textMessage = new TextMessage(transferMsg);
		String sendResultMsg = myWebSocketHandler.sendMessageToUserForMeeting(toUser, textMessage, "acceptPresenter",
				String.valueOf(meetingId));

		// 通知其余与会人主持人已变更
		if (sbOthers.length() > 0) {
			sbOthers.delete(sbOthers.length() - 1, sbOthers.length());
			List<Map> otherAttends = meetingService.getAttends(sbOthers.toString());
			msgDataMap = new HashMap<String, String>();
			msgDataMap.put("type", "updatePresenter");
			msgDataMap.put("presenter", String.valueOf(userId));
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

	@RequestMapping("/quitMeeting")
	public @ResponseBody AjaxResult quitMeeting(HttpServletRequest request, int meetingId) {
		AjaxResult result = new AjaxResult();
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		int presenterUserId = (int) meeting.get("user");
		// 若当前用户与会诊主持人是同一个人，则结束会诊
		Map<String, Object> user = userService.getLoginInfo(request);
		int userId = (int) user.get("id");
		String currentUserLoginId = (String) user.get("user");
		result = AjaxResult.success("退出会诊成功");
		result.put("currentUser", currentUserLoginId);
		if (presenterUserId == userId) {
			// 更新会诊状态为已结束
			meetingService.updateMeetingStatus(meetingId, 4);
			/* ------------ 开始---------------- */
			// V1.这里由音视频的quitmeeting的js接口进行页面刷新，暂不用发送消息到各与会人员并退出
			// V2.还是开启吧，不发消息与会人看不到提示
			String attends = (String) meeting.get("attends");
			List<Map> attendsObject = meetingService.getAttends(attends);
			for (Map map : attendsObject) {
				String toUser = (String) map.get("user");
				Map<String, String> msgDataMap = new HashMap<String, String>();
				msgDataMap.put("type", "quitMeeting");
				String transferMsg = JSON.toJSONString(msgDataMap);
				TextMessage textMessage = new TextMessage(transferMsg);
				myWebSocketHandler.sendMessageToUser(toUser, textMessage);
			}
			/* ------------ 结束 ---------------- */
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result.put("isPresenter", true);
		} else {
			// TODO 在IM消息里边出现一排红色字体，xxx已经退出会诊

			result.put("isPresenter", false);
		}
		return result;
	}

	@RequestMapping(value = "/saveMeetingInfo", method = RequestMethod.POST)
	public @ResponseBody AjaxResult saveMeetingInfo(HttpServletRequest request, int meetingId, String meetingTitle,
			String meetingTime, String meetingAttends, String meetingAbsentee, String meetingConclusion) {
		// 更新此会诊信息
		int updated = meetingService.updateMeetingInfo(meetingId, meetingTitle, meetingTime, meetingAttends,
				meetingAbsentee, meetingConclusion);
		if (updated == 1) {
			return AjaxResult.success("保存成功");
		} else {
			return AjaxResult.error("保存失败");
		}
	}

	@RequestMapping(value = "/shareMeetingInfo", method = RequestMethod.POST)
	public @ResponseBody AjaxResult shareMeetingInfo(HttpServletRequest request, int meetingId, String meetingTitle,
			String meetingTime, String meetingAttends, String meetingAbsentee, String meetingConclusion) {
		// 更新此会诊信息
		int updated = meetingService.updateMeetingInfo(meetingId, meetingTitle, meetingTime, meetingAttends,
				meetingAbsentee, meetingConclusion);
		// 发送邮件
		/*
		 * String result = emailService.sendShareMeetingInfoEmail(meetingTitle,
		 * meetingTime, meetingAttends, meetingAbsentee, meetingConclusion); if(updated
		 * == 1 && "true".equals(result)){ return AjaxResult.success("分享成功"); }else{
		 * return AjaxResult.error("分享失败"); }
		 */

		Map<String, Object> meeting = meetingService.getMeeting(meetingId);

		// 查询已签到人
		String attendaccepts = (String) meeting.get("attendaccepts");
		List<Map> attendacceptsList = new ArrayList<>();
		if (StringUtils.isNotEmpty(attendaccepts)) {
			attendacceptsList = meetingService.getAttends(attendaccepts);
		}
		String[] meetingAttendacceptsNames = new String[attendacceptsList.size()];
		for (int i = 0; i < attendacceptsList.size(); i++) {
			meetingAttendacceptsNames[i] = (String) attendacceptsList.get(i).get("name");
		}
		// 查询缺席人
		String absenteeStr = Helper.findNotExistsStringIds((String) meeting.get("attends"), attendaccepts);
		String absenteeNameStr = "";
		if (StringUtils.isNotEmpty(absenteeStr)) {
			List<Map> absenteeList = meetingService.getAttends(absenteeStr);
			for (Map absenteeUser : absenteeList) {
				absenteeNameStr = absenteeNameStr + (String) absenteeUser.get("name") + ",";
			}
			absenteeNameStr = absenteeNameStr.length() > 0 ? absenteeNameStr.substring(0, absenteeNameStr.length() - 1)
					: absenteeNameStr;
		}

		Map<String, String> mailTemplate = db.getMailTemplate("mail_share_meetingsInfo");
		Map<String, Object> mailParam = new HashMap<String, Object>();
		mailParam.put("meetingTitle", meeting.get("topic"));
		mailParam.put("meetingTime", meetingTime);
		mailParam.put("meetingAttends", StringUtils.join(meetingAttendacceptsNames, ","));
		mailParam.put("meetingAbsentee", absenteeNameStr);
		mailParam.put("meetingConclusion", meetingConclusion);
		String title = SendEmail.formatTemplate(mailTemplate.get("template_title"), mailParam);
		String content = SendEmail.formatTemplate(mailTemplate.get("template_context"), mailParam);

		String attends = (String) meeting.get("attends");
		List<Map> sendMailsList = new ArrayList<>();
		if (StringUtils.isNotEmpty(attends)) {
			sendMailsList = meetingService.getAttends(attends + "," + meeting.get("user"));
		}
		for (Map attend : sendMailsList) {
			String email = (String) attend.get("email");
			asyncService.sendMail(email, title, content);
			
			try {
		    	if(attend!=null && attend.containsKey("phone") && attend.get("phone")!=null) {
					String toPhones = attend.get("phone").toString();
					String toPhone = toPhones.substring(12);
					String[] params=new String[]{
							meeting.get("topic").toString(),
							meetingTime,
							StringUtils.join(meetingAttendacceptsNames, ","),
							absenteeNameStr,
							meetingConclusion
							};
					asyncService.sendSms(toPhone, "SMS_TEMP_6", params);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			

		}
		return AjaxResult.success("分享成功");
	}

	@RequestMapping(value = "/viewNotify", method = RequestMethod.POST)
	public @ResponseBody AjaxResult viewNotify(HttpServletRequest request, @RequestBody Map<String, Object> input) {
		Map<String, Object> user = userService.getLoginInfo(request);
		int userId = (int) user.get("id");
		String userName = (String) user.get("user");
		int meetingId = (int) input.get("meeting");

		Map<String, String> msg = new HashMap<String, String>();
		msg.put("type", "view.notify");
		msg.put("meetingId", "" + meetingId);
		msg.put("fromUser", "" + userName);
		msg.put("fromUserId", "" + userId);
		msg.put("page", (String) input.get("page"));
		if (input.containsKey("content")) {
			msg.put("content", (String) input.get("content"));
		}
		String error = this.messageBroadcast(userId, meetingId, msg);
		if (error == null || error.equals("true")) {
			return AjaxResult.success("发送成功");
		} else {
			return AjaxResult.error(error);
		}
	}

	@RequestMapping(value = "/viewAsk", method = RequestMethod.POST)
	public @ResponseBody AjaxResult viewAsk(HttpServletRequest request, @RequestBody Map<String, Object> input) {
		Map<String, Object> user = userService.getLoginInfo(request);
		int userId = (int) user.get("id");
		String userName = (String) user.get("user");
		int meetingId = (int) input.get("meeting");

		Map<String, String> msg = new HashMap<String, String>();
		msg.put("type", "view.ask");
		msg.put("meetingId", "" + meetingId);
		msg.put("fromUser", "" + userName);
		msg.put("fromUserId", "" + userId);

		String error = this.messageReport(userId, meetingId, msg);
		if (error == null || error.equals("true")) {
			return AjaxResult.success("发送成功");
		} else {
			return AjaxResult.error(error);
		}
	}

	@RequestMapping(value = "/viewTell", method = RequestMethod.POST)
	public @ResponseBody AjaxResult viewTell(HttpServletRequest request, @RequestBody Map<String, Object> input) {
		Map<String, Object> user = userService.getLoginInfo(request);
		int userId = (int) user.get("id");
		String userName = (String) user.get("user");
		int meetingId = (int) input.get("meeting");

		Map<String, String> msg = new HashMap<String, String>();
		msg.put("type", "view.tell");
		msg.put("meetingId", "" + meetingId);
		msg.put("fromUser", "" + userName);
		msg.put("fromUserId", "" + userId);
		msg.put("page", (String) input.get("page"));
		if (input.containsKey("content")) {
			msg.put("content", (String) input.get("content"));
		}

		TextMessage textMessage = new TextMessage(JSON.toJSONString(msg));
		String error = myWebSocketHandler.sendMessageToUser((String) input.get("toUser"), textMessage);
		if (error == null || error.equals("true")) {
			return AjaxResult.success("发送成功");
		} else {
			return AjaxResult.error(error);
		}
	}

	/**
	 * 广播会诊消息 - 主持人>与会人
	 */
	private String messageBroadcast(int userId, int meetingId, Map<String, String> msg) {
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		if (userId != (int) meeting.get("user")) {
			return "非会诊主持人无权广播消息";
		}
		TextMessage textMessage = new TextMessage(JSON.toJSONString(msg));
		List<Map> attendList = meetingService.getAttends((String) meeting.get("attends"));
		if (attendList != null && attendList.size() > 0) {
			for (Map item : attendList) {
				myWebSocketHandler.sendMessageToUserForMeeting(meetingId, (String) item.get("user"), textMessage);
			}
		}
		return null;
	}

	/**
	 * 上报会诊消息 - 与会人>主持人
	 */
	private String messageReport(int userId, int meetingId, Map<String, String> msg) {
		Map<String, Object> meeting = meetingService.getMeeting(meetingId);
		Map<String, Object> presenter = userService.getUserById((int) meeting.get("user"));
		TextMessage textMessage = new TextMessage(JSON.toJSONString(msg));
		return myWebSocketHandler.sendMessageToUserForMeeting(meetingId, (String) presenter.get("user"), textMessage);
	}

	/**
	 * 显示文件列表,同时支持上传文件,最多可上传8个文件
	 * 
	 * @param request
	 * @param code
	 * @param response
	 * @return
	 */
	@RequestMapping("/fileUploadForm")
	public ModelAndView fileUploadForm(HttpServletRequest request, Integer id, HttpServletResponse response) {
		// int status = 200;
		ModelAndView mv = new ModelAndView("PathUploadForm");// 返回到对应的jsp页面
		Map<String, Object> form = new HashMap<String, Object>();// 创建返回对象
		form.put("id", id);// 将code添加到列表页面
		Map<String, Object> meeting = meetingService.getMeeting(id);
		String attaId = meeting.get("attaId") != null ? meeting.get("attaId").toString() : "";
		Integer number = 8;
		List<Map> rows = getAttrByMeetingId(id);
		if (rows != null) {
			number = number - rows.size();
		}
		mv.addObject("meetingInfo", meeting);
		mv.addObject("number", number);
		mv.addObject("form", form);// 返回到列表页面
		return mv;
	}

	public List<Map> getAttrByMeetingId(Integer id) {
		List<Map> rows = new ArrayList<Map>();
		Map<String, Object> meeting = meetingService.getMeeting(id);
		String attaId = meeting.get("attaId") != null ? meeting.get("attaId").toString() : "";
		if (StringUtils.isNotEmpty(attaId)) {
			String[] aids = attaId.split(",");
			for (String aid : aids) {
				if (StringUtils.isNotEmpty(aid)) {
					rows.add(db.getAttr(aid));
				}
			}
		}
		return rows;
	}

	/**
	 * 提交文件,更新Meeting中的AttaId信息
	 * 
	 * @param request
	 * @param id
	 * @param attaId
	 * @return
	 */
	@RequestMapping(value = "/saveMeetingAttaId", method = { RequestMethod.POST })
	@ResponseBody
	public String upload(@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "attaId", required = true) String attaId, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		attaId = attaId.trim();
		JSONObject json = new JSONObject();
		json.put("code", "500");
		try {
			Map<String, Object> user = userService.getLoginInfo(request);
			Map<String, Object> meeting = meetingService.getMeeting(id);
			List<Map> rows = getAttrByMeetingId(id);// 查询获得的列表对象
			if (rows.size() >= 8) {
				json.put("code", 510);
			} else {
				if (meeting != null) {
					String attaIds = meeting.get("attaId") != null ? meeting.get("attaId").toString() : "";
					if (StringUtils.isNotEmpty(attaIds)) {
						if (attaIds.endsWith(",")) {
							attaIds = attaIds + attaId + ",";
						} else {
							attaIds = attaIds + "," + attaId + ",";
						}
					} else {
						attaIds = attaId + ",";
					}
					meetingService.updatMeetingeByAttaId(attaIds, id);
					this.folderService.createMeetingFolder(user, id, attaId);

					Integer number = 8;
					rows = getAttrByMeetingId(id);
					if (rows != null) {
						number = number - rows.size();
					}
					json.put("number", number);
					json.put("code", 200);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = json.toJSONString();
		try {
			result = new String(result.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param request
	 * @param id
	 * @param attaId
	 * @return
	 */
	@RequestMapping(value = "/saveSetShareFile", method = { RequestMethod.POST })
	@ResponseBody
	public String saveSetShareFile(@RequestParam(value = "aid", required = true) String aid,
			@RequestParam(value = "mid", required = true) int mid,
			@RequestParam(value = "attend", required = true) String attend, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		aid = aid.trim();
		attend = attend.trim();
		JSONObject json = new JSONObject();
		json.put("code", "500");
		try {
			this.folderService.deleteByParentSourceMeeting(String.valueOf(aid));
			String[] attends = attend.split(",");
			for (String uid : attends) {

				Map<String, Object> user = userService.getUserById(Integer.parseInt(uid));
				this.folderService.createMeetingFolderShare(user, mid, aid);

			}
			this.folderService.updateAttachmentByShareUser(aid, attend);
			this.folderService.updateAttachmentByIsShare(aid, 1);
			json.put("code", 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = json.toJSONString();
		try {
			result = new String(result.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	@RequestMapping(value = "/selectSetShareFile", method = { RequestMethod.POST })
	@ResponseBody
	public String selectSetShareFile(@RequestParam(value = "aid", required = true) String aid,
			@RequestParam(value = "mid", required = true) int mid,
			@RequestParam(value = "ishare", required = true) int ishare, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		aid = aid.trim();
		JSONObject json = new JSONObject();
		json.put("code", "500");
		try {
			if (ishare == 0) {
				this.folderService.deleteByParentSourceMeeting(String.valueOf(aid));
			} else {
				String attend = "";
				Map amap = db.getAttr(aid);
				if (amap.get("share_user") == null) {
					Map mmap = meetingService.getMeeting(mid);
					attend = mmap.get("attends").toString();
				} else {
					attend = amap.get("share_user").toString();
				}
				String[] attends = attend.split(",");
				for (String uid : attends) {
					Map<String, Object> user = userService.getUserById(Integer.parseInt(uid));
					this.folderService.createMeetingFolderShare(user, mid, aid);
				}
				this.folderService.updateAttachmentByShareUser(aid, attend);

			}
			this.folderService.updateAttachmentByIsShare(aid, ishare);

			json.put("code", 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = json.toJSONString();
		try {
			result = new String(result.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	@RequestMapping("RecordVideoManage")
	public ModelAndView recordVideo() {
		return new ModelAndView("RecordVideo");
	}

	@RequestMapping(value = "/queryRecordMeetings")
	@ResponseBody
	public Object queryRecordMeetings(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, Object> user = userService.getLoginInfo(request);
			int userId = (int) user.get("id");
			params.put("userId", "," + userId + ",");
			List<Map<String,Object>> data= recordService.queryMeetings(params);
			json.put("data", data);
			json.put("code", "0");
		} catch (Exception e) {
			json.put("code", "1");
			json.put("msg", e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value = "/queryRecordVideo")
	@ResponseBody
	public String queryRecordVideo(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			String searchValue = request.getParameter("searchValue");
			if ("".equals(searchValue)) {
				searchValue = null;
			}
			if (searchValue != null && !searchValue.isEmpty()) {
				searchValue = "%" + searchValue + "%";
			}
			params.put("searchValue", searchValue);
			
			int meetingId = 0;
			if (request.getParameter("meetingId") != null && !request.getParameter("meetingId").isEmpty()) {
				meetingId = Integer.valueOf(request.getParameter("meetingId"));
			}
			params.put("meetingId", meetingId);

			String sort = request.getParameter("sort");
			if (sort == null || sort.isEmpty()) {
				sort = "a.inDate";
			}
			params.put("sort", sort);

			int limit = 10;
			if (request.getParameter("limit") != null && !request.getParameter("limit").isEmpty()) {
				limit = Integer.valueOf(request.getParameter("limit"));
			}
			params.put("limit", limit);

			int page = 1;
			if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
				page = Integer.valueOf(request.getParameter("page"));
			}
			params.put("offset", (page - 1) * limit);

			String direction = request.getParameter("direction");
			if (direction == "1") {
				direction = "asc";
			} else {
				direction = "desc";
			}
			params.put("direction", direction);
			Map<String, Object> user = userService.getLoginInfo(request);
			int userId = (int) user.get("id");
			params.put("userId", "," + userId + ",");

			int count = recordService.countVideo(params);
			List<RecordVideo> dataList = recordService.queryVideo(params);
			json.put("code", "0");
			json.put("count", count);
			json.put("data", dataList);
		} catch (Exception e) {

			json.put("code", "1");
			json.put("msg", e.getMessage());
		}
		return json.toJSONString();
	}

	@RequestMapping(value = "/RenameRecordVideo", method = RequestMethod.POST)
	@ResponseBody
	public String renameRecordVideo(@RequestBody Map<String, Object> params) {
		JSONObject json = new JSONObject();
		try {
			int id = (int) params.get("id");
			String name = (String) params.get("name");
			recordService.renameVideo(id, name);
			json.put("code", "0");
		} catch (Exception e) {
			json.put("code", "1");
			json.put("msg", e.getMessage());
		}
		return json.toJSONString();
	}

	@RequestMapping(value = "/DeleteRecordVideo", method = RequestMethod.POST)
	@ResponseBody
	public String deleteRecordVideo(@RequestBody String ids) {
		JSONObject json = new JSONObject();
		try {
			ids = ids.replace('"', ' ');
			recordService.deleteVideos(ids);
			json.put("code", "0");
		} catch (Exception e) {
			json.put("code", "1");
			json.put("msg", e.getMessage());
		}
		return json.toJSONString();
	}
	
	
	
	
	@RequestMapping(value = "/findMeetingName")
	@ResponseBody
	public JSON findMeetingName(int meetingId){
		JSONObject json = meetingService.findMeetingName(meetingId);
		System.out.println(json);
		return json;
	}
	
}