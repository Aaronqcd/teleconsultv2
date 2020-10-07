package com.va.removeconsult.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.lang.reflect.*;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import com.va.removeconsult.dao.*;
import com.va.removeconsult.pojo.*;
import com.va.removeconsult.dto.*;

@Service
public class DeviceService {
	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private MeetingDao meetingDao;

	@Autowired
	private ResourceDao resDao;

	@Autowired
	private UserDao userDao;

	public AjaxResult checkLive(int meetingId) {
		AjaxResult result = AjaxResult.success();

		// TODO: 权限检测

		String sql = String.format("select * from live_video where meetingId=%d and `status` in(1,2) limit 1",
				meetingId);
		LiveVideo video = deviceDao.selectLiveOne(sql);
		if (video != null) {
			Map<String, Object> user = userDao.getUserById(video.getUserId());
			Video res = resDao.getVideoByIP(video.getDeviceId(), (int) user.get("belong"));
			Map<String, Object> data = new HashMap<String, Object>();
			Field[] fields = video.getClass().getDeclaredFields();
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					data.put(field.getName(), field.get(video));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			data.put("roomName", res.getRoomname());
			data.put("channelName", res.getV1name());
			result.put("data", data);
		}
		return result;
	}

	public AjaxResult beginOpen(int userId, int meetingId, String deviceId, int channel) {
		// 检测用户权限
		Map<String, Object> meeting = meetingDao.selectMeetingById(meetingId);
		if (meeting == null) {
			return AjaxResult.error("会诊ID无效");
		}
		if ((int) meeting.get("status") != 3) {
			return AjaxResult.error("会诊状态无效");
		}
		if ((int) meeting.get("user") != userId) {
			return AjaxResult.error("权限不足");
		}

		// 检测视频端口是否已打开
		String sql = String.format(
				"select * from live_video where deviceId='%s' and `channel`=%d and `status` in (1,2) limit 1", deviceId,
				channel); // 1=正在打开 2=已打开
		LiveVideo oldLive = deviceDao.selectLiveOne(sql);
		if (oldLive != null) {
			return AjaxResult.error(1, "设备已打开");
		}

		// 添加视频记录
		LiveVideo newLive = new LiveVideo();
		newLive.setUserId(userId);
		newLive.setMeetingId(meetingId);
		newLive.setDeviceId(deviceId);
		newLive.setChannel(channel);
		newLive.setPushUrl(this.getPushUrl(meetingId, deviceId, channel));
		newLive.setPlayUrl(this.getPlayUrl(meetingId, deviceId, channel));
		newLive.setStatus(1); // 正在打开
		deviceDao.insertLive(newLive);

		// 添加设备消息
		DeviceMessage message = new DeviceMessage();
		message.setUserId(userId);
		message.setMeetingId(meetingId);
		message.setDeviceId(deviceId);
		message.setChannel(channel);
		message.setSendTime(new Date());
		message.setSendCommand(EDeviceCommand.Open.getValue());
		message.setSendBody(newLive.getPushUrl());
		deviceDao.insertMessage(message);

		AjaxResult result = AjaxResult.success();
		result.put("data", newLive);
		return result;
	}

	public AjaxResult beginClose(int userId, int meetingId, String deviceId, int channel) {
		// 检测用户权限
		Map<String, Object> meeting = meetingDao.selectMeetingById(meetingId);
		if (meeting == null) {
			return AjaxResult.error("会诊ID无效");
		}
		if ((int) meeting.get("status") != 3) {
			return AjaxResult.error("会诊状态无效");
		}
		if ((int) meeting.get("user") != userId) {
			return AjaxResult.error("权限不足");
		}

		// 检测视频端口是否已打开
		String sql = String.format(
				"update live_video set `status`=3 where deviceId='%s' and `channel`=%d and `status` in (1,2)", deviceId,
				channel); // 1=正在打开 2=已打开
		deviceDao.updateLive(sql);

		// 添加设备消息
		DeviceMessage message = new DeviceMessage();
		message.setUserId(userId);
		message.setMeetingId(meetingId);
		message.setDeviceId(deviceId);
		message.setChannel(channel);
		message.setSendTime(new Date());
		message.setSendCommand(EDeviceCommand.Close.getValue());
		deviceDao.insertMessage(message);

		return AjaxResult.success();
	}

	/*
	 * 临时做法，配合视频设备未实现的功能
	 */
	public AjaxResult beginOpen2(int resId, int userId, int meetingId, String ip, int channel) {

		Video video = resDao.getVideo(resId);
		if (video == null) {
			return AjaxResult.error("视频设备无效: " + resId);
		}

		// 添加视频记录
		LiveVideo newLive = new LiveVideo();
		newLive.setUserId(userId);
		newLive.setMeetingId(meetingId);
		newLive.setDeviceId(ip);
		newLive.setChannel(channel);
		newLive.setPushUrl(null); // 忽略推流地址
		switch (channel) {
		case 1:
			newLive.setPlayUrl(video.getV1add());
			break;
		case 2:
			newLive.setPlayUrl(video.getV2add());
			break;
		case 3:
			newLive.setPlayUrl(video.getV3add());
			break;
		case 4:
			newLive.setPlayUrl(video.getV4add());
			break;
		case 5:
			newLive.setPlayUrl(video.getV5add());
			break;
		case 6:
			newLive.setPlayUrl(video.getV6add());
			break;
		case 7:
			newLive.setPlayUrl(video.getV7add());
			break;
		case 8:
			newLive.setPlayUrl(video.getV8add());
			break;
		default:
			return AjaxResult.error("视频通道无效: " + channel);
		}
		newLive.setStatus(2); // 打开成功
		deviceDao.insertLive(newLive);
		
		AjaxResult result = AjaxResult.success();
		result.put("data", newLive);
		return result;
	}

	/*
	 * 临时做法，配合视频设备未实现的功能
	 */
	public AjaxResult beginClose2(int userId, int meetingId, String deviceId, int channel) {
		// 检测视频端口是否已打开
		String sql = String.format(
				"update live_video set `status`=3 where deviceId='%s' and `channel`=%d and `status` in (1,2)", deviceId,
				channel); // 1=正在打开 2=已打开
		deviceDao.updateLive(sql);

		return AjaxResult.success();
	}

	public AjaxResult getMessagesOfDevice(String deviceId) {
		AjaxResult result = AjaxResult.success(null);
		List<DeviceMessage> models = deviceDao.getMessagesOfDevice(deviceId);
		if (models != null && models.size() > 0) {
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			for (DeviceMessage model : models) {
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("id", model.getId());
				row.put("device", model.getDeviceId());
				row.put("channel", model.getChannel());
				row.put("command", model.getSendCommand());
				row.put("param", model.getSendBody());
				row.put("time", model.getSendTime());
				data.add(row);
			}
			result.put("data", data);
		}
		return result;
	}

	public AjaxResult replyMessage(int msgId, int code, String error) {
		String sql = String.format("update device_message set isReply=1,replyCode=%d,%sreplyTime=now() where id=%d",
				code, error == null ? "" : String.format("replyBody='%s',", error), msgId);
		// 响应消息
		deviceDao.updateMessage(sql);

		DeviceMessage msg = deviceDao.getMessageById(msgId);
		if (msg == null) {
			return AjaxResult.error("消息ID无效");
		}
		EDeviceCommand command = EDeviceCommand.fromValue(msg.getSendCommand());
		sql = null;
		// 更改设备状态
		if (code == 0) {
			switch (command) {
			case Open: // 打开成功
				sql = String.format(
						"update live_video set `status`=2, openTime=now() where deviceId='%s' and `channel`=%d and `status`=1",
						msg.getDeviceId(), msg.getChannel());
				break;
			case Close: // 关闭成功
				sql = String.format(
						"update live_video set `status`=0, closeTime=now() where deviceId='%s' and `channel`=%d and `status`=3",
						msg.getDeviceId(), msg.getChannel());
				break;
			}
		} else {
			switch (command) {
			case Open: // 打开失败
				sql = String.format(
						"update live_video set `status`=0 where deviceId='%s' and `channel`=%d and `status`=1",
						msg.getDeviceId(), msg.getChannel());
				break;
			case Close: // 关闭失败
				// do nothing
				break;
			}
		}
		if (sql != null) {
			deviceDao.updateLive(sql);
		}
		return AjaxResult.success(null);
	}

	public String getPushUrl(int meetingId, String device, int channel) {
		ResourceBundle bundle = ResourceBundle.getBundle("properties/livevideo");
		String protocal = bundle.getString("push.protocal");
		String domain = bundle.getString("push.domain");
		String app = bundle.getString("push.app");
		String key = bundle.getString("push.key");
		String stream = this.getStreamName(meetingId, device, channel);
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_MONTH, 1); // 有效时间1天
		String sign = getSign(key, stream, date.getTimeInMillis() / 1000);
		return String.format("%s://%s/%s/%s?%s", protocal, domain, app, stream, sign);
	}

	public String getPlayUrl(int meetingId, String device, int channel) {
		ResourceBundle bundle = ResourceBundle.getBundle("properties/livevideo");
		String protocal = bundle.getString("play.protocal");
		String domain = bundle.getString("play.domain");
		String app = bundle.getString("play.app");
		String key = bundle.getString("play.key");
		String format = bundle.getString("play.format");
		String stream = this.getStreamName(meetingId, device, channel);
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_MONTH, 1); // 有效时间1天
		String sign = getSign(key, stream, date.getTimeInMillis() / 1000);
		return String.format("%s://%s/%s/%s%s?%s", protocal, domain, app, stream, format, sign);
	}

	private String getStreamName(int meetingId, String device, int channel) {
		return String.format("m%dd%sc%d", meetingId, device, channel);
	}

	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	/*
	 * KEY+ streamName + txTime
	 */
	private static String getSign(String key, String streamName, long txTime) {
		String hexTime = Long.toHexString(txTime).toUpperCase();
		String input = new StringBuilder().append(key).append(streamName).append(hexTime).toString();

		String txSecret = null;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			txSecret = byteArrayToHexString(messageDigest.digest(input.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return txSecret == null ? ""
				: new StringBuilder().append("txSecret=").append(txSecret).append("&txTime=").append(hexTime)
						.toString();
	}

	private static String byteArrayToHexString(byte[] data) {
		char[] out = new char[data.length << 1];

		for (int i = 0, j = 0; i < data.length; i++) {
			out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS_LOWER[0x0F & data[i]];
		}
		return new String(out);
	}
}
