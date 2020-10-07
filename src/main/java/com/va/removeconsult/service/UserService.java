package com.va.removeconsult.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.va.removeconsult.bean.User;
import com.va.removeconsult.bean.UserMemoryData;
import com.va.removeconsult.dao.UserDao;
import com.va.removeconsult.util.MD5Utils;

@Service  
public class UserService{

    @Autowired  
    private UserDao userDao;
    
    public boolean Login(String username,String password,String remember,HttpServletRequest request,HttpServletResponse response){
    	boolean ok=false;
    	Map<String,Object> user=userDao.getUserByUser(username);
    	if(user==null)return false;
    	if(user.get("password").equals(password)){
    		ok=true;
    		//request.getSession().setAttribute("user", user);
    		Date loginTime = new Date();
        	user.put("loginTime", loginTime);
    		userDao.updateLastLoginTime(Integer.parseInt(user.get("id").toString()), loginTime);
    		
    		this.setUserToSession(request, user);
    		/*if(remember.equals("1")){
    			Cookie cookie = new Cookie("user",(String) user.get("user"));
        		cookie.setMaxAge(3600*24*30*12);
        		response.addCookie(cookie);
    		}*/
//    		Cookie cookie = new Cookie();
//    		Cookie password_c = null ;
//    		if(remember.equals("1")){
//    			password_c =new Cookie("lastloginpassword", password);
//    			password_c.setMaxAge(60*60*7);
//
//    		}else {
//    			password_c =new Cookie("lastloginpassword", null);
//    			password_c.setMaxAge(0);
//    		}
//    		 password_c.setPath("/");
//	        response.addCookie(password_c);
//    		cookie.setMaxAge(3600*24*30*12);
//    		cookie.setPath("/");
//    		response.addCookie(cookie);
    		userDao.getUserByUser(username);
    		//update by and 
    		//检查用户是否已登录
    	/*	boolean logined = checkLogined(username);
    		//已经登陆了
    		if(logined){
    			//让以前登陆的人下线
    			request.getSession(true);
    			HttpSession session = (HttpSession)UserMemoryData.USR_SESSION.get(username);
    			//设置session存活期0，就是让session失效
    			try {
    				session.removeAttribute("loginuser");
    			}catch(Exception e) {
    				e.printStackTrace();
    			}
    		}
    		request.getSession().setAttribute("loginuser", username);
    		//如果该用户没有注销就再一次登陆，需要删除msg属性，否则会提示：该账户已在其他地方登陆
    		request.getSession().removeAttribute("msg");
    		UserMemoryData.USR_SESSION.put(username, request.getSession());
    		UserMemoryData.getBackSessionIDMap().put(username, request.getRequestedSessionId());*/
    	}
    	return ok;
    }
    //判断是否最后一次登录用户
    public boolean isLastLoginFlag(HttpServletRequest request) {
    	try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	Map<String, Object> loginInfo = this.getLoginInfo(request);
        	String loginTime = sdf.format(loginInfo.get("loginTime"));
        	int id = (int)loginInfo.get("id");
            Map<String, Object> user = userDao.getUserById(id);
            String lastLoginTime = sdf.format(user.get("last_login_time"));
        	//System.err.println("loginTime=="+loginTime+"    lastLoginTime="+lastLoginTime);
        	if(StringUtils.isNotEmpty(loginTime) && StringUtils.isNotEmpty(lastLoginTime) && !loginTime.equals(lastLoginTime)) {
        		return false;
        	}    		
    	}catch(Exception e) {
    		return true;
    	}
    	return true;
    }
    /**
	 * 检查该用户是否已经登陆
	 * @param users 要检查的用户
	 * @return 是否已经登陆，true已登录，false没有登陆
	 */
	public boolean checkLogined(String name){
		Object obj =UserMemoryData.USR_SESSION.get(name);
		if(obj == null){
			return false;
		}
		return true;
	}
	
    
    public boolean updateLogin(HttpServletRequest request){
    	boolean result=true;
    	Map<String,Object> user=getLoginInfo(request);
    	user=userDao.getUserByUser((String)user.get("user"));
    	//request.getSession().setAttribute("user", user);
    	this.setUserToSession(request, user);
    	return result;
    }
    
    public boolean updateLogin(HttpServletRequest request,String user){
    	boolean result=true;
    	Map<String,Object> userMap=userDao.getUserByUser(user);
    	if(userMap.get("id")==null)result=false;
    	//else request.getSession().setAttribute("user", userMap);
    	else this.setUserToSession(request, userMap);
    	return result;
    }
    private void setUserToSession(HttpServletRequest request,Map<String,Object> user) {
    	List<Map<String,Object>> list = (List<Map<String,Object>>)request.getSession().getAttribute("user");
    	if(list == null) {
    		list = new ArrayList<Map<String,Object>>();
    	}
    	for (Map<String, Object> map : list) {
			if(map.get("user").equals(user.get("user"))) {
				list.remove(map);
				break;
			}
		}
    	list.add(user);
    	//request.setAttribute("currentUser", user.get("user"));
    	request.getSession().setAttribute("user", list);
    }
    
    public void Logout(HttpServletRequest request,HttpServletResponse response){
    	//request.getSession().removeAttribute("user");
    	//String currentUser = request.getParameter("currentUser");
    	List<Map<String,Object>> list = (List<Map<String,Object>>)request.getSession().getAttribute("user");
    	if(list == null) {
    		list = new ArrayList<Map<String,Object>>();
    	}
    	for (Map<String, Object> map : list) {
			if(map.get("user").equals(this.getCurrentUser(request))) {
				list.remove(map);
				break;
			}
		}
    	/*Cookie cookie = new Cookie("user","");
		cookie.setMaxAge(0);
		response.addCookie(cookie);*/
    }
    
    public Map<String,Object> getLoginInfo(HttpServletRequest request){
    	//return (Map<String, Object>) request.getSession().getAttribute("user");
    	return getUserByCurrentUser(request);
    }
    
    private Map<String,Object> getUserByCurrentUser(HttpServletRequest request) {
    	//String currentUser = request.getParameter("currentUser");
    	Map<String,Object> user = null;
    	List<Map<String,Object>> list = (List<Map<String,Object>>)request.getSession().getAttribute("user");
    	if(list == null) {
    		list = new ArrayList<Map<String,Object>>();
    	}
    	for (Map<String, Object> map : list) {
			if(map.get("user").equals(this.getCurrentUser(request))) {
				user = map;
				break;
			}
		}
    	if(user == null && list.size()>0) {
    		user = list.get(0);
    	}
    	return user;
    }
    
    public String getCurrentUser(HttpServletRequest request) {
    	String currentUser = request.getParameter("currentUser");
    	if(StringUtils.isNotEmpty(currentUser)) {
    		return currentUser;
    	}
		Cookie[] cookies=request.getCookies();
		for (Cookie cookie : cookies) {
			if("currentUser".equals(cookie.getName())) {
				currentUser = cookie.getValue();
			}
		}/*
		//处理第一次登录时，没法从cookie获取当前刚登陆用户问题
		if(StringUtils.isNullOrEmpty(currentUser) || "null".equalsIgnoreCase(currentUser)) {
			currentUser = request.getParameter("currentUser");
			for (Cookie cookie : cookies) {
				if("currentUser".equals(cookie.getName())) {
					cookie.setValue(currentUser);
				}
			}
		}*/
		return currentUser;
	}

    public Map<String,Object> getUserByUser(String user) {  
        return userDao.getUserByUser(user);  
    }
    
    public Map<String,Object> getUserById(int id){
    	return userDao.getUserById(id);
    }
    
    public int insertUser(Map user){
    	return userDao.insertUser(user);
    }
    
    public int checkBelongAndGroup(int belong,String group){
    	return userDao.checkBelongAndGroup(belong, group);
    }
    
    public Map<String,Object> getUserByUserAndPwd(String user,String pwd){
    	return userDao.getUserByUserAndPwd(user, pwd);
    }
    public int updatePwd(String user,String pwd){
    	return userDao.updatePwd(user, pwd);
    }
    
    public List<Map> getUserList(Map param){
    	return userDao.getUserList(param);
    }
    public int updateUser(Map user){
    	return userDao.update(user);
    }
    
    public int getUserListCount(Map param){
    	return userDao.getUserListCount(param);
    }
    public List<Map> getByBelong(int belong){
    	return userDao.getByBelong(belong);
    }
    
    public List<Map> findByGroups(String group){
    	return userDao.findByGroups(group);
    }
    
    
    public int delete(int id){
    	return userDao.delete(id);
    }
    
    
    //微信端-login接口
    public Map<String, Object> wxApiLogin(String username,String password){
    	Map<String,Object> user=userDao.getUserByUser(username);
    	if(user!=null) {
    		if(MD5Utils.string2MD5(password).equals(user.get("password"))){
    			return user;
    		}
    	}
    	return null;
    }
    
    
	public void UpdateCodeNum(Integer userid) {
		// TODO Auto-generated method stub
		userDao.UpdateCodeNum(userid);
	}
	public List<User> findUser(String username) {
		// TODO Auto-generated method stub
		return userDao.findUser(username);
	}
	public User findUserList(String username) {
		// TODO Auto-generated method stub
		return userDao.findUserList(username);
	}
	public User findUserLoginType(int userid) {
		// TODO Auto-generated method stub
		return userDao.findUserLoginType(userid);
	}
	public void updateLogintyepe(int userid) {
		// TODO Auto-generated method stub
		userDao.updateLogintyepe(userid);
	}
	public void updateLogintyepes(int userid) {
		// TODO Auto-generated method stub
		userDao.updateLogintyepes(userid);
	}
	public void updatePasswordTime(int userid) {
		// TODO Auto-generated method stub
		userDao.updatePasswordTime(userid);
	}
	public void updateCodenums(int userid) {
		// TODO Auto-generated method stub
		userDao.updateCodenums(userid);
	}
	public User findUserNameByUserpassword(String username, String spass) {
		// TODO Auto-generated method stub
		return userDao.findUserNameByUserpassword(username,spass);
	}
	public User findUserLoginTypes(String username) {
		// TODO Auto-generated method stub
		return userDao.findUserLoginTypes(username);
	}
	public void updatePassword(String password, Integer id) {
		// TODO Auto-generated method stub
		userDao.updatePassword(password,id);
	}
    
    
    
    
}