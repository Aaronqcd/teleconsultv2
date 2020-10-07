package com.va.removeconsult.util;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.sun.mail.util.MailSSLSocketFactory;
import com.va.removeconsult.clouddisk.util.ConfigureReader;

public class SendEmail {
	/**
	 * 发送邮件
	 * 
	 * @param email 邮箱号
	 * @param top   邮件头部信息
	 * @param text  邮件主体信息
	 * @param emailAddress 发件人电子邮箱
	 * @param emailPwd   发件人密码
	 * @return 返回是否成功
	 * @throws GeneralSecurityException
	 */
	public static boolean send(String email, String top, String text ,String emailAddress ,String emailPwd) throws GeneralSecurityException {
		// 收件人电子邮箱
		String to = email;

		// 发件人电子邮箱
//		String from = ConfigureReader.instance().getAdminEmail();
		String from = emailAddress;

		// 指定发送邮件的主机为 smtp.qq.com
		String host = "smtp.qq.com"; // QQ 邮件服务器

		Utils.log(String.format("Sending email to: %s", to));
		Utils.log(String.format("Sending email from: %s", emailPwd));

		// 获取系统属性
		Properties properties = System.getProperties();

		// 设置邮件服务器
		properties.setProperty("mail.smtp.host", host);

		properties.put("mail.smtp.auth", "true");
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.ssl.socketFactory", sf);
		// 获取默认session对象
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
//				String emailFrom = ConfigureReader.instance().getAdminEmail(); // 发件人帐号
//				String emailAuth = ConfigureReader.instance().getAdminEmailAuth(); // 发件人密码
				return new PasswordAuthentication(emailAddress, emailPwd);
			}
		});

		try {
			// 创建默认的 MimeMessage 对象
			MimeMessage message = new MimeMessage(session);

			// Set From: 头部头字段
			message.setFrom(new InternetAddress(from));

			// Set To: 头部头字段
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			/*
			 * String[] split = to.split(","); Address[] addresses = new
			 * Address[split.length]; for (int i=0;i<addresses.length;i++) {
			 * addresses[i]=new InternetAddress(split[i]); }
			 * message.addRecipients(Message.RecipientType.TO, addresses);
			 */
			// Set Subject: 头部头字段
			message.setSubject(top);

			// 设置消息体
			message.setText(text);

			// 发送消息
			Utils.log("正在发送邮件 ...");
			Transport.send(message);
			Utils.log("发送邮件成功!");
			return true;
		} catch (MessagingException ex) {
			Utils.error("发送邮件失败!", ex);
			return false;
		}
	}

	public static String formatTemplate(String context, Map<String, Object> param) {
		for (String key : param.keySet()) {
			Object value = param.get(key);
			String val = "";
			if (value instanceof Date || value instanceof java.sql.Date) {
				val = getDateToString(value);
			}
			if (value == null) {
			} else {
				val = value.toString();
			}
			context = context.replaceAll("\\$\\{" + key + "\\}", val);
		}
		return context;
	}

	public static String getDateToString(Object date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	public static void main(String[] args) throws GeneralSecurityException {
		// 收件人电子邮箱
		String to = "2382653766@qq.com";

		// 发件人电子邮箱
		String from = "1191250110@qq.com";

		// 指定发送邮件的主机为 smtp.qq.com
		String host = "smtp.qq.com"; // QQ 邮件服务器

		// 获取系统属性
		Properties properties = System.getProperties();

		// 设置邮件服务器
		properties.setProperty("mail.smtp.host", host);

		properties.put("mail.smtp.auth", "true");
		MailSSLSocketFactory sf = new MailSSLSocketFactory();
		sf.setTrustAllHosts(true);
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.ssl.socketFactory", sf);
		// 获取默认session对象
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("1191250110@qq.com", "qpyapnactmxpijff"); // 发件人邮件用户名、密码
			}
		});
		session.setDebug(true);

		try {
			// 创建默认的 MimeMessage 对象
			MimeMessage message = new MimeMessage(session);

			// Set From: 头部头字段
			message.setFrom(new InternetAddress(from));

			// Set To: 头部头字段
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: 头部头字段
			message.setSubject("测试邮件发送功能给wei工");

			// 设置消息体
			message.setText("这是主体内容:测试邮件发送功能给wei工");

			// 发送消息
			Transport.send(message);
			System.out.println("Sent message successfully....from runoob.com");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}