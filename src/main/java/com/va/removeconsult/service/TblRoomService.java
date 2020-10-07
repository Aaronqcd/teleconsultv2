package com.va.removeconsult.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.va.removeconsult.dao.TblRoomDao;

@Service  
public class TblRoomService{

    @Autowired  
    private TblRoomDao tblRoomDao;
    
    public Map<String,Object> getTblRoomByMeetingID(String meetingID){
    	return tblRoomDao.getTblRoomByMeetingID(meetingID);
    }
    
    
}