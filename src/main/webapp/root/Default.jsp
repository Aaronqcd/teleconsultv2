<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.alibaba.fastjson.JSON" %>
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
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="../js/jquery-3.min.js"></script><!-- layui.js -->
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
  <div class="layui-header">
	<input id="currentUser" type="hidden">
     <div class="layui-logo"><img src="../headimg/${logoUrl}" onerror="javascript:this.src='../images/logo.png'" style="margin-right: 0px;" ></div>
    <!-- 头部区域（可配合layui已有的水平导航） -->
    <ul class="layui-nav layui-layout-left" id="bodyUl" >
      <li class="layui-nav-item" onclick="sessionStorage.setItem('currentPage_'+currentUser,$(this).children('a').attr('href'));"><a href="sysConfManage" target="mainIframe"><div class="top-nav-icon"><i class="layui-icon layui-icon-template-1"></i></div>配置管理</a></li>
      <li class="layui-nav-item" onclick="sessionStorage.setItem('currentPage_'+currentUser,$(this).children('a').attr('href'));"><a href="LogManage" target="mainIframe"><div class="top-nav-icon"><i class="layui-icon layui-icon-date"></i></div>操作日志</a></li>
    </ul>
    <ul class="layui-nav layui-layout-right">
      <li class="layui-nav-item">
        <a href="javascript:;" id="userInfo">
          <img src="../webapp/images/headimg/{user.avatar}" onerror="this.src='http://t.cn/RCzsdCq'" class="layui-nav-img">
          <label id="username"></label>
        </a>
        <dl class="layui-nav-child">
          <dd><a id="editPassword" href="javascript:;">修改密码</a></dd>
        </dl>
      </li>
      <li class="layui-nav-item"><a id="LogoutA" href="Logout">退出</a></li>
    </ul>
  </div>
  
  <div class="layui-body">
    <div class="layadmin-tabsbody-item layui-show">
      <iframe id="iframe" src="UserManage" name="mainIframe" frameborder="0" class="layadmin-iframe"></iframe>
    </div>
  </div>

</div>

<script>

var currentUrl = (sessionStorage.getItem("currentPage_"+currentUser)?sessionStorage.getItem("currentPage_"+getUrlParam("currentUser")):'sysConfManage');
document.getElementById('iframe').src=document.location.origin+'<%=this.getServletContext().getContextPath()%>'+"/root/"+currentUrl;
$("#bodyUl li").each(function(){
    $(this).removeClass('layui-this');
	var url = $(this).children('a').attr('href');
	if(url==currentUrl){
		$(this).addClass('layui-this');
	}
});
$("#LogoutA").click(function(){
	sessionStorage.removeItem("currentPage_"+getUrlParam("currentUser"));
});


document.getElementById('currentUser').value=getUrlParam("currentUser");
layui.use(['layer','element'], function(){
  var $ = layui.jquery
  var layer = layui.layer
  $("#editPassword").click(function(){
    layer.open({
      type: 2, 
      content: 'PasswordForm',
      title:'修改密码',
      area: ['500px', '280px'],
      btn:['确定','取消'], 
      yes:function(index,layero){ 
        var body = layer.getChildFrame('body', index);
        $(body).find("button[lay-submit]").click()
      }
    });
  });
  var users = '<%=JSON.toJSON(session.getAttribute("userInfo")).toString()%>';
  users = JSON.parse(users);
  var currentUser = $("#currentUser").val();
  var isLogin = false;
  for(var i=0;i<users.length;i++){
  	if(users[i]["user"] == currentUser){
  		$("#userInfo").find("img").attr("src","webapp/images/headimg/"+users[i]["avatar"]);
  		$("#username").html(users[i]["user"]);
  		isLogin = true;
  		break;
  	};
  }
  if(!isLogin){
	  location.href="root";
  } 
  var interval;
	interval = window.setInterval(function(){
		$.post("/teleconsult/isLastLoginFlag",{currentUser:currentUser},function(result){
	    	if(result.code==2){
	    		clearInterval(interval);
	    		layer.alert("已在其它地方登录",function(){
	        		$("#LogoutA").click();
	        		window.location.href='Logout';
	    		});
	    	}
	    },"json");
	},1000);
  
});
</script>

<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>