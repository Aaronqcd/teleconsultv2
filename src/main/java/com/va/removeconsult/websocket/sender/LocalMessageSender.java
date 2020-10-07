package com.va.removeconsult.websocket.sender;

import com.va.removeconsult.websocket.session.AbstractWebsocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class LocalMessageSender {

	static final Logger logger = LoggerFactory.getLogger(LocalMessageSender.class);
	private static final String SUCCESS = "true";

	public static String sendMsg(AbstractWebsocketSessionManager websocketSessionManager, String toUserCode, TextMessage message) {
		WebSocketSession webSocketSession = websocketSessionManager.getSessionByUserCode(toUserCode);
		if (webSocketSession == null) {
			String msg = "websocket session不存在,  user: " + toUserCode;
			logger.info(msg);
			return msg;
		}
		return sendMsgAndLog(webSocketSession, message);
	}


	public static String sendMsgAndLog( WebSocketSession session, TextMessage message) {
		String result = sendMsg(session, message);
		logger.info("websocket local send result: {}",result);
		return result;
	}

	private static String sendMsg( WebSocketSession session, TextMessage message) {
		logger.info("websocket local send msg: {}", message.getPayload());
		String msg;
		if (session == null) {
			msg = "websocket session不存在";
			logger.info(msg);
			return msg;
		}
		if (!session.isOpen()) {
			msg = "websocket session没有开启。";
			logger.info(msg);
			return msg;
		}
		try {
			session.sendMessage(message);
		} catch (IOException e) {
			logger.error("推送websocket消息失败", e);
			msg = e.getMessage();
			return msg;
		}
		return SUCCESS;
	}
}
