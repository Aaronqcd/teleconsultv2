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

<style type="text/css">
    .layui-table-cell {
        max-height: 80px;
    }
    .layui-table-tips-main {
    	background-color: #e6e6e6;
    	border-color: #06b4bf;
    }
</style>

<body style="background:#f5f5f5">
<!-- 居中 -->
<div style="background:#f5f5f5;width: 1060px ;margin: 0 auto;">
  <div class="user-right-content" >
    <div class="layui-form tc-tools" style="margin-top: -7px;">
      <div class="layui-inline" style="width: 260px">
        <input type="text" class="layui-input" id="dateInput" placeholder="时间范围">   
      </div>
      <div class="layui-inline tc-search">
        <input type="" id="user" placeholder="搜索账号、姓名、操作" class="layui-input">
        <button id="searchBtn" data-type="reload" class="layui-btn layui-btn-sm"><i class="layui-icon layui-icon-search"></i></button>
      </div>
    </div>
    <table class="layui-hide" id="layTable" lay-filter="layTable"></table>
  </div>
</div>
<!-- 表格头部工具栏 -->
<script type="text/html" id="toolbar">
  <div class="layui-btn-container" style="display: none;">
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除</button>
  </div>
</script>

<script src="https://cdn.bootcss.com/jquery/3.0.0/jquery.min.js"></script><!-- jquery.js -->
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="../js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<script>

layui.use(['table','form','laydate'], function(){
  var $=layui.$
  //表格
  var table = layui.table;
  table.render({
    elem: '#layTable'
    ,url:'getLogs'
    ,toolbar: '#toolbar'
    ,defaultToolbar:[]
    ,height: 'full-40'
    ,cols: [[
      {type:'checkbox'},
      {field:'account', title:'账号', width:130, sort: true },
      {field:'username', title:'姓名', width:130, sort: true},
      {field:'time', title:'时间', width:200},
      {field:'note', title:'操作',templet:function(data){
    	  var msg = data.note;
    	  if(msg.indexOf("[{") >= 0){
    		  msg = msg.replace("[{","{");
    		  msg = msg.substring(0,msg.length-1);
    		  msg = msg.replace(/},{/g,"}<br>{");
    	  }
    	  //console.info(msg);
    	  return msg;
      }}
    ]],
    id: 'logs',
    page: true
  });
  
  var $ = layui.$
  var active = {
	  reload: function(){
		 //执行重载
		 table.reload('logs', {
		   page: {
			 curr: 1 //重新从第 1 页开始
		   },
		   where: {
			 stime:$("#dateInput").val().substr(0,19),
			 etime:$("#dateInput").val().substr(22),
			 user:$("#user").val()
		   }
	   });
     }
  }
  
  $('#searchBtn').on('click', function(){
    var type = $(this).data('type');
    active[type] ? active[type].call(this) : '';
  });
  
  var laydate = layui.laydate;
  laydate.render({ 
    elem: '#dateInput'
    ,type: 'datetime'
    ,range: true //或 range: '~' 来自定义分割字符
  });
  //表头工具栏事件
  table.on('toolbar(layTable)', function(obj){
    var checkStatus = table.checkStatus(obj.config.id);
    var datas = checkStatus.data; //选中的行数据：checkStatus.data
    var count=datas.length; 
    switch(obj.event){
      case 'delete':
        if(count==0){
        	layer.msg('未选中任何数据！', {icon: 7}); 
        }
        else
          layer.confirm('确认删除选中的数据?', {icon: 3, title:'系统提示'}, function(index){
        	  var ids=new Array();
        	  for(var i=0;i<datas.length;i++)ids.push(datas[i].id);
        	  $.get("dropLog?ids="+ids,function(result){
        		if(result.code>0){
        			layer.msg("删除成功",{icon: 1},function(){
        				table.reload('logs');
        			});
        		}
        		else{
        			layer.msg("删除失败");
        		}
        	},"json");
            layer.close(index);
          });
      break;
    };
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