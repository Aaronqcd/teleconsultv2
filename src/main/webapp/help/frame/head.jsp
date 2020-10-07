<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<title>在线帮助</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/layui-v2.4.5/css/layui.css">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/css/common.css">
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/css/help.css">
</head>
<script src="<%=request.getContextPath()%>/layui-v2.4.5/layui.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-3.min.js"></script>
<body class="layui-layout-body">
	<div class="layui-layout layui-layout-admin">
		<div class="layui-header">
			<input id="basePath" type="hidden"
				value=<%=request.getServerName() + ":" + request.getServerPort() + request.getContextPath()%>>
			<div class="layui-logo">
				<img src="<%=request.getContextPath()%>/images/logo.png">在线帮助
			</div>
		</div>
		<div class="layui-body">
			<div class="help-menu">
				<%@ include file="menu.jsp"%></div>
			<div class="help-body">