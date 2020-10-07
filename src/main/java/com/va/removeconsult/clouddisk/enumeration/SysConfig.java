package com.va.removeconsult.clouddisk.enumeration;

public enum SysConfig {
	MAX_UPLOAD_SIZE("MAX_UPLOAD_SIZE","524288000"), //最大文件上传大小,默认500M(单位字节)
	DISK_SIZE("DISK_SIZE","2147483648"), //个人云盘最大容量,默认2GB(单位字节)
	CONF_TEUDBORAD("CONF_TEUDBORAD","0"), //配置开通白板功能,(0未开通, 1为开通 ) 默认为0
	USER_SIZE("USER_SIZE","30"),		//添加和批量导入用户的最大限制
	EMAIL_HOSPITAL_NAME("EMAIL_HOSPITAL_NAME","中山医院"),//邮件模板要替换的医院名称
	EMAIL_HOSPITAL_URL("EMAIL_HOSPITAL_URL","http://localhost/teleconsult/Login"),//邮件模板要替换的医院访问地址	
	EMAIL_ADDRESS("EMAIL_ADDRESS","727470665@qq.com"),//系统邮箱地址	
	EMAIL_AUTHORIZATION_CODE("EMAIL_AUTHORIZATION_CODE","vmhawxjllcptbdhi"),//系统邮箱授权码	
	MEETING_CYCLE("MEETING_CYCLE","180"),//会诊数据存储周期，单位(天)	
	SYS_LOGO("SYS_LOGO","images/logo.png");//会诊数据存储周期，单位(天)	
	private SysConfig(String key, String value) {
        this.key = key; 
        this.value = value; 
	}
	
	private String key;
	
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}