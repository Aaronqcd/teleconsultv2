package com.va.removeconsult.controller.superbackend;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.service.AsyncService;
import com.va.removeconsult.service.DbService;
import com.va.removeconsult.service.MeetingService;

import web.util.Helper;
import web.util.SqlHelper;

@Controller
@RequestMapping("/root")
public class RootController {

	@Autowired
	com.va.removeconsult.service.UserService userService;
	@Resource
	MeetingService meetingService;
	
	@Resource
	private FolderService folderService;
	
	@Resource
	private DbService db;

	@RequestMapping("/Default")
	public ModelAndView Default() {
		ModelAndView mv = new ModelAndView("/root/Default");
		mv.addObject("logoUrl",folderService.querySysConfValue(SysConfig.SYS_LOGO.getKey()));
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		return mv;
	}

	@RequestMapping("/sysConfManage")
	public ModelAndView MeetingManage() {
		ModelAndView mv = new ModelAndView("/root/SysConfManage");
		return mv;
	}


	@RequestMapping("/getSysConfs")
	public ModelAndView getSysConfs(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<>();
		List<Map> rows = null;
		String code = "0";
		String msg = "";
		int count;

		String where = " 1 ";
		Map<String, Object> pagination = new HashMap<String, Object>();

		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		limit = limit == 0 ? 10 : limit;

		pagination.put("size", limit);
		pagination.put("page", page);

		rows = db.getSysConfs(where, pagination);
		count = db.getSysConfCount(where);

		for (Map<String, Object> row : rows) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("id", row.get("id"));
			d.put("confKey", row.get("conf_key"));
			d.put("confValue", row.get("conf_value"));
			d.put("remark", row.get("remark"));
			d.put("role", row.get("id"));
			data.add(d);
		}
		result.put("code", code);
		result.put("msg", msg);
		result.put("count", count);
		result.put("data", data);
		String json = JSON.toJSONString(result);
		mv.addObject("json", json);
		db.addLog("配置管理", userService.getLoginInfo(request));
		mv.addObject("json", json);
		return mv;
	}

	/**
	 * 閻у妾�
	 * 
	 * @return
	 */
	@RequestMapping("/Login")
	public ModelAndView Login(HttpServletRequest resquest) {
		ModelAndView mv = new ModelAndView("/root/Login");
		Cookie[] cookies = resquest.getCookies();
		Map cook = new HashMap<String, Object>();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cook.put(cookie.getName(), cookie.getValue());
			}
		}
		mv.addObject("cookies", cook);
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		return mv;
	}

	@RequestMapping("/Logout")
	public ModelAndView Logout(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("/root/Logout");
		Map login = userService.getLoginInfo(request);
		db.addLog("退出登录", login);
		userService.Logout(request, response);
		request.getSession().invalidate();
		return mv;
	}

	@RequestMapping("/PasswordReset")
	public ModelAndView PasswordReset() {
		ModelAndView mv = new ModelAndView("/root/PasswordReset");
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		return mv;
	}
	

	@RequestMapping("/PasswordForm")
	public ModelAndView PasswordForm() {
		ModelAndView mv = new ModelAndView("/root/PasswordForm");
		return mv;
	}

	
	@RequestMapping("/LogManage")
	public ModelAndView LogManage() {
		ModelAndView mv = new ModelAndView("/root/LogManage");
		return mv;
	}
	
	@RequestMapping("/getLogs")
	public ModelAndView getLogs(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<>();
		List<Map> rows = null;
		String code = "0";
		String msg = "";
		int count;

		String where = " 1 ";
		Map<String, Object> pagination = new HashMap<String, Object>();

		String stime = (String) request.getParameter("stime");
		String etime = (String) request.getParameter("etime");
		String user = (String) request.getParameter("user");
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		limit = limit == 0 ? 10 : limit;

		if (stime != null && !stime.equals(""))
			where += " and DATE_FORMAT(`time`,'%Y-%m-%d %H:%i:%s')>DATE_FORMAT('" + stime + "','%Y-%m-%d %H:%i:%s')";
		if (etime != null && !etime.equals(""))
			where += " and DATE_FORMAT(`time`,'%Y-%m-%d %H:%i:%s')<DATE_FORMAT('" + etime + "','%Y-%m-%d %H:%i:%s')";
		if (user != null && !user.equals("")) {
			user = SqlHelper.Addslashes(user.trim());
			where += " and upper(user.user) like upper(\"%" + user + "%\") or upper(user.name) like upper(\"%" + user
					+ "%\") or upper(log.context) like upper(\"%" + user + "%\")";
		}
		pagination.put("size", limit);
		pagination.put("page", page);

		rows = db.getLogs(where, pagination,"0");
		count = db.getLogCount(where,"0");

		for (Map<String, Object> row : rows) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("id", row.get("id"));
			d.put("account", row.get("u"));
			d.put("username", row.get("name"));
			d.put("time", Helper.DateTimeFromat((Timestamp) row.get("time"), "yyyy-MM-dd HH:mm:ss"));
			d.put("note", row.get("context"));
			data.add(d);
		}
		result.put("code", code);
		result.put("msg", msg);
		result.put("count", count);
		result.put("data", data);
		String json = JSON.toJSONString(result);
		mv.addObject("json", json);
		return mv;
	}
	
	@RequestMapping("/SysConForm")
	public ModelAndView UserForm(int id) {
		ModelAndView mv = new ModelAndView("/root/SysConForm");
		Map<String, Object> form = db.getSysConf(id);
		mv.addObject("form", form);
		mv.addObject("id", id);
		return mv;
	}

	@RequestMapping("/SysConFormPost")
	public ModelAndView UserFormPost(HttpServletRequest request, int id) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> data = new HashMap<String, Object>();
		String[] fields = { "confKey", "confValue", "remark"};
		for (String f : fields) {
			Object o = request.getParameter(f);
			if("confKey".equals(f)){
				f= "conf_key";
			}else if("confValue".equals(f)){
				f= "conf_value";
			}
			data.put(f, o);
		}
		int result = db.EditSysConf(data, id);
		db.addLog("配置管理>修改配置", userService.getLoginInfo(request));
		String json = JSON.toJSONString(data);
		mv.addObject("json", json);
		return mv;
	}
	

}
