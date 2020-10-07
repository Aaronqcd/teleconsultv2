<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name=renderer content=webkit>
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<title>远程会诊管理系统</title>
<link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css">
<link rel="stylesheet" type="text/css" href="css/common.css">
<link href="https://cdn.bootcss.com/twitter-bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet"><!-- bootstrap.css-->
<script src="js/jquery-3.min.js"></script>
<script src="layui-v2.4.5/layui.js"></script>
<script src="js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<script src="js/recordvideo.js"></script>
<style>
.layui-table, .layui-table-view {
	margin: 0;
}
</style>
</head>
<body style="background: #f5f5f5;">
	<div class="layui-side" style="background-color:#fff;border-right:#ccc">
		<div id="meetingTree"></div>
	</div>
	<div class="layui-body">
		<div class="user-right-content">
			<div class="layui-form tc-tools">
				<div class="layui-inline tc-search">
					<input type="text" name="searchValue" id="searchValue" value=""
						placeholder="会诊编号|视频名称" class="layui-input">
					<button data-type="reload" class="layui-btn layui-btn-sm"
						id="searchBtn">
						<i class="layui-icon layui-icon-search"></i>
					</button>
				</div>
			</div>
			<table id="layTable" lay-filter="layTable"></table>
		</div>
	</div>
	<!-- 表格头部工具栏 -->
	<script type="text/html" id="toolbar">
  <div class="layui-btn-container">
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main"  lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除</button>
  </div>
	</script>
	<!-- 表格行内工具栏 -->
	<script type="text/html" id="toolline">
  <div class="row-btn-container">
    <a class="layui-btn layui-btn-xs layui-btn-radius tc-bg-main" lay-event="name">重命名</a>
    <a class="layui-btn layui-btn-xs layui-btn-radius tc-bg-main" lay-event="play">播放</a>
    <!-- <a class="layui-btn layui-btn-xs layui-btn-radius tc-bg-main" lay-event="download2" href="{{d.videoUrl}}" download="{{d.videoUrl}}">下载</a> -->
    <a class="layui-btn layui-btn-xs layui-btn-radius tc-bg-main" lay-event="delete">删除</a>
</div>
	</script>
</body>
</html>