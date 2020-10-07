package web.interceptor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.va.removeconsult.service.UserService;

public class AuthAdminInterceptor  implements HandlerInterceptor{
	@Resource  
    private UserService userService;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse respone, Object handler, Exception e)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse respone, Object handler, ModelAndView mv)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse respone, Object handler) throws Exception {
		// TODO Auto-generated method stub
		boolean flag=false;
		//防止劫持
		respone.setHeader("x-frame-options", "SAMEORIGIN");
		String[] IGNORE_URI = {"/Login","/404.html","/index.jsp","/PasswordReset"};
		String servletPath=request.getServletPath();
		for(String s:IGNORE_URI){
			if(servletPath.contains(s)){
				flag=true;
				break;
			}
		}
		if(!flag){
			//System.err.println("admin:servletPath=="+servletPath+"     cookie.currentUser=="+userService.getCurrentUser(request));
			//Map user=(Map<String,Object>)request.getSession().getAttribute("user");
			Map<String, Object> user = userService.getLoginInfo(request);
			if(user==null){
				Cookie[] cookies=request.getCookies();
				for(Cookie cookie:cookies){//遍历cookie数组
				    if(!cookie.getName().equals("user")) continue;
				    String cuser=cookie.getValue();
				    if(cuser.equals(""))break;
				    flag=userService.updateLogin(request,cuser);
				    /*if(flag){				    	
				    	user=(Map<String,Object>)request.getSession().getAttribute("user");
				    	flag=((int)user.get("id")==1);
				    	if(!flag){
							respone.sendRedirect("/teleconsult/Default");
							return flag;
						}
				    }*/
				    if(flag)return flag;
				}
				request.setAttribute("message", "请先登录！");
				//request.getRequestDispatcher("/login").forward(request, respone);
				respone.sendRedirect("/teleconsult/admin/Login");
				return flag;
			}
			else{
				flag=((int)user.get("id")==1);
				if(!flag){
					respone.sendRedirect("/teleconsult/admin/Default");
				} else {
					/*boolean isLastLogin = userService.isLastLoginFlag(request);
					if(!isLastLogin) {
						request.setAttribute("message", "已在其它地方登录");
						respone.sendRedirect("/teleconsult/admin/Login");
					}*/
				}
			}
		}
		return flag;
	}

}
