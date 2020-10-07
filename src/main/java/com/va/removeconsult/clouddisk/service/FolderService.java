package com.va.removeconsult.clouddisk.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.va.removeconsult.clouddisk.model.Folder;

public interface FolderService
{
    String newFolder(final HttpServletRequest request);
    
    String deleteFolder(final HttpServletRequest request);
    
    String renameFolder(final HttpServletRequest request);
    
    String getFolderTreeJson(final HttpServletRequest request);
    
    void createRootFolder(final HttpServletRequest request);
    
    Folder queryRootFolder(final HttpServletRequest request);
    
    void createMeetingFolder(final HttpServletRequest request, int meetingId);
    
    void createMeetingFolder(HttpServletRequest request, String meetingNo, String attaId);
    
    void createMeetingFolder(Map<String, Object> user ,int meetingId, String attaId);
    
    void createMeetingFolderShare(Map<String, Object> user ,int meetingId, String attaId);
    
    void deleteByParentSourceMeeting(String mid);
    
    String querySysConfValue(final String key);
    
    long queryRootFolderSize(Integer userId);
    
    long queryUserDiskConf(final String key, Map user);
    
    
    public int updateAttachmentByShareUser(String id,String shareUser);
    
    
    public int updateAttachmentByIsShare(String id,int isShare);
    
    public void deleteOverdueFolder(String meetimgCycle);
    
}
