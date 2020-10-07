package com.va.removeconsult.websocket.sender;

import com.google.gson.Gson;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.websocket.remote.RemoteMessageRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.text.MessageFormat;

public class RemoteMessageSender {

	static final Logger logger = LoggerFactory.getLogger(RemoteMessageSender.class);

	public static final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	private static final String ENCODE = "UTF-8";

	public static String sendRemoteMessageAndLog(String toUserCode, String remoteServerIP, TextMessage message) {
		String result = sendRemoteMessage(toUserCode, remoteServerIP, message);
		logger.info("websocket remote send result：{}",result);
		return result;
	}

	private static String sendRemoteMessage(String toUserCode, String remoteServerIP, TextMessage message) {
		logger.info("websocket remote send, ip: {}, msg：{}", remoteServerIP, message.getPayload());

		RemoteMessageRequest remoteMessageRequest = new RemoteMessageRequest();
		remoteMessageRequest.setMessage(message.getPayload().toString());
		remoteMessageRequest.setToUserCode(toUserCode);
		

		StringEntity reqEntity = new StringEntity(new Gson().toJson(remoteMessageRequest), ContentType.create("text/plain", ENCODE));
		reqEntity.setContentType("application/json");
		reqEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

		HttpPost httpPost = new HttpPost(getRemoteUrl(remoteServerIP));
		httpPost.setEntity(reqEntity);
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			String result = EntityUtils.toString(response.getEntity(), "UTF-8");
			logger.info("invoke remote sever send websocket msg result: {}", result);
			return result;
		} catch (IOException e) {
			logger.error("invoke remote sever send websocket msg fail", e);
			return "调用远程服务发送websocket消息失败";
		}
	}


	private static String getRemoteUrl(String ip) {
		return MessageFormat.format("{0}://{1}:{2}/teleconsult/remote/websocket/msg/send", ConfigureReader.instance().getScheme(), ip, ConfigureReader.instance().getPort());
	}
	
}
