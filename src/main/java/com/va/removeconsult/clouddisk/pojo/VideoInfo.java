package com.va.removeconsult.clouddisk.pojo;

import com.va.removeconsult.clouddisk.model.Node;


public class VideoInfo extends Node{
	
	public VideoInfo(Node n) {
		this.setFileId(n.getFileId());
		this.setFileName(n.getFileName());
		this.setFileParentFolder(n.getFileParentFolder());
		this.setFilePath(n.getFilePath());
		this.setFileSize(n.getFileSize());
		this.setFileCreationDate(n.getFileCreationDate());
		this.setFileCreator(n.getFileCreator());
	}
	
	private String needEncode;

	public String getNeedEncode() {
		return needEncode;
	}

	public void setNeedEncode(String needEncode) {
		this.needEncode = needEncode;
	}
	
}
