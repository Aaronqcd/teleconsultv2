package com.va.removeconsult.dao;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.alibaba.fastjson.JSONObject;

public interface MeetingDao {
	//这两个功能已经在db类里面实现，可以删除
	@SelectProvider(type=MeetingListSql.class,method="selectWhitParam")
    public ArrayList<Map<String,String>> getMeetings(Map<String,Object> param);
	
	@SelectProvider(type=MeetingListSql.class,method="selectCountWhitParam")
    public String getMeetingCount(Map<String,Object> param);
	
	@Select("SELECT * FROM meeting WHERE id = #{id}")
    public Map<String,Object> selectMeetingById(int id);
	
	@Update("update meeting set user= #{user}, attends=#{attends} where id = #{id}")
    public int updateUser(@Param("user")int user,@Param("attends")String attends,@Param("id")int id);
	
	@Update("update meeting set attaId= #{attaId} where id = #{id}")
    public int updatMeetingeByAttaId(@Param("attaId")String attaId,@Param("id")int id);
	
	@Select("SELECT * FROM meeting m LEFT JOIN `user` u on u.id=m.`user` LEFT JOIN attachment a on a.sysName = u.signature WHERE m.code = #{code}")
    public Map<String,Object> selectMeetingByCode(String code);
	
	@Select("SELECT c.user FROM tbl_room a LEFT JOIN meeting b on a.meetingID = b.id LEFT JOIN user c on b.user = c.id WHERE a.groupID = #{meetingId}")
	public JSONObject findMeetingName(int meetingId);
}