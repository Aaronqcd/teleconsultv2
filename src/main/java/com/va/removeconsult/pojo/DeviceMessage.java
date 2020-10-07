package com.va.removeconsult.pojo;

import java.util.*;

public class DeviceMessage {
	private int id;
	private String deviceId;
	private int channel;
	private int userId;
	private int meetingId;
	private int sendCommand;
	private String sendBody;
	private Date sendTime;
	private boolean isReply;
	private int replyCode;
	private String replyBody;
	private Date replyTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(int meetingId) {
		this.meetingId = meetingId;
	}

	public int getSendCommand() {
		return sendCommand;
	}

	public void setSendCommand(int sendCommand) {
		this.sendCommand = sendCommand;
	}

	public String getSendBody() {
		return sendBody;
	}

	public void setSendBody(String sendBody) {
		this.sendBody = sendBody;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public boolean isReply() {
		return isReply;
	}

	public void setReply(boolean isReply) {
		this.isReply = isReply;
	}

	public int getReplyCode() {
		return replyCode;
	}

	public void setReplyCode(int replyCode) {
		this.replyCode = replyCode;
	}

	public String getReplyBody() {
		return replyBody;
	}

	public void setReplyBody(String replyBody) {
		this.replyBody = replyBody;
	}

	public Date getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(Date replyTime) {
		this.replyTime = replyTime;
	}
}