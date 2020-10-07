package com.va.removeconsult.util;

public enum ErrorMsgEnum {

	USER_REPLACE("USER_REPLACE","您的账号正在另一客户端登录，或您的账号登录超时!"),
	NOT_FIND_MEETING("NOT_FIND_MEETING","会诊被删除或禁用了!"),
	NOT_FIND_MEETING_USER("NOT_FIND_MEETING_USER","该会诊主持人不存在了!"),
	UNKNOWN_ERROR("UNKNOWN_ERROR","未知错误，联系技术员!");
	private ErrorMsgEnum(String code, String msg) { 
        this.code = code; 
        this.msg = msg; 
	} 
	
	private String code;
	
	private String msg;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	

}
