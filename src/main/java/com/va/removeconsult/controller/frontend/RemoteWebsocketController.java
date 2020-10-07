package com.va.removeconsult.controller.frontend;

import com.va.removeconsult.websocket.remote.RemoteMessageRequest;
import com.va.removeconsult.websocket.sender.LocalMessageSender;
import com.va.removeconsult.websocket.session.AbstractWebsocketSessionManager;
import com.va.removeconsult.websocket.session.GlobalUserWebsocketSessionManager;
import com.va.removeconsult.websocket.session.MettingUserWebsocketSessionManager;
import com.va.removeconsult.websocket.session.UserWebsocketSessionManager;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.TextMessage;

@RequestMapping("/remote/websocket")
@Controller
public class RemoteWebsocketController {
	static final Logger logger = LoggerFactory.getLogger(RemoteWebsocketController.class);
	
	@Autowired
	private UserWebsocketSessionManager userWebsocketSessionManager;

	@RequestMapping(value = "/msg/send")
	public void sendMsg(@RequestBody RemoteMessageRequest request, HttpServletResponse resp) throws IOException {
		logger.info("RemoteWebsocketController send websocket toUser: {}, msg: {}", request.getToUserCode(), request.getMessage());
		TextMessage message = new TextMessage(request.getMessage());
		String result = LocalMessageSender.sendMsg(userWebsocketSessionManager, request.getToUserCode(), message);
		logger.info("RemoteWebsocketController send websocket result: {}", result);

		resp.getOutputStream().write(result.getBytes());
//		resp.getWriter().flush();
		
	}
}
