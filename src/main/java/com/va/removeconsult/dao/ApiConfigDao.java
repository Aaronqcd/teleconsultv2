package com.va.removeconsult.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

public interface ApiConfigDao {
    
	@Select("  SELECT t.id,t.`name`,t.hosp_url as hospUrl,t.app_id as appId,t.app_key as appKey FROM `api_config` t ORDER BY t.create_time ASC   ")
    public List<Map<String,Object>> findApiConfigPage();
	
	
}