package com.va.removeconsult.dao;

import java.util.List;

import org.apache.ibatis.annotations.*;
import com.va.removeconsult.pojo.*;

public interface DeviceDao {
	@Insert("insert into device_message (deviceId, `channel`, userId, meetingId, sendCommand, sendBody, sendTime, isReply, replyCode, replyBody, replyTime)\r\n"
			+ "values (#{deviceId}, #{channel}, #{userId}, #{meetingId},#{sendCommand},#{sendBody},#{sendTime},#{isReply},#{replyCode},#{replyBody},#{replyTime});")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int insertMessage(DeviceMessage model);

	@Update("${value}")
	public int updateMessage(String value);

	@Delete("delete from device_message where id = #{id}")
	public int deleteMessage(int id);

	@Select("select * from device_message where id=#{id}")
	public DeviceMessage getMessageById(int id);

	@Select("select * from device_message where deviceId=#{deviceId} and isReply=0")
	public List<DeviceMessage> getMessagesOfDevice(String deviceId);

	@Update("update device_message set isReply=1,replyCode=#{replyCode},replyBody=#{replyBody},replyTime=now() where id=#{id};")
	public void replyMessage(int id, int replyCode, String replyBody);

	@Insert("insert into live_video(userId, meetingId, deviceId, `channel`, pushUrl, playUrl, `status`, openTime, closeTime)\r\n"
			+ "values (#{userId},#{meetingId},#{deviceId},#{channel},#{pushUrl},#{playUrl},#{status},#{openTime},#{closeTime});")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int insertLive(LiveVideo model);

	@Update("${value}")
	public int updateLive(String value);

	@Delete("delete from live_video where id=#{id}")
	public int deleteLive(int id);

	@Select("${value}")
	public LiveVideo selectLiveOne(String value);

	@Select("select * from live_video where id=#{id}")
	public LiveVideo selectLiveById(int id);

}
