package com.va.removeconsult.websocket.sender;

import com.va.removeconsult.websocket.session.UserWebsocketSessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author yefei
 * @date: 2020/7/4
 */
@Service
public class MessageSender {
	static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    @Autowired
    private UserWebsocketSessionManager userWebsocketSessionManager;

    public String sendMsg(String toUserCode, TextMessage message) {
    	try {
	        WebSocketSession session = userWebsocketSessionManager.getSessionByUserCode(toUserCode);
	        if (session == null) {
	            return RemoteMessageSender.sendRemoteMessageAndLog(toUserCode, userWebsocketSessionManager.getUserIP(toUserCode), message);
	        } else if (session.isOpen()) {
	            return LocalMessageSender.sendMsgAndLog(session, message);
	        } else {
	            userWebsocketSessionManager.removeSession(toUserCode);
	            logger.error("websocket has gone");
	            return "websocket已断开";
	        }
    	}catch(Exception e) {
    		logger.error("推送websocket消息时异常", e);
        	return e.getMessage();
        }
    }
    

}
