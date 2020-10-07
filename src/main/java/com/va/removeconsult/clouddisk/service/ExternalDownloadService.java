package com.va.removeconsult.clouddisk.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface ExternalDownloadService {
	
	
	String getDownloadKey(HttpServletRequest request);
	
	
	void downloadFileByKey(HttpServletRequest request,HttpServletResponse response);

}
