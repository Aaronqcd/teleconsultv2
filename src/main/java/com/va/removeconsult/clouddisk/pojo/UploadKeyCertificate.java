package com.va.removeconsult.clouddisk.pojo;


public class UploadKeyCertificate {
	
	private int term;
	private String account;
	
	
	public UploadKeyCertificate(int term,String account) {
		this.term=term;
		this.account=account;
	}
	
	
	public void checked() {
		term--;
	}
	
	
	public boolean isEffective() {
		return term > 0;
	}
	
	
	public String getAccount() {
		return account;
	}

}
