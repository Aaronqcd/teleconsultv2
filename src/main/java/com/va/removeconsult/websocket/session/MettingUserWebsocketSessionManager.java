package com.va.removeconsult.websocket.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.va.removeconsult.websocket.sender.MessageSender;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class MettingUserWebsocketSessionManager {
	static final Logger logger = LoggerFactory.getLogger(MettingUserWebsocketSessionManager.class);

    private final static String redisKeyFormat = "meeting_{0}_{1}";

    @Autowired
    private StringRedisTemplate redisTemplate2;

    private String getRedisKey(String meetingID, String userCode) {
        return MessageFormat.format(redisKeyFormat, meetingID, userCode);
    }

    public void save(String meetingID, String userCode) {
        save(getRedisKey(meetingID, userCode));
    }

    public void save(String key) {
    	try {
	        redisTemplate2.opsForValue().set(key, "1");
	        redisTemplate2.expire(key, 1, TimeUnit.DAYS);
    	}catch(Exception e) {
    		logger.error("保存会议成员标识时异常", e);
        }
    }


    public boolean exists(String meetingID, String userCode) {
        return exists(getRedisKey(meetingID, userCode));
    }
    public boolean exists(String key) {
    	try {
    		return redisTemplate2.hasKey(key);
    	}catch(Exception e) {
    		logger.error("查询会议成员标识时异常", e);
    		return false;
        }
    }

    public void remove(String meetingID, String userCode) {
        remove(getRedisKey(meetingID, userCode));
    }

    public void remove(String key) {
    	try {
	        if (redisTemplate2.hasKey(key)) {
	            redisTemplate2.delete(key);
	        }
    	}catch(Exception e) {
    		logger.error("删除会议成员标识时异常", e);
        }
    }
}
