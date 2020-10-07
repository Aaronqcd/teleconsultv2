package com.va.removeconsult.service;

import java.security.GeneralSecurityException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.util.SendEmail;
import com.va.removeconsult.util.SendSms;

@Service  
public class AsyncService {
	
	 private static Logger logger = LoggerFactory.getLogger(SendSms.class);
	
	@Resource
	private FolderService folderService;
	

	public void sendMail(String mailTo, String title, String text) {
		String emailAddress = this.folderService.querySysConfValue(SysConfig.EMAIL_ADDRESS.getKey());
		String emailPwd = this.folderService.querySysConfValue(SysConfig.EMAIL_AUTHORIZATION_CODE.getKey());
		sendMail(mailTo, title, text,emailAddress,emailPwd);
	}
	public void send(String mailTo, String title, String text) {
		String emailAddress = this.folderService.querySysConfValue(SysConfig.EMAIL_ADDRESS.getKey());
		String emailPwd = this.folderService.querySysConfValue(SysConfig.EMAIL_AUTHORIZATION_CODE.getKey());
		try {
			SendEmail.send(mailTo, title, text,emailAddress,emailPwd);
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	
	@Async  //异步发送邮件  
	public void sendMail(String mailTo, String title, String text,String adminEmail ,String emailPwd) {
		new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					SendEmail.send(mailTo, title, text,adminEmail,emailPwd);
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}	
			}
		}).start();			
	}
	
	//异步发送短信
	@Async
    public void sendSms( String toPhone,String templateKey , String[] params) {
		try {
			String appId = this.folderService.querySysConfValue("SMS_APPID");
			String appKey = this.folderService.querySysConfValue("SMS_APPKEY");
			String sign = this.folderService.querySysConfValue("SMS_SIGN");
			String templateId = this.folderService.querySysConfValue(templateKey);
			if(StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(appKey) && StringUtils.isNotEmpty(sign) && StringUtils.isNotEmpty(templateId)) {
				SendSms.sendSms(Integer.parseInt(appId), appKey, sign, toPhone, Integer.parseInt(templateId), params);
			}else {
				logger.info(String.format("发送SMS不成功，配置参数为空"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
    }
	
	
}
