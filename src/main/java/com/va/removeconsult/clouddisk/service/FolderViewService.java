package com.va.removeconsult.clouddisk.service;

import javax.servlet.http.*;


public interface FolderViewService{
	
	
    String getFolderViewToJson(final String fid, final HttpSession session, final HttpServletRequest request);
    
    
    String getSreachViewToJson(final HttpServletRequest request);
    
    
    String getTableViewToJson(final String fid, final HttpSession session, final HttpServletRequest request);
}
