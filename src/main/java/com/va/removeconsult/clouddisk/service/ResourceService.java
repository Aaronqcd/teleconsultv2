package com.va.removeconsult.clouddisk.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResourceService {
	
	
	public void getResource(HttpServletRequest request,HttpServletResponse response);
	
	
	public void getWordView(String fileId,HttpServletRequest request,HttpServletResponse response);
	
	
	public void getTxtView(String fileId,HttpServletRequest request,HttpServletResponse response);
	
	public void getPdfView(String fileId,HttpServletRequest request,HttpServletResponse response);
	
	
	public String getVideoTranscodeStatus(HttpServletRequest request);

}
