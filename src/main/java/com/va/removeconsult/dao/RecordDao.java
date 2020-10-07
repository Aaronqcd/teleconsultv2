package com.va.removeconsult.dao;

import java.util.*;
import org.apache.ibatis.annotations.*;
import com.va.removeconsult.pojo.*;

public interface RecordDao {
	@Select("SELECT count(1) FROM record_video a LEFT JOIN meeting b ON a.meetingId = b.id"
			+ " WHERE instr(concat(',',a.attends,','),'${userId}')>0 AND" +
			" (#{searchValue} is null or a.name like #{searchValue} or b.no like #{searchValue})" +
			" AND (#{meetingId}=0 or a.meetingId=#{meetingId})")
	public int countVideo(Map<String, Object> params);

	@Select("SELECT a.*, b.no meetingNo FROM record_video a LEFT JOIN meeting b ON a.meetingId = b.id" 
			+" WHERE instr(concat(',',a.attends,','),'${userId}')>0 AND" +
			" (#{searchValue} is null or a.name like #{searchValue} or b.no like #{searchValue}) " +
			" AND (#{meetingId}=0 or a.meetingId=#{meetingId})"
			+ " ORDER BY #{sort} #{direction}"
			+ " LIMIT #{offset}, #{limit}")
	public List<RecordVideo> queryVideo(Map<String, Object> params);

	@Select("SELECT b.id,b.no FROM record_video a LEFT JOIN meeting b ON a.meetingId = b.id" 
			+" WHERE instr(concat(',',a.attends,','),'${userId}')>0" 
			+ " GROUP BY b.id, b.no"
			+ " ORDER BY b.id DESC")
	public List<Map<String, Object>> queryMeetings(Map<String, Object> params);

	@Select("SELECT * FROM record_video WHERE id = #{id}")
	public RecordVideo detailVideo(int id);

	@Delete("DELETE FROM record_video WHERE id = #{id}")
	public void deleteVideo(int id);
	
	@Delete("DELETE FROM record_video WHERE id in (${ids})")
	public void deleteVideos(@Param("ids")String ids);

	@Update("UPDATE record_video SET name=#{name} WHERE id=#{id}")
	public void renameVideo(@Param("id")int id, @Param("name")String name);
}