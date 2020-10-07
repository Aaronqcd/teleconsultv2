<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	Object msg = request.getSession().getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
  <link href="css/bootstrap.min.css" rel="stylesheet"><!-- bootstrap.css-->

</head>
<style type="text/css">
  	.layui-btn-normal{
  		background-color: #06b4bf !important;
  	}

  .video-rate-select div{
    font-size: 24px !important;
  }

  .video-rate-select{
    margin: 20px auto;
    width: 100px;
    line-height: 24px;
  }

</style>
<body>
<!-- 左侧 -->
<div class="layui-side layui-bg-black" style="width: 210px;border-right: 1px solid #ddd;">
  <div class="layui-side-scroll" style="background: #fff;width: 230px">
    <div id="hospitalTree" class="tree-hospital" style="width: 210px;margin-top: 15px;"></div>
  </div>
</div>
<!-- 右侧 -->
<div class="layui-body" style="background:#f5f5f5;left: 210px">
  <div class="user-right-content">
    <div class="layui-form tc-tools">
      <div class="layui-inline tc-search">
      
        <input type="text" name="seachValue" id="seachValue" value="" placeholder="搜索" class="layui-input">
        <button data-type="reload" class="layui-btn layui-btn-sm" id="searchBtn"><i class="layui-icon layui-icon-search"></i></button>
        <input type="hidden" name="status" id="status" value=""/>
        <input type="hidden" name="type" id="type" value="0"/>
      </div>   
    </div>
    <table class="layui-hide" id="layTable" lay-filter="layTable"></table>
  </div>
</div>

<form class="layui-form usevideo" action="" style="display: none">
  <div class="layui-form-item">
    <div class="layui-input-block video-rate-select">
      <input type="radio" name="use-video" value="user_16_9" title="16:9">
      <input type="radio" name="use-video" value="user_4_3" title="4:3">
    </div>
  </div>
</form>
<!-- 表格头部工具栏 -->
<script type="text/html" id="toolbar">
  <div class="layui-btn-container">
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="add"><i class="layui-icon layui-icon-add-circle"></i>申请会诊</button>
    <button id="meetingDelete"  class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main"  lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除</button>
  </div>
</script>
<!-- 表格行内工具栏 -->
<script type="text/html" id="toolline">
  <div class="layui-btn-container">
    //根据会诊状态控制按钮
    {{#  if(d.status == 1){ }}
    <a class="layui-btn layui-btn-xs layui-btn-radius tc-bg-main" lay-event="check">加入</a>
    {{#  } }}
    {{#  if(d.status == 2){ }}
    <a class="layui-btn layui-btn-normal layui-btn-xs layui-btn-radius" lay-event="stop">发起</a>
    <a class="layui-btn layui-btn-danger layui-btn-xs layui-btn-radius" lay-event="stop">取消</a>
    {{#  } }}
    {{#  if(d.status == 3){ }}
    <a class="layui-btn layui-btn-xs layui-btn-warm layui-btn-radius" lay-event="reject">重新申请</a>
    {{#  } }}
  </div>

</script>
<script src="js/jquery-3.min.js"></script><!-- jquery.js -->
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/bootstrap-treeview.js"></script><!-- 树形插件 -->
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
          if(data.type=="all") $("#status").val("");
          else{
        	  $("#status").val(data.type);
        	  $("#type").val(data.roletype);
          }
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
  
var applystatus=${applystatus};
var attendstatus=${attendstatus}
var meetingaction=${meetingaction};
var layui;
function findFile(href){
	if(href === undefined || ""=== href || href === null){
		layer.msg('该用户未上传相应资料！');
	}else{
		return true;
	}
	  
}
layui.use(['table','form', 'layer'], function(){
    var $=layui.$
    var layer = layui.layer

    var meetingUrl = "";

    // 弹出视频分辨率选择框
    function showVideoRateSelect(url){
        parent.window.location.href = url
        // meetingUrl = url;
        // console.log($)
        // var videoRateHtml = $(".usevideo").clone().show().get(0).outerHTML
        // layer.open({
        //     type:1,
        //     title: "请选择视频的分辨率",
        //     content: videoRateHtml,
        //     area: ['200px', '160px']
        // })

    }

    // 使用分辨率比例进入会诊
    $(document).on("click", ".video-rate-select .layui-form-radio", function(event){
        var videoRole = $(this).prev().val()

        console.log(meetingUrl, videoRole)
        parent.window.location.href = meetingUrl + "&videoRole="+videoRole;
    })



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
      //,{field:'account', title:'创建账号', width:100, sort: true }
      ,{field:'username', title:'创建者', width:68,  sort: true}
      ,{field:'title', title:'会诊主题'}
      //,{field:'meetingtype', title:'类型', width:80}
      ,{field:'members', title:'与会人'}
      ,{field:'time', title:'时间', width:100}
      ,{field:'attrId', title:'会诊资料',align:'center', width:66,templet:function(d){
    	  var href = "";
    	  if(d.sysName){
    		  href = "window.open('headimg/"+d.sysName+"')";
    	  }
        return '<a href="javascript:'+href+';" onclick="findFile('+href+')">查看</a>';
      }}
      ,{field:'note', title:'会诊纪要'}
      ,{field:'status', title:'会诊状态', width:68,templet:function(d){
    	if(d.role=="applybutton")return applystatus[d.status];
    	else return attendstatus[d.status];
      }}
      ,{field:'status', title:'动作',width:140,templet:function(d){
    	  return meetingaction[d.status-1][d.role];
      }}
    ]] ,
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
			 seachValue:$("#seachValue").val(),
			 status:$("#status").val(),
			 type:$("#type").val()
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
      case 'add':
        layer.open({
          type: 2, 
          content: 'MeetingForm',
          title:'申请会诊',
          area: ['800px', '600px'],
          btn:['提交申请','取消'], 
          yes:function(index,layero){ 
            var body = layer.getChildFrame('body', index);
            $(body).find("button[lay-submit]").click()
          }
        });
      break;
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
    	       		  $.get("dropMeeting?ids="+ids,function(result){
    	           		  	if(result.code>0){
    	           		  		table.reload('meeting');
    	  	          			layer.msg("删除成功");
    	  	          		}
    	  	          		else{
    	  	          			layer.msg("删除失败！正在会诊的会诊不能被删除!");
    	  	          		}
    	   	          },"json");
    	                 layer.close(index);
    	             });
    	      break;
    	    };
    	  }
        
  });
  
  //行内工具栏事件
  table.on('tool(layTable)', function(obj){
    var data = obj.data;
    if(obj.event === 'cancle'){
      layer.confirm('确认取消此条会诊申请?', {icon: 3, title:'系统提示'}, function(index){
        $.get("MeetingAction",{id:data.id,action:"cancle"},function(result){
        	if(result.code){
        		table.reload('meeting');
        		layer.msg("取消成功！");
        	}
        },"json");
    	layer.close(index);
      });
    }else if(obj.event === 'accept'){
      layer.confirm('确认加入此条会诊申请?', {icon: 3, title:'系统提示'}, function(index){
   	    $.get("MeetingAction",{id:data.id,action:"accept"},function(result){
         	if(result.code==1){
         		table.reload('meeting');
         		layer.msg("加入成功！");
         	}
        },"json");
        layer.close(index);
      });
    }else if(obj.event === 'recheck'){
      layer.confirm('确认重新申请此条会诊?', {icon: 3, title:'系统提示'}, function(index){
    	  $.get("MeetingAction",{id:data.id,action:"recheck"},function(result){
           	if(result.code==1){
           		table.reload('meeting');
           		layer.msg("申请成功！");
           	}
          },"json");
          layer.close(index);
      });
    }else if(obj.event === 'start'){
        var url ="Meeting"+"?meetingId="+data.id+"&userid="+parent.window.userID;
        showVideoRateSelect(url)
    }else if(obj.event === 'check'){
    	$.get("currentMeeting",{meetingId:data.id},function(result){
            var isPresenter = result.isPresenter;
            var meetingStatus = result.meetingInfo.status;
            //需要主持人先开房间
            if( (isPresenter== true) || (isPresenter== false && meetingStatus == 3)){
                var url ="Meeting"+"?meetingId="+data.id+"&userid="+result['currentUser']['id'];
                showVideoRateSelect(url)
            }else{
                layer.msg("需要主持人先发起会诊才能进入");
            }
        },"json");
    }else if(obj.event === 'entry'){
    	$.get("currentMeeting",{meetingId:data.id},function(result){
            var isPresenter = result.isPresenter;
            var meetingStatus = result.meetingInfo.status;
            //需要主持人先开房间
            if( (isPresenter== true) || (isPresenter== false && meetingStatus == 3)){
                showVideoRateSelect("Meeting"+"?meetingId="+data.id+"&userid="+result['currentUser']['id']+"&currentUser="+result['currentUser']['user'])
            }else{
                layer.msg("需要主持人先发起会诊才能进入");
            }
        },"json");
        
    }
  });
});

var d = window.setInterval("getNews()", 1000);
function InitAjax() {

	var ajax = false;
	try {

		ajax = new ActiveXObject("Msxml2.XMLHTTP");

	} catch (e) {

		ajax = false;
	}
	if (!ajax && typeof XMLHttpRequest != 'undefined') {

		ajax = new XMLHttpRequest();
	}

	return ajax;

}

function getNews() {

	//需要进行Ajax的URL地址
	var url = "fronCheckLogined";
	//实例化Ajax对象
	var ajax = InitAjax();

	//使用Get方式进行请求
	ajax.open("POST", url, true);
	//获取执行状态
	ajax.onreadystatechange = function(){
	//如果执行是状态正常，那么就把返回的内容赋值给上面指定的层
	if (ajax.readyState == 4 && ajax.status == 200){
	
		var str = ajax.responseText;
		if(str != "normal"){
			alert(str);
			window.clearInterval(d);
			  $.post("Logout",null,function(result){
				  window.location.reload();
			  })
			
		}
	};
		
	};
	var msg =	"${msg}";
	if(msg != null && msg != ""){
		//发送空
		ajax.send(null);
		msg = null;
	}
	
}
</script>
<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>