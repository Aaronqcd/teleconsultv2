package com.va.removeconsult.websocket.session;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.socket.WebSocketSession;

import com.va.removeconsult.websocket.sender.MessageSender;

/**
 * @author yefei
 * @date: 2020/7/4
 */
public abstract class AbstractWebsocketSessionManager {
	static final Logger logger = LoggerFactory.getLogger(AbstractWebsocketSessionManager.class);
    protected static String localIP;
   
    protected final static String userRedisKeyFormat = "User_Websocket_instance_{0}";

    protected HashMap<String, WebSocketSession> localSessionPool;
    static {
    	localIP = getLocalIp();
    }
    @Autowired
    private StringRedisTemplate redisTemplate2;
    
    AbstractWebsocketSessionManager () {
        localSessionPool = new HashMap();
    }

    protected String genUserRedisKey(String userCode) {
        return MessageFormat.format(userRedisKeyFormat, userCode);
    } 

    public WebSocketSession getSessionByUserCode(String userCode) {
//    	return null;
        return localSessionPool.containsKey(userCode) ? localSessionPool.get(userCode) : null;
    }

    public void saveSession(String userCode, WebSocketSession session) {
        localSessionPool.put(userCode, session);
        saveToRedis(userCode);
    }

    private void saveToRedis(String userCode) {
    	String redisKey = genUserRedisKey(userCode);
    	redisTemplate2.opsForValue().set(redisKey, localIP);
        redisTemplate2.expire(redisKey, 1, TimeUnit.DAYS);
    }

    public void removeSession(String userCode) {
        localSessionPool.remove(userCode);
        deleteRedisSession(userCode);
    }

    private void deleteRedisSession(String userCode) {
        localSessionPool.remove(genUserRedisKey(userCode));
    }
    
   public String getUserIP(String userCode) {
	   return redisTemplate2.opsForValue().get(genUserRedisKey(userCode));
   }
    
    protected static String getLocalIp() {
    	String localIP = null;
		InetAddress ip4;
		try {
			ip4 = Inet4Address.getLocalHost();
			localIP = ip4.getHostAddress();
			
		} catch (UnknownHostException e) {
			logger.error("初始化获取本地ip失败", e);
		}
		logger.info("获取本地IP:{}", localIP);
		return localIP;
    }

    public boolean exists(String userCode) {
    	logger.info("查询websocket用户是否在线, {}", userCode);
        if (localSessionPool.containsKey(userCode)) { 
        	logger.info("websocket用户在本地, {}", userCode);
            return true;
        }
        return redisTemplate2.hasKey(genUserRedisKey(userCode));
    }
}
