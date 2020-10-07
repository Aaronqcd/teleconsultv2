<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <title></title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
</head>
<body>

<form class="layui-form tc-form-p20" action="">
<input type="hidden" name="username" value="${username}">
  <div class="layui-row">
    <div class="tc-form-item layui-col-xs11">
      <label class="layui-form-label">旧密码</label>
      <div class="layui-input-block">
        <input type="password" name="oldpassword" required placeholder="请输入旧密码" autocomplete="off" class="layui-input">
      </div>
    </div>
  </div>
  <div class="layui-row">
    <div class="tc-form-item layui-col-xs11">
      <label class="layui-form-label">新密码</label>
      <div class="layui-input-block">
        <input type="password" name="password" id="password" required lay-verify="pass" placeholder="请输入新密码" autocomplete="off" class="layui-input">
      </div>
    </div>
  </div>
    <div class="layui-row">
    <div class="tc-form-item layui-col-xs11">
      <label class="layui-form-label">确认密码</label>
      <div class="layui-input-block">
        <input type="password" name="repassword" id="repassword" required  placeholder="请再次输入密码" autocomplete="off" class="layui-input">
      </div>
    </div>
  </div>
  <div class="layui-form-item" style="display: none">
        <div class="layui-input-block">
        <input class="layui-layer-inputbtn0" type="button" value="立即提交" onclick="formDemo()" style="background-color: #06b4bf;border: none;color: white; padding: 15px 32px;text-align: center;text-decoration: none;display: inline-block;font-size: 16px;border-radius: 8px;width:180px;height:50px"/>
        <!-- button class="layui-btn layui-btn-normal" lay-submit lay-filter="layForm" onclick="formDemo()">提交</button> -->
    </div>
</form>
 
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script>
//Demo
layui.use(['form'], function(){
  var form = layui.form;
  var $=layui.$;
  //监听提交
  formDemo =function(){
	  var oldpassword = $("input[name='oldpassword']").val();
	  var password = $("input[name='password']").val();
	  var username = $("input[name='username']").val();
	  var repassword = $("input[name='repassword']").val();
	  if(repassword==password){
		  $.ajax({ //前面这个路径是什么 项目名 之前不是都没加？
		    	url:"/teleconsult/PasswordFormPosts",
		    	type:"post",
		    	data:{oldpassword:oldpassword,password:password,username:username},
		    	success:function(datas){
		    		if(datas == "1"){
		    			top.layer.msg("修改成功",{time:3000})
		    			setTimeout(toLogin,5000);
		    		}
		    		if(datas == "3"){
		    			top.layer.msg('旧密码错误',{icon:7,time:2000})
		    		}
		    		if(datas == "2"){
		    			top.layer.msg("密码长度错误，密码必须8到16位且必须包含大小写字母，数字，特殊符号，或至少包含其中的三种")
		    			//alert("密码长度错误，密码必须8到16位")
		    		}
		    		if(datas =="4"){
		    			top.layer.msg("密码长度错误，密码必须8到16位且必须包含大小写字母，数字，特殊符号，或至少包含其中的三种")
		    		}
		    	}
		  }) 
	  }else{
		  top.layer.msg('两次密码不一致')
	  }

	  
  }
  
  
  function toLogin() {
	  location.href="/teleconsult/Login";
  }
});
</script>

<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>