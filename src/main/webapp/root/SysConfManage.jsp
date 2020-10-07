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
    <div class="layui-form tc-tools" style="margin: -7px auto 0 ;" >
    	<p>配置管理列表<p>
    </div>
    <table class="layui-hide" id="layTable" lay-filter="layTable"></table>
  </div>
</div>
<script src="https://cdn.bootcss.com/jquery/3.0.0/jquery.min.js"></script><!-- jquery.js -->
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="../js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<script>

layui.use(['table','form'], function(){
  var $=layui.$
  //表格
  var table = layui.table;
  table.render({
    elem: '#layTable'
    ,limit: 20
    ,url:'getSysConfs'
    ,toolbar: '#toolbar'
    ,defaultToolbar:[]
    ,height: 'full-40'
    ,cols: [[
      {type: 'checkbox'},
      {field:'remark', title:'说明', width:350},
      {field:'confValue', title:'内容', align:'center', width:400, sort: true, templet:function(data){
    	  var v = data.confValue;
    	  if("CONF_TEUDBORAD" == data.confKey){
    		  if(data.confValue == "0" ){
    			  v = "不开通";
    		  }else if(data.confValue == "1" ){
    			  v = "开通";
    		  }
    	  }else if("MEETING_CYCLE" == data.confKey){
    		  if(data.confValue == "0" ){
    			  v = "不删除";
    		  }else if(data.confValue == "00" ){
    			  v = "一个月";
    		  }else if(data.confValue == "000" ){
    			  v = "三个月";
    		  }else if(data.confValue == "0000" ){
    			  v = "六个月";
    		  }
    	  }
    	  return v;
      }},
      {field:'role', title:'操作', align:'center', templet:function(d){
    		return '<a class="layui-btn layui-btn-xs layui-btn-radius tc-bg-main" lay-event="check">编辑</a>';
      }}
       ]],
    id: 'SysConfs',
    page: true
  });
  //行内工具栏事件
  table.on('tool(layTable)', function(obj){
    var data = obj.data;
    if(obj.event === 'check'){
    	layer.confirm('确认要编辑当前数据?', {icon: 3, title:'系统提示'}, function(index){  	  
    		layer.open({
                type: 2, 
                content: 'SysConForm?id='+data.id,
                title:'修改配置信息',
                area: ['480px', '400px'],
                btn:['确定','取消'], 
                yes:function(index,layero){ 
                  var body = layer.getChildFrame('body', index);
                  $(body).find("button[lay-submit]").click()
                }
              });
        	layer.close(index);
        });
    	
    	
      }
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