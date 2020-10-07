<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="com.alibaba.fastjson.JSON" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="renderer" content="webkit">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
</head>
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/jquery-3.min.js"></script><!-- layui.js -->
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
  <div class="layui-header">
  	<input id="basePath" type="hidden" value=<%= request.getServerName()+":"+request.getServerPort()+request.getContextPath() %> >
	<input id="currentUser" type="hidden"/>
	<input id="currentUserId" type="hidden" value="${currentUserId}"/>
    <div class="layui-logo" style="padding-left: 10px;"><img src="headimg/${logoUrl}" onerror="javascript:this.src='images/logo.png'" style="margin-right: 0px;" ></div>
    <!-- 头部区域（可配合layui已有的水平导航） -->
    <ul class="layui-nav layui-layout-left" id="bodyUl" style="padding: 0;">
      <li class="layui-nav-item layui-this" onclick="sessionStorage.setItem('currentPage_'+currentUser,$(this).children('a').attr('href'));"><a href="MeetingManage" target="mainIframe"><div class="top-nav-icon"><i class="layui-icon layui-icon-template-1"></i></div>会诊中心</a></li>
      <li class="layui-nav-item" onclick="sessionStorage.setItem('currentPage_'+currentUser,$(this).children('a').attr('href'));"><a href="ResourceManage" target="mainIframe"><div class="top-nav-icon"><i class="layui-icon layui-icon-app"></i></div>资源管理</a></li>
      <li class="layui-nav-item" onclick="sessionStorage.setItem('currentPage_'+currentUser,$(this).children('a').attr('href'));"><a href="MeetingDiskManage" target="mainIframe"><div class="top-nav-icon"><i class="layui-icon layui-icon-component"></i></div>会诊云盘</a></li>
      <li class="layui-nav-item" onclick="sessionStorage.setItem('currentPage_'+currentUser,$(this).children('a').attr('href'));"><a href="RecordVideoManage" target="mainIframe"><div class="top-nav-icon"><i class="layui-icon layui-icon-video"></i></div>会诊视频</a></li>
      <li class="layui-nav-item" onclick="sessionStorage.setItem('currentPage_'+currentUser,$(this).children('a').attr('href'));"><a href="Personal" target="mainIframe"><div class="top-nav-icon"><i class="layui-icon layui-icon-username"></i></div>个人中心</a></li>
    </ul>
    <ul class="layui-nav layui-layout-right" style="padding: 0;">
      <li class="layui-nav-item">
        <a href="javascript:;" id="userInfo" style="padding-right: 15px; padding-left: 5px;">
          <img src="webapp/images/headimg/{user.avatar}" onerror="this.src='http://t.cn/RCzsdCq'" class="layui-nav-img">
         <label id="username"></label>
        </a>
        <dl class="layui-nav-child">
          <dd><a id="editPassword" href="javascript:;">修改密码</a></dd>
        </dl>
      </li>
      <li class="layui-nav-item"><a id="LogoutA" href="Logout" style="padding: 0px 10px;">退出</a></li>
      <li class="layui-nav-item"><a href="help" target="_blank" style="padding: 0px 15px 0px 0px;">帮助</a></li>
    </ul>
  </div>
  <div class="layui-body">
    <div class="layadmin-tabsbody-item layui-show">
      <iframe id="iframe" src="MeetingManage" name="mainIframe" frameborder="0" class="layadmin-iframe"></iframe>
    </div>
  </div>
</div>
<script>
document.getElementById('currentUser').value=getUrlParam("currentUser");
var users = '<%=JSON.toJSON(session.getAttribute("userInfo")).toString()%>';
users = JSON.parse(users);
var currentUser = getUrlParam("currentUser");
var currentUserId = document.getElementById('currentUserId').value;
for(var i=0;i<users.length;i++){
    if(users[i]["user"] == currentUser){
        window.userID = users[i].id;
        break;
    };
}

var currentUrl = (sessionStorage.getItem("currentPage_"+currentUser)?sessionStorage.getItem("currentPage_"+currentUser):'MeetingManage');
document.getElementById('iframe').src = document.location.origin+'<%=this.getServletContext().getContextPath()%>'+"/"+currentUrl;
$("#bodyUl li").each(function(){
    $(this).removeClass('layui-this');
	var url = $(this).children('a').attr('href');
	if(url==currentUrl){
		$(this).addClass('layui-this');
	}
});
$("#LogoutA").click(function(){
	sessionStorage.removeItem("currentPage_"+currentUser);
});

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
  		$("#userInfo").find("img").attr("src","headimg/"+users[i]["avatar"]);
  		$("#username").html(users[i]["user"]);
  		isLogin = true;
  		break;
  	};
  }
  if(!isLogin){
	  location.href="Login";
  }
  var interval;
	interval = window.setInterval(function(){
  	$.post("isLastLoginFlag",{currentUser:currentUser},function(result){
      	if(result.code==2){
      		clearInterval(interval);
      		layer.alert("已在其它地方登录",function(){
          		$("#LogoutA").click();
          		window.location.href='Logout';
      		});
      	}
      },"json");
  },10000);
});
var basePath = $("#basePath").val();
var websocket;
var protocol = window.location.protocol;
// 首先判断是否 支持 WebSocket
if('WebSocket' in window) {
	if(protocol==="http:"){
	     websocket = new WebSocket("ws://"+basePath+"/webSocket");
	}else{
	     websocket = new WebSocket("wss://"+basePath+"/webSocket");
	}
} else if('MozWebSocket' in window) {
	if(protocol==="http:"){
	    websocket = new MozWebSocket("ws://"+basePath+"/webSocket");
	}else{
	    websocket = new MozWebSocket("wss://"+basePath+"/webSocket");
   }
} else {
    websocket = new SockJS(protocol+"://"+basePath+"/sockjs/webSocket");
}
// 打开时
websocket.onopen = function(event) {
    console.log("  Default UI websocket.onopen  ");
};
//处理消息时
websocket.onmessage = function(event) {
	var msg = $.parseJSON(event.data);
	if(msg.type === "inviteExpert"){//邀请专家
		//邀请专家的会诊id和主持人信息
		var meetingId = msg.meetingId;
		var fromUser = msg.fromUser; //邀请人名称
		var fromUserId = msg.fromUserId; //邀请人id
		var isAssent = 1; //默认同意请求
		layer.confirm(fromUser+'邀请您参加会诊', 
		{
   		  btn: ['同意', '拒绝'], //可以无限个按钮
		  btn1: function(index, layero){
  			  	//按钮【同意】的回调
  			 	confirmInvite(isAssent, fromUser, fromUserId, meetingId);
  			  	layer.close(index);
  			  }, 
  			  btn2: function(index){
  			  	//按钮【拒绝】的回调
  				isAssent = 0;
  				confirmInvite(isAssent, fromUser, fromUserId, meetingId);
  				layer.close(index);
  			  },
  				//右上角关闭回调
  				cancel: function(){
  			    return false; //开启该代码可禁止点击该按钮关闭
  			  }
		});
	}
};

//专家确认邀请方法
function confirmInvite(isAssent, fromUser, fromUserId, meetingId){
	//发起确认转换请求
	var url = "${pageContext.request.contextPath}/MeetingTools/confirmInvite";
	$.ajax({
        data: {
        	"isAssent":isAssent,
        	"toUser":fromUser,
            "toUserId":fromUserId,
            "meetingId":meetingId
        },
        type: "post",
        dataType: 'json',
        async: false,
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        encoding: "UTF-8",
        url: url,
        success: function (data) {
        	console.log("邀请专家已发送");
        	//如果专家同意
        	if(isAssent === 1){
        		window.location.href = "Meeting"+"?meetingId="+meetingId+"&userid="+currentUserId+"&currentUser="+currentUser;
/*         		layer.alert("专家已进入会诊",
   		    	{
   					closeBtn: 0//没有关闭按钮
   				}); */
            	return;
        	}
        },
        error: function (result) {
            layer.alert("accept error:"+result.responseText);
        },
    });
	
	
}
websocket.onerror = function(event) {
    console.log(" Default UI websocket.onerror  ");
};
websocket.onclose = function(event) {
    console.log(" Default UI websocket.onclose  ");
};

</script>

<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>