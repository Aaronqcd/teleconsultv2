package com.va.removeconsult.dao;

import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.pojo.SysConf;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

public interface FolderDao
{
    Folder queryById(final String fid);
    
    List<Folder> queryByParentId(final String pid);
    
    Folder queryByParentIdAndFolderName(final Map<String, String> map);
    
    int insertNewFolder(final Folder f);
    
    int deleteById(final String folderId);
    
    int updateFolderNameById(final Map<String, String> map);
    
    int updateFolderConstraintById(final Map<String, Object> map);

	int moveById(Map<String, String> map);
	
	List<Folder> queryByUserId(final Map<String, Object> map);

	List<Folder> queryByNameAndUser(final Map<String, Object> map);
	
	Folder queryRootByUserId(final Integer userId);

    SysConf querySysConf(String confKey);
    
    @Select("SELECT * FROM `disk_folder` t where t.folder_name=#{folderName}  LIMIT 1")
    Map<String,Object> queryByFolderName(String folderName);
    
    @Select("SELECT * FROM `disk_folder` t where t.folder_user_id=#{userId} and t.folder_name=#{folderName}   LIMIT 1")
    Map<String,Object> queryByFolderNameByUserId(Map<String, String> map);
    
    @Select("SELECT * FROM `disk_folder` t where t.folder_user_id=#{userId} and t.folder_name=#{folderName} and t.folder_parent=#{folderParent}  LIMIT 1")
    Map<String,Object> queryByFolderNameByUserIdParent(Map<String, String> map);

    List<Folder> queryOverdueFolder();
    
    
}
