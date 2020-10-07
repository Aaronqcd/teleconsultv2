package com.va.removeconsult.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Select;

public interface TblRoomDao {
    
	@Select(" SELECT t.* FROM `tbl_room` t WHERE t.meetingID=#{meetingID} ORDER BY t.id DESC LIMIT 1  ")
    public Map<String,Object> getTblRoomByMeetingID(String meetingID);
	
	
}