<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String basePath = request.getContextPath();
%>
<ul>
	<li
		<%=request.getServletPath().equalsIgnoreCase("/help/index.jsp") ? "class='selected'" : ""%>><a
		href="<%=basePath%>/help">帮助中心</a></li>
	<li
		<%=request.getServletPath().equalsIgnoreCase("/help/forgetpwd.jsp") ? "class='selected'" : ""%>><a
		href="<%=basePath%>/help/forgetpwd.jsp">忘记密码</a></li>
	<li
		<%=request.getServletPath().equalsIgnoreCase("/help/changepwd.jsp") ? "class='selected'" : ""%>><a
		href="<%=basePath%>/help/changepwd.jsp">修改密码</a></li>
	<li
		<%=request.getServletPath().equalsIgnoreCase("/help/screenshare.jsp") ? "class='selected'" : ""%>><a
		href="<%=basePath%>/help/screenshare.jsp">屏幕分享</a></li>
</ul>