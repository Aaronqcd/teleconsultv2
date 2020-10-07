package com.va.removeconsult.clouddisk.pojo;

import java.util.List;


public class CheckUploadFilesRespons {
	
	private String checkResult;
	private String uploadKey;
	private List<String> pereFileNameList;
	
	public String getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}
	public String getUploadKey() {
		return uploadKey;
	}
	public void setUploadKey(String uploadKey) {
		this.uploadKey = uploadKey;
	}
	public List<String> getPereFileNameList() {
		return pereFileNameList;
	}
	public void setPereFileNameList(List<String> pereFileNameList) {
		this.pereFileNameList = pereFileNameList;
	}
}
