package com.va.removeconsult.controller.backend;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.va.removeconsult.bean.UserMemoryData;
import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.service.AsyncService;
import com.va.removeconsult.service.DbService;
import com.va.removeconsult.service.MeetingService;
import com.va.removeconsult.util.MD5Utils;
import com.va.removeconsult.util.SendEmail;
import com.va.removeconsult.util.Utils;

import web.util.Helper;
import web.util.SqlHelper;

@Controller
@RequestMapping("/admin")
public class adminController {

	@Autowired
	com.va.removeconsult.service.UserService userService;
	@Resource
	MeetingService meetingService;
	@Resource
	private DbService db;
	@Resource
	private FolderService folderService;
	@Autowired
	private AsyncService asyncService;

	@RequestMapping("/Default")
	public ModelAndView Default() {
		ModelAndView mv = new ModelAndView("/admin/Default");
		mv.addObject("logoUrl",folderService.querySysConfValue(SysConfig.SYS_LOGO.getKey()));
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		return mv;
	}

	@RequestMapping("/MeetingManage")
	public ModelAndView MeetingManage() {
		ModelAndView mv = new ModelAndView("/admin/MeetingManage");
		String node = meetingService.getNodeJSON(1);
		mv.addObject("node", node);
		Map<String, Object> meetingstatus = db.getMeetingStatus("admin");
		mv.addObject("meetingstatus", JSON.toJSONString(meetingstatus));
		List<Map> meetingaction = db.getMeetingStatus();
		mv.addObject("meetingaction", JSON.toJSONString(meetingaction));
		return mv;
	}

	@RequestMapping("/dropMeeting")
	public void dropMeeting(String ids, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> pagination = new HashMap<String, Object>();
		pagination.put("page", 1);
		pagination.put("size", Integer.MAX_VALUE);
		List<Map> meetings = db.getMeetings("meeting.id in(" + ids + ")", pagination);
		// int code=db.dropMeetings(ids);
		int code = db.dropMettingByIds(ids, 3);
		for (Map map : meetings) {
			String attends = map.get("attends").toString();
			String[] split = attends.split(",");
			for (int i = 0; i < split.length; i++) {
				String id = split[i];
				Map byUser = db.getByUser(id);
				if (null != byUser) {
					String email = byUser.get("email").toString();
					
					// 通知与会者
					try {
						if (Integer.parseInt(map.get("is_del_metting").toString()) != 1) {
							String emailAddress = this.folderService.querySysConfValue(SysConfig.EMAIL_ADDRESS.getKey());
							String emailPwd = this.folderService.querySysConfValue(SysConfig.EMAIL_AUTHORIZATION_CODE.getKey());
							SendEmail.send(email, map.get("topic").toString(), "删除会诊",emailAddress,emailPwd);
							
							try {
								if(byUser.containsKey("phone") && byUser.get("phone")!=null) {
									String toPhones = byUser.get("phone").toString();
									String toPhone = toPhones.substring(12);
									String[] params=new String[]{map.get("no").toString()};
									asyncService.sendSms(toPhone, "SMS_TEMP_1", params);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("code", code);
		String log = "";
		if (code > 0) {
			log = meetingService.getMeetingLog(meetings);
		} else {
			log = "失败";
		}
		db.addLog("会诊管理>删除会诊:" + log, userService.getLoginInfo(request));
		try {
			response.getOutputStream().print(json.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/getMeetings")
	public ModelAndView getMeetings(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<>();
		List<Map> rows = null;
		String code = "0";
		String msg = "";
		int count;

		String where = " 1 ";
		Map<String, Object> pagination = new HashMap<String, Object>();

		String seachValue = (String) request.getParameter("seachValue");
		String username = (String) request.getParameter("username");
		String status = (String) request.getParameter("status");
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		limit = limit == 0 ? 10 : limit;

		if (username != null && !username.equals("")) {
			username = SqlHelper.Addslashes(username);
			where += " and upper(user.`user`) like upper(trim(\"%" + username + "%\"))";
		}

		if (status != null && !status.equals("")) {
			status = SqlHelper.Addslashes(status);
			where += " and meeting.`status`=\"" + status + "\"";
		}

		// 页面搜索条件
		if (seachValue != null && !seachValue.trim().equals("")) {
			seachValue = SqlHelper.Addslashes(seachValue);
			StringBuffer str = new StringBuffer();
			str.append(
					"ifnull(meeting.no,''),ifnull(user.user,''),ifnull(user.name,''),ifnull(meeting.topic,''),ifnull(meeting.stime,''),ifnull(meeting.etime,''),");
			str.append("ifnull(meeting.conclusion,'无'),");
			str.append(
					"(select a.value from dictitem a where a.type='meetingtype' and a.pid=0 and a.`key`=meeting.type limit 1)");
			where += " and upper(concat(" + str + ")) like upper(\"%" + seachValue.trim() + "%\")";
		}
		pagination.put("size", limit);
		pagination.put("page", page);
		// update by and
		rows = db.getMeetingsWithAdmin(where, pagination);
		count = db.getMeetingsCountWithAdmin(where);

		for (Map<String, String> row : rows) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("id", row.get("id"));
			d.put("encode", row.get("no"));
			d.put("account", row.get("user_user"));
			d.put("username", row.get("user_name"));
			d.put("title", row.get("topic"));
			d.put("time", String.valueOf(row.get("stime")) + " " + String.valueOf(row.get("etime")));
			d.put("record", "");
			// d.put("note", "閺冿拷");
			d.put("note", row.get("note"));
			d.put("status", String.valueOf(row.get("status")));
			d.put("type", row.get("meetingtype"));
			d.put("members", row.get("attends"));
			d.put("role", "adminbutton");
			data.add(d);
		}
		result.put("code", code);
		result.put("msg", msg);
		result.put("count", count);
		result.put("data", data);
		String json = JSON.toJSONString(result);
		db.addLog("会诊管理>列表搜索", userService.getLoginInfo(request));
		mv.addObject("json", json);
		return mv;
	}

	@RequestMapping("/getUsers")
	public ModelAndView getUsers(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<>();
		List<Map> rows = null;
		String code = "0";
		String msg = "";
		int count;

		String where = " 1 ";
		Map<String, Object> pagination = new HashMap<String, Object>();

		String seachValue = (String) request.getParameter("seachValue");
		String group = (String) request.getParameter("group");
		String user = (String) request.getParameter("user");
		String belong = (String) request.getParameter("belong");
		String type = (String) request.getParameter("type");
		
		int page = Integer.parseInt(request.getParameter("page"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		limit = limit == 0 ? 10 : limit;
		if (group != null && !group.equals("")) {
			group = SqlHelper.Addslashes(group);
			where += " and `group`=\"" + group + "\"";
		}
		if (type != null && !type.equals("") && !type.equals("0")) {
			if (belong != null && !belong.equals("") && !belong.equals("0")) {
				String[] belongs = belong.split(",");
				where += " and belong=" + belongs[0];
			}
		} else {
			if (belong != null && !belong.equals("") && !belong.equals("0")) {
				where += " and find_in_set(belong,'" + belong + "')";
			}
		}
		if (user != null && !user.equals("")) {
			user = SqlHelper.Addslashes(user);
			where += " and (user.user like \"%" + user + "%\" or user.name like \"%" + user + "%\")";
		}
		// 页面搜索条件
		if (seachValue != null && !seachValue.trim().equals("")) {
			seachValue = SqlHelper.Addslashes(seachValue);
			StringBuffer str = new StringBuffer();
			str.append("user.user,user.name,");
			str.append("(case when user.sex=0 then '男' when user.sex=1 then '女' else user.sex end),");
			str.append("ifnull(user.job,''),ifnull(user.special,''),ifnull(o2.NAME,''),ifnull(o1.NAME,''),");
			str.append("group.groupname,ifnull(user.phone,''),ifnull(user.email,'')");
			// str.append("(select b.attendname from meeting_status b where
			// b.id=meeting.status limit 1),");
			// str.append("(select replace(group_concat(c.name),',',';') from user c where
			// find_in_set(c.id,meeting.attends))");
			where += " and upper(concat(" + str + ")) like upper(\"%" + seachValue.trim() + "%\")";
		}

		pagination.put("size", limit);
		pagination.put("page", page);
		rows = db.getUsers(where, pagination);
		count = db.getUserCount(where);

		for (Map<String, Object> row : rows) {
			Map<String, Object> d = new HashMap<String, Object>();
			d.put("id", row.get("id"));
			d.put("account", row.get("user"));
			d.put("username", row.get("name"));
			d.put("avatar", row.get("avatar"));
			d.put("email", row.get("email"));
			/*
			 * if((int)row.get("sex")==0)d.put("sex", "閻拷"); else d.put("sex", "婵傦拷");
			 */
			d.put("sex", row.get("sex"));
			d.put("title", row.get("job"));
			d.put("specialty", row.get("special"));
			d.put("photo", 116);
			d.put("tel", row.get("phone"));
			d.put("parentOrg", row.get("parentOrgan"));
			d.put("organization", row.get("selfOrgan"));
			d.put("role", row.get("groupname"));
			data.add(d);
		}
		result.put("code", code);
		result.put("msg", msg);
		result.put("count", count);
		result.put("data", data);
		String json = JSON.toJSONString(result);
		mv.addObject("json", json);
		db.addLog("用户管理>列表搜索", userService.getLoginInfo(request));
		return mv;
	}

	@RequestMapping("/dropUser")
	public void dropUser(int id, HttpServletRequest request, HttpServletResponse response) {
		int code = db.dropUser(id);
		JSONObject json = new JSONObject();
		json.put("code", code);
		try {
			response.getOutputStream().print(json.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.addLog("用户管理>删除用户", userService.getLoginInfo(request));
	}

	@RequestMapping("/UserImport")
	public ModelAndView UserImport() {
		ModelAndView mv = new ModelAndView("/admin/UserImport");
		return mv;
	}
	
	@RequestMapping("/UserManage")
	public ModelAndView UserManage() {
		ModelAndView mv = new ModelAndView("/admin/UserManage");
		List<Map> organ = db.getTreeOrganList(0);
		mv.addObject("organ", JSON.toJSONString(organ));

		Map<String, Object> form = new HashMap<String, Object>();
		Map<String, Object> group = new HashMap<String, Object>();

		List<Map> groupItems = db.getGroups();
		group.put("items", groupItems);
		form.put("group", group);
		mv.addObject("form", form);
		return mv;
	}

	@RequestMapping("/LogManage")
	public ModelAndView LogManage() {
		ModelAndView mv = new ModelAndView("/admin/LogManage");
		return mv;
	}

	@RequestMapping("/dropLog")
	public void dropLogs(String ids, HttpServletResponse response) {
		int code = db.dropLogs(ids);
		JSONObject data = new JSONObject();
		data.put("code", code);
		try {
			response.getOutputStream().print(data.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		rows = db.getLogs(where, pagination,"1");
		count = db.getLogCount(where,"1");

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

	@RequestMapping("/PasswordForm")
	public ModelAndView PasswordForm() {
		ModelAndView mv = new ModelAndView("/admin/PasswordForm");
		return mv;
	}

	@RequestMapping("/addUser")
	public ModelAndView addUser(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/admin/Login");
		return mv;
	}

	/**
	 * 閻у妾�
	 * 
	 * @return
	 */
	@RequestMapping("/Login")
	public ModelAndView Login(HttpServletRequest resquest) {
		ModelAndView mv = new ModelAndView("/admin/Login");
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
		ModelAndView mv = new ModelAndView("/admin/Logout");
		Map login = userService.getLoginInfo(request);
		db.addLog("退出登录", login);
		userService.Logout(request, response);
		request.getSession().invalidate();
		return mv;
	}

	@RequestMapping("/PasswordReset")
	public ModelAndView PasswordReset() {
		ModelAndView mv = new ModelAndView("/admin/PasswordReset");
		mv.addObject("titleName",folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
		return mv;
	}
	@RequestMapping("/getUserSize")
	public void getUserSize( int id, HttpServletResponse response, HttpServletRequest request) throws IOException {
		JSONObject json = new JSONObject();
		// 判断用户是否已超过最大限制数
		if(id == 0){
			int count =  db.getUserIsNotAdminsCount("admins");
			String userSizeStr = this.folderService.querySysConfValue(SysConfig.USER_SIZE.getKey());
			int userSize  = Integer.parseInt(userSizeStr);
			if( userSize - count > 0 ){
				json.put("status", 1);
			}else{
				json.put("status", 0);
			}
		}else{
			json.put("status", 2);
		}
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
		return ;
	}
	
	
	
	

	@RequestMapping("/UserForm")
	public ModelAndView UserForm(String action, int id) {
		ModelAndView mv = new ModelAndView("/admin/UserForm");
		Map<String, Object> user = db.getUser(id);
		Map<String, Object> form = new HashMap<String, Object>();
		if (user == null) {
			user = new HashMap<String, Object>();
			user.put("sex", 0);
			user.put("group", "users");
			user.put("belong", 0);
		}
		for (Entry<String, Object> fv : user.entrySet()) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("value", fv.getValue());
			form.put(fv.getKey(), item);
		}

		Map<String, Object> sex = (Map<String, Object>) form.get("sex");
		List<Map> sexItems = db.getDictItems("sex", 0);
		for (Map<String, Object> item : sexItems) {
			String key = String.valueOf(item.get("key"));
			String key1 = String.valueOf(sex.get("value"));
			if (key.equals(key1)) {
				item.put("checked", "checked");
			}
		}
		sex.put("items", sexItems);

		Map<String, Object> group = (Map<String, Object>) form.get("group");
		List<Map> groupItems = db.getGroups(" and gid <> 1");
		for (Map<String, Object> item : groupItems) {
			String key = String.valueOf(item.get("key"));
			String key1 = String.valueOf(group.get("value"));
			if (key.equals(key1)) {
				item.put("selected", "selected");
			}
		}
		group.put("items", groupItems);

		Map<String, Object> belong = (Map<String, Object>) form.get("belong");
		List<Map<String, Object>> organItems = db.getOrganTree(0);
		for (Map<String, Object> item : organItems) {
			String key = String.valueOf(item.get("id"));
			String key1 = String.valueOf(belong.get("value"));
			if (key.equals(key1)) {
				item.put("selected", "selected");
			}
		}
		belong.put("items", organItems);
		mv.addObject("form", form);
		mv.addObject("action", action);
		mv.addObject("id", id);
		return mv;
	}

	@RequestMapping("/isExists")
	public ModelAndView isExists(HttpServletRequest request, String tableName, String param, String debar) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> data = new HashMap<String, Object>();
		// 判断用户是否已存在

		Map<String, Object> paramMap = (Map) JSON.parse(param);
		Map<String, Object> debarMap = (Map) JSON.parse(debar);
		if (db.isExists(tableName, paramMap, debarMap)) {
			data.put("status", 1);
		} else {
			data.put("status", 0);
		}
		String json = JSON.toJSONString(data);
		return mv.addObject("json", json);
	}

	@RequestMapping("/UserFormPost")
	public ModelAndView UserFormPost(HttpServletRequest request, String action, int id) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> data = new HashMap<String, Object>();
		String[] fields = { "user", "belong", "group", "avatar", "name", "sex", "job", "special", "phone", "email" };
		for (String f : fields) {
			data.put(f, request.getParameter(f));
		}
		String user = request.getParameter("user");
		String email = request.getParameter("email");

		boolean valid = true;
		// 判断用户名已存在
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("user", user);
		Map<String, Object> debarMap = new HashMap<String, Object>();
		debarMap.put("id", action.equals("add") ? "" : id);
		if (db.isExists("user", paramMap, debarMap)) {
			data.put("code", -1);
			valid = false;
		}

		// 判断邮箱名已存在
		paramMap = new HashMap<String, Object>();
		paramMap.put("email", email);
		debarMap = new HashMap<String, Object>();
		debarMap.put("id", action.equals("add") ? "" : id);
		if (valid && db.isExists("user", paramMap, debarMap)) {
			data.put("code", -2);
			valid = false;
		}

		if (valid) {
			String group = request.getParameter("group");
			int belong = Integer.parseInt(request.getParameter("belong"));
			Map OrganAdmin = db.getOrganAdminUser(belong);
			int have = db.getOrganAdminUserCount(belong);
			int result = 0;
			if (action.equals("add")) {
				if (group.equals("managers") && have > 0) {
					data.put("code", 0);
				} else {
					int password = (int) (100000 + Math.random() * (10000000 - 100000));
					String phone = request.getParameter("phone");
					data.put("phone", phone);
					data.put("password", MD5Utils.string2MD5(String.valueOf(password)));
					data.put("logintype", 1);
					result = db.AddUser(data);
					Utils.log(String.format("新用户已写入数据库, %s", data.get("user")));
					
					Map<String, String> mailTemplate = db.getMailTemplate("mail_user_warn");
					String hospitalName = this.folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey());
					String sysUrl = this.folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_URL.getKey());
					Map<String, Object> mailParam = new HashMap<String, Object>();
					mailParam.put("hospitalName", hospitalName);
					mailParam.put("user", user);
					mailParam.put("password", "" + password);
					mailParam.put("sysUrl",sysUrl);
					String title = mailTemplate.get("template_title");
					String text = SendEmail.formatTemplate(mailTemplate.get("template_context"), mailParam);
					
					try {
						Utils.log(String.format("准备发送注册邮件, %s, %s", email, title));
						String emailAddress = this.folderService.querySysConfValue(SysConfig.EMAIL_ADDRESS.getKey());
						String emailPwd = this.folderService.querySysConfValue(SysConfig.EMAIL_AUTHORIZATION_CODE.getKey());
						SendEmail.send(email, title, text,emailAddress,emailPwd);
					} catch (GeneralSecurityException e) {
						Utils.error("发送注册邮件失败", e);
					}
					
					try {
				    	if(data!=null && data.containsKey("phone") && data.get("phone")!=null) {
							String toPhones = data.get("phone").toString();
							String toPhone = toPhones.substring(12);
							System.out.println("toPhone:"+toPhone);
							String[] params=new String[]{user,String.valueOf(password)};
							asyncService.sendSms(toPhone, "SMS_TEMP_4", params);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				db.addLog("用户管理>新增用户", userService.getLoginInfo(request));
			} else {
				boolean ismanagers = false;
				if (have > 0) {
					if ((int) OrganAdmin.get("id") == id)
						ismanagers = true;
				}
				if (group.equals("managers") && have > 0 && !ismanagers) {
					data.put("code", 0);
					// data.put("msg", "鐠囥儲婧�閺嬪嫬鍑＄�涙ê婀張鐑樼�粻锛勬倞閸涳拷!");
				} else {
					String phone = request.getParameter("phone");
					data.put("phone", "AC5615684452"+phone);
					result = db.EditUser(data, id);
				}
				db.addLog("用户管理>修改用户", userService.getLoginInfo(request));
			}
			data.put("code", result);
		}

		String json = JSON.toJSONString(data);
		mv.addObject("json", json);
		return mv;
	}

	@RequestMapping("/OrganizationForm")
	public ModelAndView OrganizationForm(String currentName, int currentId) {
		ModelAndView mv = new ModelAndView("/admin/OrganizationForm");
		mv.addObject("currentName", currentName);
		mv.addObject("currentId", currentId);
		return mv;
	}

	@RequestMapping("/OrganizationFormPost")
	public ModelAndView OrganizationFormPost(HttpServletRequest request, String name, int id) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", name);
		int result;
		result = db.EditOrgan(data, id);
		data.put("code", result);
		String json = JSON.toJSONString(data);
		mv.addObject("json", json);
		db.addLog("用户管理>修改机构", userService.getLoginInfo(request));
		return mv;
	}

	@RequestMapping("/OrganizationNew")
	public ModelAndView OrganizationNew(String currentName, int currentId) {
		ModelAndView mv = new ModelAndView("/admin/OrganizationNew");
		mv.addObject("currentName", currentName);
		mv.addObject("currentId", currentId);
		return mv;
	}

	@RequestMapping("/OrganizationNewPost")
	public ModelAndView OrganizationNewPost(HttpServletRequest request, String name, int pid) {
		ModelAndView mv = new ModelAndView("../json");
		Map<String, Object> data = new HashMap<String, Object>();
		Map porgan = db.getOrgan(pid);
		int type = (int) porgan.get("type");
		data.put("name", name);
		data.put("pid", pid);
		data.put("type", type + 1);
		int result;
		result = db.AddOrgan(data);
		data.put("code", result);
		String json = JSON.toJSONString(data);
		mv.addObject("json", json);
		db.addLog("用户管理>增加下级机构", userService.getLoginInfo(request));
		return mv;
	}

	@RequestMapping("/MeetingAction")
	public void MeetingAction(int id, String action, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> user = userService.getLoginInfo(request);
		Map meeting = db.getMeeting(String.valueOf(id));
		int userId = (int) user.get("id");
		JSONObject result = new JSONObject();
		int code = 0;

		code = meetingService.MeetingAdminAction(meeting, action, request);

		result.put("code", code);
		try {
			response.getOutputStream().print(result.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping("/dropOrgan")
	public void dropOrgan(int id, HttpServletResponse response, HttpServletRequest request) {
		int haveUser = db.getOrganUserCount(id);
		int haveSons = db.getOrganSonsCount(id);
		int code = 0;
		if (haveUser == 0 && haveSons == 0) {
			Map organ = db.getOrgan(id);
			code = db.dropOrgan(id);
			db.addLog("用户管理>删除机构 [" + organ.get("name") + "]", userService.getLoginInfo(request));
		}
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("haveUser", haveUser);
		json.put("haveSons", haveSons);
		json.put("code", code);
		try {
			response.getOutputStream().print(JSON.toJSONString(json));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ArrayList<Object> arrayList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			arrayList.add(1);
		}
		System.out.println(arrayList);
		int indexOf = arrayList.indexOf(1);
		boolean contains = arrayList.contains(1);

		String attends = "1,23,34";
		String[] attendsArray = attends.split("\\,");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < attendsArray.length; i++) {
			if (!attendsArray[i].equals("23")) {
				list.add(attendsArray[i]);
			}
		}
		String str = list.toString();
		list.toString().substring(1, str.length() - 1);
	}

	// update by and
	@RequestMapping("/batchDelUser")
	public void batchDelUser(String ids, HttpServletResponse response, HttpServletRequest request) throws IOException {
		JSONObject json = new JSONObject();

		Map<String, Object> pagination = new HashMap<String, Object>();
		pagination.put("page", 1);
		pagination.put("size", Integer.MAX_VALUE);
		List<Map> users = db.getUsers("user.id in(" + ids + ")", pagination);
		int result = 0;
		int delMeeting = 0;
		boolean success = true;
		for (Map user : users) {
			String id = user.get("id").toString();
			String name = user.get("name").toString();
			List<Map> meetingList = db.getMeetings(" meeting.user=" + user.get("id"), pagination);
			ArrayList<Object> statusArray = new ArrayList<>();
			for (Map meeting : meetingList) {
				int status = (int) meeting.get("status");
				statusArray.add(status);
			}
			if (!statusArray.contains(3)) {
				result = db.dropUser((int) user.get("id"));
				success = true;
				for (Map meeting : meetingList) {
					if (null != meeting) {
						int status = (int) meeting.get("status");
						String attends = meeting.get("attends").toString();
						String[] attendsArray = attends.split("\\,");
						UserMemoryData.getSessionIDMap().remove(name);
						UserMemoryData.getSessionIDMap().put(name, "用户" + name + "已被删除");
						if (meeting.get("user").toString().equalsIgnoreCase(user.get("id").toString())) {
							// 删除该用户发起的会诊（除正在开会的会诊）
							delMeeting = db.dropMeetingsById((int) meeting.get("id"));
							// 通知与会人
							for (int i = 0; i < attendsArray.length; i++) {
								int userId = Integer.valueOf(attendsArray[i]);
								Map byUser = db.getUser(userId);
								if (null != byUser) {
									String email = byUser.get("email").toString();
									// 通知与会者
									try {
										String delStr = meeting.get("is_del_metting").toString();
										if (Integer.valueOf(delStr).intValue() != 1) {
											// SendEmail.send(email, meeting.get("topic").toString(),
											// meeting.get("user_name").toString()+"已删除编号为"+meeting.get("no").toString()+"会诊");
											asyncService.sendMail(email, meeting.get("topic").toString(),
													meeting.get("user_name").toString() + "已删除编号为"
															+ meeting.get("no").toString() + "会诊");
										}
									} catch (Exception e) {
										e.printStackTrace();
										continue;
									}
								}
							}
						} else {
							// 更新会诊的参与者
							List<String> list = new ArrayList<String>();
							for (int i = 0; i < attendsArray.length; i++) {
								if (!attendsArray[i].equals(id)) {
									list.add(attendsArray[i]);
								}
							}
							String str = list.toString();
							String updateIds = list.toString().substring(1, str.length() - 1);
							db.updateMeetingAttends((int) meeting.get("id"), updateIds);
						}

						db.addLog("用户管理>删除用户:" + "用户" + user.get("name") + "已删除", userService.getLoginInfo(request));
					}
				}
			} else {
				success = false;
			}

		}

		/*
		 * int result = db.batchDelUser(ids); List<Map> selectMetting =
		 * db.selectMetting(ids, 4); //删除该用户发起的会诊（除正在开会的会诊）
		 * db.dropMeetingsByUser(ids,4);
		 */
		ArrayList<Object> topic = new ArrayList<>();
		json.put("success", success);
		if (result > 0) {
			json.put("code", "1");
		} else {
			json.put("code", "-10");
		}
		String log = getUserLog(users);
		db.addLog("用户管理>删除用户:" + log, userService.getLoginInfo(request));
		response.getWriter().write(json.toString());
		return;
	}

	private String getUserLog(List<Map> users) {
		for (Map map : users) {
			Object sex = map.get("sex");
			map.put("sex", sex == null ? "" : sex.toString().equals("1") ? "男" : "女");
		}
		return Utils.getLogJson(users, getUserTitle());
	}

	public Map<String, String> getUserTitle() {
		Map<String, String> titleMap = new HashMap<String, String>();
		titleMap.put("user", "账号");
		titleMap.put("selfOrgan", "所属机构");
		titleMap.put("groupname", "账号类型");
		titleMap.put("name", "姓名");
		titleMap.put("sex", "性别");
		titleMap.put("job", "职称");
		titleMap.put("special", "专长");
		titleMap.put("phone", "电话");
		titleMap.put("email", "邮箱");
		return titleMap;
	}
	
	
	@RequestMapping("/MeetingCound")
	public ModelAndView MeetingCound() {
		ModelAndView mv = new ModelAndView("/admin/MeetingCound");
		return mv;
	}

	@RequestMapping("/getMeetingCounds")
	public ModelAndView getMeetingCounds(HttpServletRequest request) {
			ModelAndView mv = new ModelAndView("../json");
			Map<String, Object> result = new HashMap<String, Object>();
			List<Map<String, Object>> data = new ArrayList<>();
			List<Map> rows = null;
			int count;
			int timeCount=0;
			String where = " 1 ";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar todayStart = Calendar.getInstance();
			todayStart.setTime(new Date());
			 todayStart.set(Calendar.HOUR_OF_DAY, 0);
			 todayStart.set(Calendar.MINUTE, 0);
			 todayStart.set(Calendar.SECOND, 0);
			Map<String, Object> pagination = new HashMap<String, Object>();
			String type = (String) request.getParameter("type");
			int page = Integer.parseInt(request.getParameter("page"));
			int limit = Integer.parseInt(request.getParameter("limit"));
			limit = limit == 0 ? 10 : limit;
			if (type != null && !type.equals("")) {
				String start = null;
				String end = null;
				if("1".equals(type)){
					start = sdf.format(todayStart.getTime());
					todayStart.add(Calendar.DATE,1);
					end = sdf.format(todayStart.getTime());
				}else if("2".equals(type)){
					todayStart.set(Calendar.DAY_OF_MONTH,1);
					start = sdf.format(todayStart.getTime());
					todayStart.add(Calendar.MONTH, 1);
					end = sdf.format(todayStart.getTime());
				}else if("3".equals(type)){
					todayStart.set(Calendar.DAY_OF_MONTH,1);
					end = sdf.format(todayStart.getTime());
					todayStart.add(Calendar.MONTH, -1);
					start = sdf.format(todayStart.getTime());
				}else if("4".equals(type)){
						int month = todayStart.get(Calendar.MONTH)+1;
						if (month >= 1 && month <= 3) {
							todayStart.set(todayStart.get(Calendar.YEAR), 0, 1, 0, 0);
							start = sdf.format(todayStart.getTime());
							todayStart.set(todayStart.get(Calendar.YEAR), 3, 1, 0, 0);
							end = sdf.format(todayStart.getTime());
						} else if (month >= 4 && month <= 6) {
							todayStart.set(todayStart.get(Calendar.YEAR), 3, 1, 0, 0);
							start = sdf.format(todayStart.getTime());
							todayStart.set(todayStart.get(Calendar.YEAR), 6, 1, 0, 0);
							end = sdf.format(todayStart.getTime());
						} else if (month >= 7 && month <= 9) {
							todayStart.set(todayStart.get(Calendar.YEAR), 6, 1, 0, 0);
							start = sdf.format(todayStart.getTime());
							todayStart.set(todayStart.get(Calendar.YEAR), 9, 1, 0, 0);
							end = sdf.format(todayStart.getTime());
						} else {
							todayStart.set(todayStart.get(Calendar.YEAR), 9, 1, 0, 0);
							start = sdf.format(todayStart.getTime());
							todayStart.set(todayStart.get(Calendar.YEAR)+1, 0, 1, 0, 0);
							end = sdf.format(todayStart.getTime());
						}
					
				}else if("5".equals(type)){
					start = (String) request.getParameter("stime");
					end = (String) request.getParameter("etime");
				}
				where += " and DATE_FORMAT(`stear_time`,'%Y-%m-%d %H:%i:%s') >= DATE_FORMAT('" +start+ "','%Y-%m-%d %H:%i:%s')";
				where += " and DATE_FORMAT(`stear_time`,'%Y-%m-%d %H:%i:%s') < DATE_FORMAT('" +end+ "','%Y-%m-%d %H:%i:%s')";
			}
			pagination.put("size", limit);
			pagination.put("page", page);
			
			rows = db.getMeetingCounts(where, pagination);
			count = db.getMeetingsRowsCount(where);
			if(count>0){
				timeCount =  db.getMeetingsTimesCount(where);
			}
			for (Map<String, Object> row : rows) {
				Map<String, Object> d = new HashMap<String, Object>();
				d.put("id", row.get("id"));
				d.put("code", row.get("code"));
				d.put("stearTime", Helper.DateTimeFromat((Timestamp) row.get("stear_time"), "yyyy-MM-dd HH:mm:ss"));
				d.put("endTime", Helper.DateTimeFromat((Timestamp) row.get("end_time"), "yyyy-MM-dd HH:mm:ss"));
				d.put("people", row.get("people"));
				d.put("duration", row.get("duration"));
				d.put("durationCount", row.get("duration_count"));
				data.add(d);
			}
			result.put("timeCount", timeCount);
			result.put("msg","success");
			result.put("code", 0);
			result.put("count", count);
			result.put("data", data);
			String json = JSON.toJSONString(result);
			mv.addObject("json", json);
			return mv;
	}
	
	
}
