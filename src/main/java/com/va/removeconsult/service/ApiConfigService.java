package com.va.removeconsult.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.va.removeconsult.dao.ApiConfigDao;

@Service  
public class ApiConfigService{

    @Autowired  
    private ApiConfigDao apiConfigDao;
    
    public List<Map<String,Object>> findApiConfigPage(){
    	return apiConfigDao.findApiConfigPage();
    }
    
    
}