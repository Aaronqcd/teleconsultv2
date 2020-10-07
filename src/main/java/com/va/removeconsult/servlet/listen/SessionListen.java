package com.va.removeconsult.servlet.listen;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import com.va.removeconsult.bean.UserMemoryData;


/**
 * 监听session的变化，
 * @author andong
 * @version 1.0
 *
 */
public class SessionListen implements HttpSessionAttributeListener{


	public void attributeAdded(HttpSessionBindingEvent se) {
		
	}

	public void attributeRemoved(HttpSessionBindingEvent se) {
		String str = se.getName();
		Object obj = se.getValue();
		//如果删除的是Users对象
		if(obj instanceof String){
			if("loginuser".equals(str)){
				//得到之前登陆该账号的用户session，并删除登陆信息
				HttpSession session = (HttpSession)UserMemoryData.getUsrSession().get(obj.toString());
				//删除登陆信息
				session.removeAttribute("loginuser");
				//该msg用于提示用户账号已在另一地点登陆，会通过ajax定时访问一个sevlet，该servlet会调用这个属性
				session.setAttribute("msg", "您的账号已在另一地点登陆!");
				
			}
		}
	}

	public void attributeReplaced(HttpSessionBindingEvent se) {
		
	}
}
