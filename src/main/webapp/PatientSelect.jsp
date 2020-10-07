<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择病人</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/layui-v2.4.5/css/layui.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/formSelects-v4.css">
<script src="${pageContext.request.contextPath}/layui-v2.4.5/layui.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery-3.min.js"></script>
<script src="${pageContext.request.contextPath}/js/formSelects-v4.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script src="${pageContext.request.contextPath}/js/patient.js"></script>
</head>
<body>
	<div style="height: 20px"></div>
	<div class="layui-row">
		<div class="tc-form-item layui-col-sm5">
			<label class="layui-form-label">病人ID</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" id="iptPatientId" />
			</div>
		</div>
		<div class="tc-form-item layui-col-sm5">
			<label class="layui-form-label">日期</label>
			<div class="layui-input-block">
				<input type="text" class="layui-input" id="iptInDate" />
			</div>
		</div>
		<div class="tc-form-item layui-col-sm1 layui-col-sm-offset1">
			<button data-type="reload" class="layui-btn layui-btn-sm"
				id="btnSearch">
				<i class="layui-icon layui-icon-search"></i>
			</button>
		</div>
	</div>
	<table id="mainTable" lay-filter="mainTable">
		<thead>
			<tr>
				<th lay-data="{field:'patient_id'}">病人ID</th>
				<th lay-data="{field:'indate'}">日期</th>
			</tr>
		</thead>
	</table>
</body>
</html>