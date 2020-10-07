package com.va.removeconsult.wxapi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.va.removeconsult.dao.ApiConfigDao;
import com.va.removeconsult.service.DbService;
import com.va.removeconsult.service.TblRoomService;
import com.va.removeconsult.service.UserService;
import com.va.removeconsult.wxapi.utils.AccessTokenUtils;
import com.va.removeconsult.wxapi.utils.ResultBean;

import web.util.SqlHelper;


@Controller
@RequestMapping(value="wxapi") 
public class WxApiController  extends BaseController {
	
	@Resource  
    private UserService userService;
	
	@Resource  
    private TblRoomService  tblRoomService;
	
	@Resource  
    private ApiConfigDao apiConfigDao;
	
	@Resource
	private DbService db;
	
	
	@ResponseBody
	@RequestMapping(value={"/hosp/list"})
    public ResultBean hospList(HttpServletRequest request,HttpSession session){
		ResultBean responseEntity=new ResultBean();
		Map<String,Object> data=new LinkedHashMap<String, Object>();
		List<Map<String,Object>> list= apiConfigDao.findApiConfigPage();
		data.put("list", list);
		responseEntity=successResult(STATUS_SUCCESS, "查询成功", data);
        return responseEntity;
    }
	
			
	@ResponseBody
	@RequestMapping(value={"/user/login"})
    public ResultBean login(HttpServletRequest request,HttpSession session,
    		String userAccount,String password){
		ResultBean responseEntity=new ResultBean();
		Map<String,Object> data=new LinkedHashMap<String, Object>();
		
		if(StringUtils.isEmpty(userAccount)){
			responseEntity=errorResult("您输入的帐号不能为空");
			return responseEntity;
		}
		if(StringUtils.isEmpty(password)){
			responseEntity=errorResult("您输入的密码不能为空");
			return responseEntity;
		}
		Map<String, Object> user=userService.wxApiLogin(userAccount, password);
		if(user==null){
			responseEntity=errorResult("您输入帐号无权限登录");
			return responseEntity;
		}
		Map<String,Object> mapUser=new LinkedHashMap<String, Object>();
		String sexstr="未知";
		if("0".equals(user.getOrDefault("sex","2").toString())) {
			sexstr="男";
		}else if("1".equals(user.getOrDefault("sex","2").toString())) {
			sexstr="女";
		}
		 
		mapUser.put("id", user.get("id"));
		mapUser.put("name", user.get("name"));
		mapUser.put("avatar", user.get("avatar"));
		mapUser.put("sex", sexstr);
		mapUser.put("job", user.get("job"));
		mapUser.put("special", user.get("special"));
		mapUser.put("phone", user.get("phone"));
		mapUser.put("email", user.get("email"));
		mapUser.put("selfOrgan", user.get("selfOrgan"));
		mapUser.put("parentOrgan", user.get("parentOrgan"));
		
		String accessToken=AccessTokenUtils.getToken(String.valueOf(user.get("id")));
		data.put("accessToken", accessToken);
		data.put("user", mapUser);
		responseEntity=successResult(STATUS_SUCCESS, "登录成功", data);
        return responseEntity;
    }
	
	
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value={"/meeting/list"})
    public ResultBean meetingList(HttpSession session,String accessToken,Integer page,Integer limit,
    		String type,String seachValue,String username){
		ResultBean responseEntity=new ResultBean();
		if(!AccessTokenUtils.checkToken(accessToken)){
			responseEntity=errorResult("accessToken不合法");
			return responseEntity;
		}
		if(StringUtils.isEmpty(type)){
			responseEntity=errorResult("会议类型不能为空");
			return responseEntity;
		}
		Map<String,Object> data=new LinkedHashMap<String, Object>();
		Map<String,Object> dataMap=new LinkedHashMap<String, Object>();
		int waitCount =0;
		int inCount =0;

		if("1".equals(type)) {
			data=findList(accessToken, page, limit, type);
			if(data!=null) {
				waitCount=(int) data.get("count");
			}
			
			dataMap=findList(accessToken, 1, 1, "2");
			if(dataMap!=null) {
				inCount=(int) dataMap.get("count");
			}
		}else if("2".equals(type)) {
			data=findList(accessToken, page, limit, type);
			if(data!=null) {
				inCount=(int) data.get("count");
			}
			dataMap=findList(accessToken, 1, 1, "1");
			if(dataMap!=null) {
				waitCount=(int) dataMap.get("count");
			}
		}
		
		data.put("waitCount", waitCount);
		data.put("inCount", inCount);
		responseEntity=successResult(STATUS_SUCCESS, "查询成功", data);
        return responseEntity;
    }
	
	
	@SuppressWarnings("rawtypes")
	private Map<String,Object> findList(String accessToken,Integer page,Integer limit, String type) {
		Map<String,Object> data=new LinkedHashMap<String, Object>();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		int count=0;
		data.put("count", count);
		data.put("list", list);
		try {
			String userId=AccessTokenUtils.getUserId(accessToken);
			String statusStr="";
			String status="1";
			if("1".equals(type)) {
				status="2";
				type="3";
				statusStr="待会诊";
			}else if("2".equals(type)) {
				status="3";
				type="0";
				statusStr="正在会诊";
			}
			if(page==null) {
				page=1;
			}
			if(limit==null) {
				limit=10;
			}
			String where=" (meeting.user="+userId+" or find_in_set("+userId+",meeting.attends)) ";
			Map<String,Object>pagination=new HashMap<String,Object>();
			if(status!=null && !status.equals("")){
				if(status.equals("2")){
					status=SqlHelper.Addslashes(status);
					if(type.equals("2"))where=" meeting.user="+userId+" and meeting.`status`=\""+status+"\" ";
					else if(type.equals("3"))where=" find_in_set("+userId+",meeting.attends) and meeting.`status`=\""+status+"\"";
					else where+=" and 0";
				}
				else{
					status=SqlHelper.Addslashes(status);
					where+=" and meeting.`status`=\""+status+"\"";
				}
			}
			pagination.put("size", limit);
			pagination.put("page", page);
			List<Map> rows=db.getMeetings(where, pagination);
			count=db.getMeetingsCount(where);
			for(Map row : rows){
				boolean show = true;
				int length = row.get("attends").toString().length();
			    if(!row.get("user").toString().trim().equals(userId) && length ==0  ) {
					show = false;
				}
				if(show) {
					String roomId="";
					String meetingID=row.get("id")!=null ? row.get("id").toString() : "";
					Map<String, Object> roomMap=tblRoomService.getTblRoomByMeetingID(meetingID);
					if(roomMap!=null) {
						roomId=roomMap.get("groupID")!=null ? roomMap.get("groupID").toString() : "";
					}
					Map<String,Object>d=new HashMap<String,Object>();
					d.put("id",meetingID);
					d.put("roomId",roomId);
					d.put("encode", row.get("no"));
					d.put("account", row.get("user_user"));
					d.put("userName", row.get("user_name"));
					d.put("title", row.get("topic"));
					d.put("time", String.valueOf(row.get("stime")).substring(0,16));
					Map attr = db.getAttr((String)row.get("attaId"));
					d.put("sysName", attr!=null?attr.get("sysName"):"");
					d.put("note", row.get("note"));
					d.put("status", statusStr);
					d.put("meetingType", row.get("meetingtype"));
					String attends="";
					if(length >0) {
						attends = db.getUserUsers((String)row.get("attends"),";");
					}
					if(attends.trim().endsWith("；")) {
						attends = attends.trim().substring(0,attends.trim().length()-1);
					}
					d.put("members", attends);
					if(row.get("user").toString().equals(userId))d.put("role", "applybutton");
					else d.put("role", "attendbutton");
					list.add(d);
				}
			}
			data.put("count", count);
			data.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
		
	}
	
	
	
	

	

}
