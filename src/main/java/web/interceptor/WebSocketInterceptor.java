package web.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.va.removeconsult.service.UserService;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
	static final Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);
    @Autowired
    private UserService userService;

    @Override
    public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2, Exception arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler,
            Map<String, Object> map) throws Exception {
    	try {
	        if (request instanceof ServletServerHttpRequest) {
	            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
	            Map<String, Object> user = null;
	            List<Map<String, Object>> list = (List<Map<String, Object>>) serverHttpRequest.getServletRequest().getSession().getAttribute("user");
	            if (list == null) {
	                list = new ArrayList<Map<String, Object>>();
	            }
	            for (Map<String, Object> currentMap : list) {
	                if (currentMap.get("user").equals(getCurrentUser(serverHttpRequest.getServletRequest()))) {
	                    user = currentMap;
	                    break;
	                }
	            }
	            if (user == null && list.size() > 0) {
	                user = list.get(0);
	            }
	            String url = serverHttpRequest.getServletRequest().getRequestURL().toString();
	            String meetingId = serverHttpRequest.getServletRequest().getParameter("meetingId");
	
	            String loginId = (String) user.get("user");
	            map.put("loginId", loginId);
	            if(StringUtils.isNotBlank(meetingId) ){
	            	map.put("meetingAndloginId", meetingId+"_"+loginId);
	            }
	        }
    	} catch (Exception e) {
    		logger.error("websocket beforeHandshake error", e);
    		return false;
    	}
        return true;
    }

    public String getCurrentUser(HttpServletRequest request) {
        String currentUser = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("currentUser".equals(cookie.getName())) {
                currentUser = cookie.getValue();
            }
        }
        return currentUser;
    }
}
