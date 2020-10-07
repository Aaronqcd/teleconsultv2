<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="../layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="../css/common.css"><!-- 自定义css-->
</head>

<body style="background:#f5f5f5">
<!-- 居中 -->

<div style="background:#f5f5f5;width: 1060px ;margin: 0 auto;">
  <div class="user-right-content" >
   <div class="layui-form tc-tools" style="margin-top: -7px;">
    <div class="layui-inline" style="padding-left:40px;width: 1020px" >
    	  <input type="hidden" value="" name="type"  id="type" >
		  <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" data-type="reload" id="getthisDay">当&nbsp;&nbsp;日</button>
		  <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" data-type="reload" id="getthisMenth">本&nbsp;&nbsp;月</button>
		  <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" data-type="reload" id="getUpMenth">上&nbsp;&nbsp;月</button>
		  <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" data-type="reload" id="getthisJiDu">本季度</button>
		  <div class="layui-inline tc-search" style="width:200px;margin-left: 100px">
	        <input type="text" class="layui-input" id="dateInput"  placeholder="自定义时间">
	        <button id="shoshuo" data-type="reload" class="layui-btn layui-btn-sm"><i class="layui-icon layui-icon-search"></i></button>
	      </div>
	      <div class="layui-inline" style="width:430px" >
	       	<input type="text" id="timeCodeShow" style="font-weight:bold;font-family:Serif;text-align:center;border: 0px"  readonly="readonly" value=""  class="layui-input">
	      </div>
    </div>
  </div>
    <table class="layui-hide" id="layTable" lay-filter="layTable"></table>
</div>
</div>
<script type="text/html" id="toolbar">
  <div class="layui-btn-container" style="display: none;">
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除</button>
  </div>
</script>
<script src="https://cdn.bootcss.com/jquery/3.0.0/jquery.min.js"></script><!-- jquery.js -->
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="../js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<script>
layui.use(['element','table','form','laydate'], function(){
  var $=layui.$,form = layui.form;
  //表格
  var table = layui.table;
  table.render({
    elem: '#layTable'
    ,url:'getMeetingCounds'
    ,defaultToolbar:[]
  ,toolbar: '#toolbar'
    ,height: 'full-40'
    ,cols: [[
      {checkbox: true, fixed: true},
      {field:'code', title:'会诊编号',align:'center',width:180, sort: true },
      {field:'stearTime', title:'起始时间', width:180,align:'center',sort: true},
      {field:'endTime', title:'结束时间', width:180,align:'center',sort: true},
      {field:'people', title:'会诊人数', align:'center',sort: true},
      {field:'duration', title:'会诊时长（分）', align:'center', sort: true},
      {field:'durationCount', title:'总耗时（分）', align:'center' , sort: true},
    ]],
    id: 'meetingCounds',
    page: true,
    done: function(res, curr, count){
        $("#timeCodeShow").attr("value","总耗时：   "+res.timeCount+"分钟");
      }
  });
  
  var $ = layui.$, active = {
	reload: function(){
	 //执行重载
	 table.reload('meetingCounds', {
	   page: {
		 curr: 1 //重新从第 1 页开始
	   },
	   where: {
		 stime:$("#dateInput").val().substr(0,10),
		 etime:$("#dateInput").val().substr(13),
		 type:$("#type").val()
	   }
	 }, 'data');
	}
 }
  
  $('#getthisDay').on('click', function(){
	  $("#type").val("1");
	  $("#dateInput").val("");
	    var type = $(this).data('type');
	    active[type] ? active[type].call(this) : '';
	  });
  $('#getthisMenth').on('click', function(){
	  $("#type").val("2");
	  $("#dateInput").val("");
	    var type = $(this).data('type');
	    active[type] ? active[type].call(this) : '';
	  });
  $('#getUpMenth').on('click', function(){
	  $("#type").val("3");
	  $("#dateInput").val("");
	    var type = $(this).data('type');
	    active[type] ? active[type].call(this) : '';
	  });
  $('#getthisJiDu').on('click', function(){
	  $("#type").val("4");
	  $("#dateInput").val("");
	    var type = $(this).data('type');
	    active[type] ? active[type].call(this) : '';
	  });
  $('#shoshuo').on('click', function(){
	  $("#type").val("5");
	  if($("#dateInput").val() == "" || $("#dateInput").val().length <1 ){
		  layer.msg("请选择要搜索的时间段");
		  return;
	  }
	    var type = $(this).data('type');
	    active[type] ? active[type].call(this) : '';
	  });
  var laydate = layui.laydate;
  	laydate.render({
	    elem: '#dateInput'
	    ,range: true
	  });
  
});
</script>

<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>