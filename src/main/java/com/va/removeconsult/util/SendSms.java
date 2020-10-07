package com.va.removeconsult.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;

public class SendSms {
	
	 private static Logger logger = LoggerFactory.getLogger(SendSms.class);

	 public static boolean sendSms(Integer appId,String appKey,String sign,String toPhone,int templateId,String[] params) {
		    try {
		    	if(StringUtils.isNotEmpty(toPhone)) {
			    	SmsSingleSender ssender = new SmsSingleSender(appId, appKey);
			    	SmsSingleSenderResult result = ssender.sendWithParam("86", toPhone,templateId, params, sign, "", "");
			    	logger.info(String.format("发送SMS-->: %s", toPhone));
			    	logger.info(String.format("发送SMS返回-->: %s", decodeUnicode(JSONObject.toJSONString(result))));
			    	return true;
		    	}else {
		    		logger.info(String.format("发送SMS不成功，手机号为空"));
		    	}
			} catch (Exception e) {
				e.printStackTrace();
			}
		    return false;
	  }
	 
	 
	 public static String decodeUnicode(String text) {
	    try{
	        final StringBuffer buffer = new StringBuffer(text==null?"":text);
	        if(StringUtils.isNotBlank(text) && text.contains("\\u")) {
	            buffer.delete(0,buffer.length());
	            int start = 0;
	            int end = 0;
	            while (start > -1) {
	                end = text.indexOf("\\u", start + 2);
	                String a="";//如果夹着非unicode编码的字符串，存放在这
	                String charStr = "";
	                if (end == -1) {
	                    if(text.substring(start + 2, text.length()).length()>4){
	                        charStr = text.substring(start + 2, start + 6);
	                        a = text.substring(start + 6, text.length())  ;
	                    }else{
	                        charStr = text.substring(start + 2, text.length());
	                    }
	                } else {
	                    charStr = text.substring(start + 2, end);
	                }
	                char letter = (char) Integer.parseInt(charStr.trim(), 16); // 16进制parse整形字符串。
	                buffer.append(new Character(letter).toString());
	                if(StringUtils.isNotBlank(a)){
	                    buffer.append(a);
	                }
	                start = end;
	            }
	        }
	        return buffer.toString();
	    } catch (Exception e){
	    }
	  return text;
	 }
	 
	 
	  
}