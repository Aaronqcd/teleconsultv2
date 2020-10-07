package com.va.removeconsult.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;  
import org.springframework.beans.factory.annotation.Autowired;

import com.va.removeconsult.dao.DbDao;
import com.va.removeconsult.dao.MeetingDao;
import com.va.removeconsult.service.MeetingService;
import com.va.removeconsult.service.UserService;
import com.va.removeconsult.test.SpringTestCase;  
import com.alibaba.fastjson.JSON;

public class UserServiceTest extends SpringTestCase {
    @Autowired  
    private DbDao db;

    
    @Test
    public void selectItems(){
    	List <Map> is;
    	String sql="select * from user where id=1";
    	is=db.select(sql);
    	//String s=JSON.toJSONString(is);
    	
    	sql="update user set name='ooo' where id=1";
    	int r=db.update(sql);
    	System.out.println(r);
    }
}