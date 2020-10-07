package com.va.removeconsult.controller.frontend;

import java.util.*;
import javax.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import com.va.removeconsult.service.*;
import com.va.removeconsult.dto.AjaxResult;
import com.va.removeconsult.pojo.DeviceMessage;

@Controller
public class DeviceController {

	@Resource
	private DeviceService deviceService;

	@Resource
	private ResourceService resourceService;

	private ModelAndView getJsonView(int code, String message, Map<String, Object> data) {
		AjaxResult model = new AjaxResult();
		model.put("code", code);
		model.put("msg", message);
		model.put("data", data);
		return this.getJsonView(model);
	}

	private ModelAndView getJsonView(AjaxResult model) {
		ModelAndView result = new ModelAndView("/json");
		result.addObject("json", JSON.toJSONString(model));
		return result;
	}

	@RequestMapping(value = "/meeting/live", method = RequestMethod.GET)
	public ModelAndView checkLive(@RequestParam int id) {
		return this.getJsonView(deviceService.checkLive(id));
	}

	/**
	 * 打开视频设备
	 */
	@RequestMapping(value = "/device/open", method = RequestMethod.POST)
	public ModelAndView open(@RequestBody Map<String, Object> input) {
		int userId, meetingId, channel;
		String device;
		try {
			userId = (int) input.get("user");
			meetingId = (int) input.get("meeting");
			channel = (int) input.get("channel");
			device = (String) input.get("device");
			if (device == null || device.isEmpty()) {
				return this.getJsonView(1, "设备ID不能为空", null);
			}
		} catch (Exception ex) {
			return this.getJsonView(1, "无效的输入参数", null);
		}
		return this.getJsonView(deviceService.beginOpen(userId, meetingId, device, channel));
	}

	/**
	 * 关闭视频设备
	 */
	@RequestMapping(value = "/device/close", method = RequestMethod.POST)
	public ModelAndView close(@RequestBody Map<String, Object> input) {
		int userId, meetingId, channel;
		String device;
		try {
			userId = (int) input.get("user");
			meetingId = (int) input.get("meeting");
			device = (String) input.get("device");
			channel = (int) input.get("channel");
		} catch (Exception ex) {
			return this.getJsonView(1, "无效的输入参数", null);
		}
		return this.getJsonView(deviceService.beginClose(userId, meetingId, device, channel));
	}

	/*
	 * 临时做法，配合视频设备未实现的功能
	 */
	@RequestMapping(value = "/device/open2", method = RequestMethod.POST)
	public ModelAndView open2(@RequestBody Map<String, Object> input) {
		int id, userId, meetingId, channel;
		String device;
		try {
			id = (int) input.get("id");
			userId = (int) input.get("user");
			meetingId = (int) input.get("meeting");
			channel = (int) input.get("channel");
			device = (String) input.get("device");
			if (channel < 1 || channel > 8) {
				return this.getJsonView(1, "视频通道无效: " + channel, null);
			}
		} catch (Exception ex) {
			return this.getJsonView(1, "无效的输入参数", null);
		}
		return this.getJsonView(deviceService.beginOpen2(id, userId, meetingId, device, channel));
	}

	/*
	 * 临时做法，配合视频设备未实现的功能
	 */
	@RequestMapping(value = "/device/close2", method = RequestMethod.POST)
	public ModelAndView close2(@RequestBody Map<String, Object> input) {
		int userId, meetingId, channel;
		String device;
		try {
			userId = (int) input.get("user");
			meetingId = (int) input.get("meeting");
			device = (String) input.get("device");
			channel = (int) input.get("channel");
			if (channel < 1 || channel > 8) {
				return this.getJsonView(1, "视频通道无效: " + channel, null);
			}
		} catch (Exception ex) {
			return this.getJsonView(1, "无效的输入参数", null);
		}
		return this.getJsonView(deviceService.beginClose2(userId, meetingId, device, channel));
	}

	/**
	 * 获取设备消息
	 */
	@RequestMapping(value = "/devmsg/get")
	public ModelAndView getMessage(@RequestParam String id) {
		return this.getJsonView(deviceService.getMessagesOfDevice(id));
	}

	/**
	 * 响应设备消息
	 */
	@RequestMapping(value = "/devmsg/reply", method = RequestMethod.POST)
	public ModelAndView replyMessage(@RequestBody Map<String, Object> input) {
		int id, code;
		String msg;
		try {
			id = (int) input.get("id");
			code = (int) input.get("code");
			msg = (String) input.get("msg");
		} catch (Exception e) {
			return this.getJsonView(1, "无效的输入参数", null);
		}
		return this.getJsonView(deviceService.replyMessage(id, code, msg));
	}
}