package com.va.removeconsult.bean;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class UserMemoryData {
	
	public static final Map<String, HttpSession> USR_SESSION = new HashMap<String, HttpSession>();
	
	public static Map<String, HttpSession> getUsrSession() {
		return USR_SESSION;
	}

	private static Map<String, String> sessionIDMap = new HashMap<String, String>();

	public static Map<String, String> getSessionIDMap() {
		return sessionIDMap;
	}

	public static void setSessionIDMap(Map<String, String> sessionIDMap) {
		UserMemoryData.sessionIDMap = sessionIDMap;
	}
	
	private static Map<String, String> backSessionIDMap = new HashMap<String, String>();

	public static Map<String, String> getBackSessionIDMap() {
		return backSessionIDMap;
	}

	public static void setBackSessionIDMap(Map<String, String> backSessionIDMap) {
		UserMemoryData.backSessionIDMap = backSessionIDMap;
	}

}
