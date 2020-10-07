package com.va.removeconsult.controller.frontend;

import java.util.*;
import javax.annotation.*;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import com.va.removeconsult.pojo.organization;
import com.va.removeconsult.service.*;

@Controller
@RequestMapping("/patient")
public class PatientController {
	@Resource
	private PatientService patientService;
	@Autowired
	private UserService userService;
	@Autowired
	private ResourceService resourceService;

	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public ModelAndView select() {
		ModelAndView result = new ModelAndView("/PatientSelect");
		return result;
	}

	@RequestMapping(value = "/select", method = RequestMethod.POST)
	public ModelAndView search(HttpServletRequest request) {
		ModelAndView result = new ModelAndView("/json");
		List<Map<String, Object>> jsonData = new ArrayList<>();
		int count = 0;
		String patientId = request.getParameter("patientid");
		String inDate = request.getParameter("indate");
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));

		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put("page", page);
		filters.put("limit", limit);
		filters.put("resType", request.getParameter("restype"));
		filters.put("resId", request.getParameter("resid"));
		filters.put("patientId", patientId);
		filters.put("inDate", inDate);
		jsonData = patientService.queryPatient(filters);

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("code", "0");
		jsonMap.put("msg", "");
		jsonMap.put("count", count);
		jsonMap.put("data", jsonData);
		result.addObject("json", JSON.toJSONString(jsonMap));
		return result;
	}

	@RequestMapping(value = "/data4s")
	public ModelAndView get4S(HttpServletRequest request) {
		ModelAndView result = new ModelAndView("/Patient4SReport");
		Map<String, Object> user = userService.getLoginInfo(request);
		if (user == null)
			return null;
		List<organization> orgList = resourceService.getOrgPid((int) user.get("belong"));
		if (orgList == null || orgList.size() == 0)
			return null;

		String resType = request.getParameter("restype");
		int resId = Integer.valueOf(request.getParameter("resid"));
		String patientId = request.getParameter("patientid");
		String inDate = request.getParameter("indate");
		String noId = request.getParameter("noid");
		Map<String, Object> data = patientService.get4SData(resType, resId, patientId, inDate, noId);
		if (data != null) {
			data.put("ORG", orgList.get(0));
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("time", new Date());
			context.put("user", user);
			data.put("Context", context);
		}
		result.addAllObjects(data);
		return result;
	}
}