<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
  <link href="css/bootstrap.css" rel="stylesheet"><!-- bootstrap.css-->
	<!-- 全局样式 -->
	<link rel="stylesheet" href="css/overrall.min.css">
	<!-- 图片查看器插件 -->
	<link rel="stylesheet" href="css/viewer.min.css">
	<!-- 音乐播放器插件 -->
	<link rel="stylesheet" href="css/APlayer.min.css">
	<!-- 对旧浏览器的支持部分... -->
	<!--[if lt IE 9]>
	      <script src="js/html5shiv.min.js"></script>
	      <script src="js/respond.min.js"></script>
	<![endif]-->
	<style type="text/css">
	
		.layui-btn-normal{
		  		background-color: #06b4bf !important;
		  	}
		
		div.ui-body{
			position: absolute;
		    left: 240px;
		    right: 0;
		    top: 0;
		    bottom: 0;
		    /* z-index: 998; */
		    width: auto;
		    overflow: hidden;
		    overflow-y: auto;
		    box-sizing: border-box;
		}
		
		h4.modal-title{
			margin:10px 0 0 0;
		}
		
		#itemAll{
		    width: 18px;
   			height: 18px;
   			border: 1px solid #d2d2d2;
		    font-size: 12px;
		    border-radius: 2px;
		    background-color: #fff;
		    margin: 8px 0 0 5px;
		}
		.items{
			width: 18px;
   			height: 18px;
   			border: 1px solid #d2d2d2;
		    font-size: 12px;
		    border-radius: 2px;
		    background-color: #fff;
		    margin: 8px 0 0 5px;
		}
		
		.btn-xs{
			font-size: 14px;
			padding: 1px 0px;
			color: #666;
			
		}
		
		.glyphicon-folder-close{
			color: #06b4bf;
		}
		.glyphicon-remove{
			color: #06b4bf;
		}
		
		h4{
			font-size: 14px;
		}
		
		.modal-content {

		    border: 0px;
		    /* border: 1px solid rgba(0, 0, 0, 0.2); */
		    border-radius: 6px;
		}
		
		body{
			color: #666;
		}
		
		.navbar-btn{
    		/* margin-bottom: 8px; */
    		margin: 10px 8px 0 0;
		}
		
		.navbar-btn li a{
			padding: 0px;
			line-height: 10px;
		}
		  
	</style>
</head>
<body>
<!-- 左侧 -->
<div class="layui-side layui-bg-black" style="width: 240px;border-right: 1px solid #ddd;">
  <div class="layui-side-scroll" style="background: #fff;width: 260px">
    <div id="testTree" class="tree-hospital" style="width: 240px;margin-top: 15px;"></div>
  </div>
</div>
<!-- 右侧 -->
<div class="layui-body" style="background:#f5f5f5;left: 240px;min-width: 1000px ">
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
  <!-- 上传文件框 -->
	<div class="modal " id="uploadFileModal" tabindex="-1"
		role="dialog" aria-labelledby="uploadFileMolderTitle">
		<div class="modal-dialog " role="document">
			<div class="modal-content">
				<!-- <div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="uploadFileMolderTitle">
						<span class="glyphicon glyphicon-cloud-upload"></span> 上传文件
					</h4>
				</div> -->
				<div class="modal-body">
					<h5>
						选择文件：<span id="selectcount"></span>
					</h5>
					<input type="text" id="filepath" class="form-control"
						onclick="checkpath()" onfocus="this.blur()"
						placeholder="请点击选择要上传的文件……"> <input type="file"
						id="uploadfile" style="display: none;" onchange="getInputUpload()"
						multiple="multiple"> <br />
					<h5>
						上传进度：<span id="filecount"></span>
					</h5>
					<div class="progress">
						<div id="pros" class="progress-bar" role="progressbar"
							aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"
							style="width: 0%;">
							<span class="sr-only"></span>
						</div>
					</div>
					<h5>上传状态：</h5>
					<div class="panel panel-default">
						<div class="panel-body">
							<div id="uploadstatus" class="uploadstatusbox"></div>
						</div>
					</div>
					<div id="uploadFileAlert" role="alert"></div>
					<div id="selectFileUpLoadModelAlert" class="alert alert-danger"
						role="alert">
						<h4>提示：存在同名文件！</h4>
						<p>
							您要上传的文件“<span id="repeFileName"></span>”已存在于该路径下，您希望：
						</p>
						<p>
							<input id="selectFileUpLoadModelAsAll" type="checkbox">
							全部应用
						</p>
						<p>
							<button id="uploadcoverbtn" type="button"
								class="btn btn-danger btn-sm"
								onclick="selectFileUpLoadModelEnd('cover')">覆盖</button>
							<button type="button" class="btn btn-default btn-sm"
								onclick="selectFileUpLoadModelEnd('skip')">跳过</button>
							<button type="button" class="btn btn-default btn-sm"
								onclick="selectFileUpLoadModelEnd('both')">保留两者</button>
						</p>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"
						onclick='abortUpload()'>取消</button>
					<button id="umbutton" type='button' class='btn btn-primary'
						onclick='checkUploadFile()' style="border-color: #06b4bf!important;background-color: #06b4bf!important;">开始上传</button>
				</div>
			</div>
		</div>
	</div>
	<!-- end 上传文件 -->
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
<!-- bootstrap基本框架 -->
<script type="text/javascript" src="js/bootstrap.js"></script>
<!-- 加密插件 -->
<script type="text/javascript" src="js/jsencrypt.min.js"></script>
<!-- 图片查看器 -->
<script type="text/javascript" src="js/viewer.min.js"></script>
<script type="text/javascript" src="js/jquery-viewer.min.js"></script>
<!-- 音乐播放器 -->
<script type="text/javascript" src="js/APlayer.min.js"></script>
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<!-- 页面操作定义 -->
<script type="text/javascript" src="js/home.js"></script>
<script>
var userId = '${user.id}';
var userAccount = '${user.user}';
var userName = '${user.name}';
var rootFolderId = '${rootFolder.folderId}';
//单位字节
var maxUploadSize = parseInt('${maxUploadSize}');
//单位M
var maxSizeStr = bytesToSize(maxUploadSize);
//云盘大小 单位字节
var diskSize = parseInt('${diskSize}');

var diskSizeStr = bytesToSize(diskSize);

function reloadTree(){
	$.get("/teleconsult/homeController/getFolderTreeJson.ajax",
		function(result) {
			$('#testTree').treeview({
				color : "#666",
				expandIcon : 'glyphicon glyphicon-chevron-right',
				collapseIcon : 'glyphicon glyphicon-chevron-down',
				nodeIcon : "glyphicon glyphicon-folder-close",
				selectedBackColor : "#d4f9fb",
				showBorder : false,
				selectedColor : "#666",
				showTags : true,
				data : result,
				onNodeSelected : function(event, data) {
					console.log(data);
					showFolderView(data.id);
					/* me.checkTreeDoubleClick(event, data); */
				},
				onNodeUnselected : function(event, data) {
					/* me.checkTreeDoubleClick(event, data); */
				}
			});
		});
}

reloadTree();
  
var applystatus={"1":"待审核","2":"待主持","3":"正在会诊","4":"已结束","5":"已取消","6":"审核拒绝"};
var attendstatus={"1":"待审核","2":"待参加","3":"正在会诊","4":"已结束","5":"已取消","6":"审核拒绝"}
var meetingaction=[{"adminbutton":"<a class=\"layui-btn layui-btn-xs layui-btn-radius tc-bg-main\" lay-event=\"check\">同意</a>\n    <a class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" lay-event=\"reject\">拒绝</a>","adminname":"待审核","applybutton":"<a class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" lay-event=\"cancle\">取消</a>","applyname":"待审核","attendbutton":"","attendname":"待审核","id":1},{"adminbutton":"","adminname":"审核通过","applybutton":"<a class=\"layui-btn layui-btn-normal layui-btn-xs layui-btn-radius\" lay-event=\"start\">发起</a>\n    <a class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" lay-event=\"cancle\">取消</a>","applyname":"待主持","attendbutton":"<a class=\"layui-btn layui-btn-xs layui-btn-radius tc-bg-main\" lay-event=\"check\">加入</a>","attendname":"待参加","id":2},{"adminbutton":"<a class=\"layui-btn layui-btn-xs layui-btn-danger layui-btn-radius\" lay-event=\"stop\">中断</a>","adminname":"正在会诊","applybutton":"<a class=\"layui-btn layui-btn-normal layui-btn-xs layui-btn-radius\" lay-event=\"entry\">进入</a>","applyname":"正在会诊","attendbutton":"<a class=\"layui-btn layui-btn-normal layui-btn-xs layui-btn-radius\" lay-event=\"entry\">进入</a>","attendname":"正在会诊","id":3},{"adminbutton":"","adminname":"已结束","applybutton":"","applyname":"已结束","attendbutton":"","attendname":"已结束","id":4},{"adminbutton":"","adminname":"已取消","applybutton":"","applyname":"已取消","attendbutton":"","attendname":"已取消","id":5},{"adminbutton":"","adminname":"审核拒绝","applybutton":"<a class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" lay-event=\"recheck\">重新申请</a>","applyname":"审核拒绝","attendbutton":"","attendname":"审核拒绝","id":6}];
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
        parent.window.location.href = url;
    }



  //表格
  var table = layui.table;
  table.render({
    elem: '#layTable'
    ,url:'homeController/getFolderView.ajax'
    ,toolbar: '#toolbar'
    ,where : {'fid':rootFolderId}
  	,response:{
  		dataName: 'folderList' //规定数据列表的字段名称，默认：data
  		
  	}
    ,defaultToolbar:[]
    ,height: 'full-40'
    ,cols: [[
      {type: 'checkbox',}
      ,{field:'folderName', title:'文件名', width:150, edit : false}
      ,{field:'folderCreationDate', title:'创建日期', width:100, sort: true }
      ,{field:'folderSize', title:'大小', width:80,  sort: true,templet:function(d){
    	  if(d.folderSize == 0 || d.folderSize == '0'){
    		  return '--'
    	  }else{
    		  return bytesToSize(b);
    	  }
      }}
      ,{field:'folderCreator', title:'创建者'}
      ,{field:'status', title:'动作',width:214,templet:function(d){
    	  return "还没有";
      }}
    ]] ,
	id: 'meeting',
    page: false
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
              type: 1, 
              content: $('.modal-content').html(),
              title:'上传文件',
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
        var url ="Meeting"+"?meetingId="+data.id+"&userid="+parent.window.userID+"&currentUser="+parent.window.user;
        showVideoRateSelect(url)
    }else if(obj.event === 'check'){
    	$.get("currentMeeting",{meetingId:data.id},function(result){
            var isPresenter = result.isPresenter;
            var meetingStatus = result.meetingInfo.status;
            //需要主持人先开房间
            if( (isPresenter== true) || (isPresenter== false && meetingStatus == 3)){
                var url ="Meeting"+"?meetingId="+data.id+"&userid="+result['currentUser']['id']+"&currentUser="+result['currentUser']['user'];
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
</script>
<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>