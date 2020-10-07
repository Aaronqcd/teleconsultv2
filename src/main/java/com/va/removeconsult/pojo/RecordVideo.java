package com.va.removeconsult.pojo;

import java.util.Date;

public class RecordVideo {
	private int id;
	
	// 视频名称
	private String name;
	
	//录制任务ID
	private String taskId;
	
	//请求时间
	private Date inDate;
	
	//视频开始播放的时间（单位：毫秒）
	private long videoPlayTime;
	
	//视频大小（字节）
	private long videoSize;
	
	//视频格式
	private String videoFormat;
	
	//视频播放时长（单位：毫秒）
	private long videoDuration;
	
	//视频文件URL
	private String videoUrl;
	
	//视频文件Id
	private String videoId;
	
	//视频流类型 0：摄像头视频 1：屏幕分享视频（仅课后录制支持）2：白板视频 3：混流视频 4：纯音频（mp3)
	private long videoType;
	
	//摄像头/屏幕分享视频所属用户的 Id（白板视频为空、混流视频tic_mixstream_房间号_混流布局类型）
	private String userId;

	// 会议ID
	private int meetingId;
	
	// 关联 - 会议编号
	private String meetingNo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Date getInDate() {
		return inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

	public long getVideoPlayTime() {
		return videoPlayTime;
	}

	public void setVideoPlayTime(long videoPlayTime) {
		this.videoPlayTime = videoPlayTime;
	}

	public long getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(long videoSize) {
		this.videoSize = videoSize;
	}

	public String getVideoFormat() {
		return videoFormat;
	}

	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
	}

	public long getVideoDuration() {
		return videoDuration;
	}

	public void setVideoDuration(long videoDuration) {
		this.videoDuration = videoDuration;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public long getVideoType() {
		return videoType;
	}

	public void setVideoType(long videoType) {
		this.videoType = videoType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(int meetingId) {
		this.meetingId = meetingId;
	}

	public String getMeetingNo() {
		return meetingNo;
	}

	public void setMeetingNo(String meetingNo) {
		this.meetingNo = meetingNo;
	}
}