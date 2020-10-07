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
  <link href="https://cdn.bootcss.com/twitter-bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet"><!-- bootstrap.css-->
</head>
<body>
<!-- 左侧 -->
<div class="layui-side layui-bg-black" style="width: 230px;border-right: 1px solid #ddd;">
  <div class="layui-side-scroll" style="background: #fff;width: 250px">
    <div id="hospitalTree" class="tree-hospital" style="width: 230px;margin-top: 15px;"></div>
  </div>
</div>
<!-- 右侧 -->
<div class="layui-body" style="background:#f5f5f5;left: 230px">
  <div class="user-right-content">
    <div class="layui-form tc-tools">
      <div class="layui-inline tc-search">
        <input type="text" name="seachValue" id="seachValue" placeholder="搜索" class="layui-input">
        <button data-type="reload" class="layui-btn layui-btn-sm" id="searchBtn"><i class="layui-icon layui-icon-search"></i></button>
        <input type="hidden" name="status" id="status" value=""/>
      </div>   
    </div>
    <table class="layui-hide" id="layTable" lay-filter="layTable"></table>
  </div>
</div>
<!-- 表格头部工具栏 -->
<script type="text/html" id="toolbar">
  <div class="layui-btn-container">
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除</button>
  </div>
</script>
<!-- 表格行内工具栏 -->
<script type="text/html" id="toolline">
  <div class="layui-btn-container">
    //根据会诊状态控制按钮
    <a class="layui-btn layui-btn-xs layui-btn-radius tc-bg-main" lay-event="check">同意</a>
    <a class="layui-btn layui-btn-xs layui-btn-warm layui-btn-radius" lay-event="reject">拒绝</a>
    <a class="layui-btn layui-btn-xs layui-btn-danger layui-btn-radius" lay-event="stop">中断</a>
  </div>
</script>
<script src="https://cdn.bootcss.com/jquery/3.0.0/jquery.min.js"></script><!-- jquery.js -->
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="../js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<script>
  $(function(){
    // 机构树
    $('#hospitalTree')
    .treeview({
      color: "#666",
      expandIcon: 'glyphicon glyphicon-chevron-right',
      collapseIcon: 'glyphicon glyphicon-chevron-down',
      nodeIcon: "glyphicon glyphicon-bookmark",
      selectedBackColor:"#d4f9fb",
      selectedColor:"#666",
      showTags: true,
      data: defaultData,
      onNodeUnselected: function(event, data) {
        $(this).treeview('selectNode', [ 0 ,{ silent: true } ]);
        // 这里需要加载所有会诊..
      },
      onNodeSelected: function(event, data) {
        if(data.nodeId!=0)
          $(this).treeview('unselectNode', [0, { silent: true } ]);
          // 这里需要加载当前状态的会诊或者所有会诊..
          if(data.type=="all")$("#status").val("");
          else $("#status").val(data.type);
          $('#searchBtn').click();
          console.log(data.type)
      }
    })
    .treeview('selectNode', [ 0, { silent: true } ])
  })
// 会诊状态
var defaultData = [
  {
    text: '会诊中心',
    type:'all',
    icon: 'layui-icon layui-icon-dialogue icon-meeting',
    nodes: ${node}
  }
];

var meetingstatus=${meetingstatus};
var meetingaction=${meetingaction};

layui.use(['table','form'], function(){
  var $=layui.$
  //表格
  var table = layui.table;
  table.render({
    elem: '#layTable'
    ,url:'getMeetings'
    ,toolbar: '#toolbar'
    ,defaultToolbar:[]
    ,height: 'full-40'
    ,cols: [[
      {type: 'checkbox', width:35}
      ,{field:'encode', title:'会诊编号', width:132}
      ,{field:'account', title:'创建账号', width:100, sort: true }
      //,{field:'username', title:'创建者', width:80,  sort: true}
      ,{field:'title', title:'会诊主题'}
      ,{field:'time', title:'时间', width:100}
      ,{field:'record', title:'会诊录制',align:'center', width:66,templet:function(d){
        return '<a href="javascript:;">查看</a>';
      }}
      ,{field:'note', title:'会诊纪要'}
      ,{field:'status', title:'会诊状态', width:70,templet:function(d){
        return meetingstatus[d.status];
      }}
      ,{field:'status', title:'动作',width:140,templet:function(d){
    	  return meetingaction[d.status-1][d.role];
      }}
    ]],
    id: 'meeting',
    page: true
  });
  

  var $ = layui.$
  var active = {
	  reload: function(){
		 //执行重载
		 table.reload('meeting', {
		   page: {
			 curr: 1 //重新从第 1 页开始
		   },
		   where: {
			 seachValue: $("#seachValue").val(),
			 status:$("#status").val()
		   }
	   });
     }
  }
  
  $('#searchBtn').on('click', function(){
    var type = $(this).data('type');
    active[type] ? active[type].call(this) : '';
  });

  //表头工具栏事件
  table.on('toolbar(layTable)', function(obj){
    var checkStatus = table.checkStatus(obj.config.id);
    var datas = checkStatus.data; //选中的行数据：checkStatus.data
    var count=datas.length; 
    var id  = obj.config.id;
    var status_arr = [];
    for(var i=0 ;  i < datas.length;i++){
    	if(datas[i].status ==="3"){
    		status_arr.push(datas[i].status);
    	}
    }
    switch(obj.event){
      case 'delete':
    	 if(id=="meeting" && status_arr.length >0){
    		  layer.msg('存在正在会诊的会诊不能删除！', {icon: 7}); 
    	 }else{
    		 if(count==0)
    	          layer.msg('未选中任何数据！', {icon: 7}); 
    	        else
    	          layer.confirm('确认删除选中的数据?', {icon: 3, title:'系统提示'}, function(index){
    	        	  var ids=new Array();
    	        	  for(var i=0;i<datas.length;i++)ids.push(datas[i].id);
    	        	  $.get("dropMeeting?ids="+ids.toString(),function(result){
    	        		  	if(result.code>0){
    		          			layer.msg("删除成功");
    		          			table.reload('meeting');
    		          		}
    		          		else{
    		          			layer.msg("删除失败");
    		          		}
    		          },"json");
    	              layer.close(index);
    	          });
    	      break;
    	 }
    };
  });
  //行内工具栏事件
  table.on('tool(layTable)', function(obj){
    var data = obj.data;
    //console.log(obj)
    if(obj.event === 'check'){
      layer.confirm('确认同意此条会诊申请?', {icon: 3, title:'系统提示'}, function(index){
    	$.get("MeetingAction",{id:data.id,action:"check"},function(result){
    		if(result.code){
        		layer.msg("审核成功！");
        		table.reload('meeting');
        	}
    	},"json");
        layer.close(index);
      });
    }else if(obj.event === 'reject'){
      layer.confirm('确认拒绝此条会诊申请?', {icon: 3, title:'系统提示'}, function(index){
    	$.get("MeetingAction",{id:data.id,action:"reject"},function(result){
      		if(result.code){
          		layer.msg("拒绝成功！");
          		table.reload('meeting');
          	}
      	},"json");
    	layer.close(index);
      });
    }else if(obj.event === 'stop'){
      layer.confirm('会诊正在进行中，确认中断?', {icon: 3, title:'系统提示'}, function(index){
   	    $.get("MeetingAction",{id:data.id,action:"stop"},function(result){
       		if(result.code){
           		layer.msg("中断成功！");
           		table.reload('meeting');
           	}
       	},"json");
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