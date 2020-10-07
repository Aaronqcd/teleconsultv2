package com.va.removeconsult.websocket;

import com.alibaba.fastjson.JSON;
import com.va.removeconsult.websocket.sender.MessageSender;
import com.va.removeconsult.websocket.session.MettingUserWebsocketSessionManager;
import com.va.removeconsult.websocket.session.UserWebsocketSessionManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Service
public class MyWebSocketHandler extends TextWebSocketHandler {
	Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);
	// 在线用户列表
//	private static final Map<String, WebSocketSession> users;

	// 会诊中心用户列表
//	private static final Map<String, WebSocketSession> meetingUsers;

	// 全局用户列表
	private static final Map<String, WebSocketSession> globalUsers;

	// 用户标识
	private static final String CLIENT_ID = "loginId";

	private static final String SUCCESS = "true";

	@Autowired
	private UserWebsocketSessionManager userWebsocketSessionManager;

	@Autowired
	private MettingUserWebsocketSessionManager mettingUserWebsocketSessionManager;


	@Autowired
	private MessageSender messageSender;

	static {
//		users = new HashMap<String, WebSocketSession>();
//		meetingUsers = new HashMap<String, WebSocketSession>();
		globalUsers = new HashMap<String, WebSocketSession>();

	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info("成功建立连接");
		String loginId = getClientId(session);
		String meetingAndLoginId = getMeetingAndloginId(session);

		logger.info("websocket登录用户：" + loginId);

		logger.info("websocket登录用户session：" + session.getUri() + "  " + session.getAcceptedProtocol() + " "
				+ JSON.toJSONString(session.getExtensions()) + JSON.toJSONString(session.getAttributes())
				+ JSON.toJSONString(session.getHandshakeHeaders()));
		if (loginId != null) {
//			users.put(loginId, session);
			userWebsocketSessionManager.saveSession(loginId, session);
			// session.sendMessage(new TextMessage("成功建立socket连接"));
			logger.info("成功建立socket连接-----------" + loginId + ": " + session);
		}
//		System.out.println(users);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {

		logger.info(message.getPayload());

		String key = message.getPayload();
		if (StringUtils.isNotBlank(key)) {
			if("ping".equals(key)) {
				logger.info("websocket ping");
				return;
			}
			
			
			if (key.startsWith("meeting")) {
//				meetingUsers.put(key, session);
				mettingUserWebsocketSessionManager.save(key);
			} else if (key.startsWith("global")) {
				globalUsers.put(key, session);
			} else {
				logger.info("不确定是哪个页面发送过来的websocket = " + key);
			}
		}
		WebSocketMessage<String> message1 = new TextMessage("websocket服务端处理信息:" + message);
		logger.info(message1.getPayload());

		/*
		 * try { session.sendMessage(message1); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
	}

	/**
	 * 发送信息给指定用户
	 * 
	 * @param clientId
	 * @param message
	 * @return
	 */
	public String sendMessageToUser(String clientId, TextMessage message, String... params) {
//		String msg = "该用户未在线，无法发送通知";
//		String type = null;
//		if (null != params && params.length > 0) {// 数params是被作为一个数组对待的
//			type = params[0];
//		}
//		if (users.get(clientId) == null) {
//			if (StringUtils.isNotBlank(type)) {
//				if (type.contains("applyHost")) { // 申请主持人
//					msg = "当前会诊主持人未进入会诊中，无法申请";
//				} else if (type.contains("confirmTransfer")) {
//					msg = "该用户未进入会诊中，无法通知";
//				} else if (type.contains("inviteExpert")) {
//					// 如果专家不在，则发邮件通知
//					msg = "该用户未登录,已发邮件通知";
//				}
//			}
//			logger.debug(msg);
//			return msg;
//		} else {
//			if (StringUtils.isNotBlank(type)) {
//				if (type.contains("transferPresenter")) { // 主持人转让
//					if (meetingUsers.get("meeting_" + clientId) == null) {
//						msg = "该用户未进入会诊中，无法转让";
//						logger.info(clientId + msg);
//						return msg;
//					}
//				}
//			}
//		}
//		WebSocketSession session = users.get(clientId);
//		logger.debug("发送消息给:" + clientId + ", 内容: " + session);
//		if (!session.isOpen()) {
//			msg = "websocket session没有开启。";
//			logger.debug(msg);
//			return msg;
//		}
//		try {
//			if (session != null) {
//				session.sendMessage(message);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			msg = e.getMessage();
//			return msg;
//		}
//		return "true";


		String msg = "该用户未在线，无法发送通知";
		String type = null;
		if (null != params && params.length > 0) {// 数params是被作为一个数组对待的
			type = params[0];
		}
		if (!SUCCESS.equals(messageSender.sendMsg(clientId, message))) {
			if (StringUtils.isNotBlank(type)) {
				if (type.contains("applyHost")) { // 申请主持人
					msg = "当前会诊主持人未进入会诊中，无法申请";
				} else if (type.contains("confirmTransfer")) {
					msg = "该用户未进入会诊中，无法通知";
				} else if (type.contains("inviteExpert")) {
					// 如果专家不在，则发邮件通知
					msg = "该用户未登录,已发邮件通知";
				}
			}
			logger.debug(msg);
			return msg;
		} else {
			if (StringUtils.isNotBlank(type)) {
				if (type.contains("transferPresenter")) { // 主持人转让
					if (!mettingUserWebsocketSessionManager.exists("meeting_" + clientId)) {
						msg = "该用户未进入会诊中，无法转让";
						logger.info(clientId + msg);
						return msg;
					}
				}
			}
		}

		return SUCCESS;
	}

	/**
	 * 广播信息
	 * 
	 * @param message
	 * @return
	 */
	public boolean sendMessageToAllUsers(TextMessage message) {
		boolean allSendSuccess = true;
//		Set<String> clientIds = users.keySet();
//		WebSocketSession session = null;
//		for (String clientId : clientIds) {
//			try {
//				session = users.get(clientId);
//				if (session.isOpen()) {
//					session.sendMessage(message);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				allSendSuccess = false;
//			}
//		}

		return allSendSuccess;
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		if (session.isOpen()) {
			session.close();
		}
//		users.remove(getClientId(session));
		userWebsocketSessionManager.removeSession(getClientId(session));

//		meetingUsers.remove("meeting_" + getMeetingAndloginId(session));
		mettingUserWebsocketSessionManager.remove("meeting_" + getMeetingAndloginId(session));
		globalUsers.remove("global_" + getClientId(session));
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("websocket连接已关闭：" + status + " start");
//		users.remove(getClientId(session));
		userWebsocketSessionManager.removeSession(getClientId(session));
//		meetingUsers.remove("meeting_" + getMeetingAndloginId(session));
		mettingUserWebsocketSessionManager.remove("meeting_" + getMeetingAndloginId(session));
		globalUsers.remove("global_" + getClientId(session));
		String loginId = getClientId(session);
		logger.info("websocket连接已关闭：{}  end. 登录用户: {}}", status , loginId);
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	/**
	 * 获取用户标识
	 * 
	 * @param session
	 * @return
	 */
	private String getClientId(WebSocketSession session) {
		try {
			String clientId = (String) session.getAttributes().get(CLIENT_ID);
			return clientId;
		} catch (Exception e) {
			return null;
		}
	}

	private String getMeetingAndloginId(WebSocketSession session) {
		try {
			String meetingAndloginId = (String) session.getAttributes().get("meetingAndloginId");
			return meetingAndloginId;
		} catch (Exception e) {
			logger.error("未获取到meetingAndloginId,错误信息:" + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 会诊中心的websocket推送方法 发送信息给指定用户
	 * 
	 * @param clientId
	 * @param message
	 * @return
	 */
	public String sendMessageToUserForMeeting(String clientId, TextMessage message, String... params) {
		String msg = "该用户未在线，无法发送通知";
		String type = null;
		String meetingId = null;
		if (null != params && params.length > 0) {// 数params是被作为一个数组对待的
			type = params[0];
			meetingId = params[1];
		}

//		if (users.get(clientId) == null) {
		if (!userWebsocketSessionManager.exists(clientId)) {
			if (StringUtils.isNotBlank(type)) {
				if (type.contains("applyHost")) { // 申请主持人
					msg = "当前会诊主持人未在线，无法申请";
				} else if (type.contains("inviteExpert")) {
					// 如果专家不在，则发邮件通知
					msg = "该用户未登录,已发邮件通知";
				}
			}
			logger.info(msg);
			return msg;
		} else {
			if (StringUtils.isNotBlank(type)) {
				if (!mettingUserWebsocketSessionManager.exists(meetingId, clientId)) {
					if (type.equalsIgnoreCase("transferPresenter")) { // 主持人转让
						msg = "该用户未进入会诊中，无法转让";
						logger.info(clientId + msg);
						return msg;
					} else if (type.equalsIgnoreCase("applyHost")) { // 主持人转让
						msg = "当前会诊主持人未进入会诊中，无法申请";
						logger.info(clientId + msg);
						return msg;

					} else if (type.equalsIgnoreCase("confirmTransfer")) {
						msg = "当前用户未进入会诊中，无法通知";
						logger.info(clientId + msg);
						return msg;
					} else if (type.equalsIgnoreCase("joinMeetingBroadcast")) {
						msg = "当前用户不在同一会诊中，无法通知";
						logger.info(clientId + msg);
						return msg;
					}
				}
			}
		}
//		WebSocketSession session = users.get(clientId);
//		logger.debug("发送消息给:" + clientId + ", 内容: " + session);
//		if (!session.isOpen()) {
//			msg = "websocket session没有开启。";
//			logger.debug(msg);
//			return msg;
//		}
//		try {
//			if (session != null) {
//				session.sendMessage(message);
//			}
//		} catch (IOException e) {
//			logger.error("websocket连接报错 " + e.getMessage(), e);
//			msg = "服务连接报错,无法推送信息";
//			return msg;
//		}
//		return "true";
		return messageSender.sendMsg(clientId, message);
	}

	/**
	 * 给会诊中的用户发送消息
	 * @param meetingId: 会诊ID
	 * @param userLogin: 用户登录名
	 * */
	public String sendMessageToUserForMeeting(int meetingId, String userLogin, TextMessage message) {
//		WebSocketSession session = meetingUsers.get("meeting_" + meetingId + "_" + userLogin);
//		if (session == null || !session.isOpen()) {
//			return null;
//		}
//		try {
//			session.sendMessage(message);
//		} catch (IOException e) {
//			logger.error("websocket报错 " + e.getMessage(), e);
//			return  "服务连接报错,无法推送信息";
//		}
//		return null;
		if (!mettingUserWebsocketSessionManager.exists(meetingId + "", userLogin)) {
			return null;
		}
		return messageSender.sendMsg(userLogin, message);
	}
}
