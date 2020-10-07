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
  <div class="layui-row">
    <div class="tc-form-item layui-col-xs11">
      <label class="layui-form-label">旧密码</label>
      <div class="layui-input-block">
        <input type="password" name="oldpassword" required lay-verify="required" placeholder="请输入旧密码" autocomplete="off" class="layui-input">
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
        <input type="password" name="repassword" required lay-verify="confirmPwd" placeholder="请再次输入密码" autocomplete="off" class="layui-input">
      </div>
    </div>
  </div>
  <div class="tc-form-item " style="display: none">
    <button class="layui-btn layui-btn-normal" lay-submit lay-filter="layForm">提交</button>
  </div>
</form>
 
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script>
//Demo
layui.use(['form'], function(){
  var form = layui.form;
  var $=layui.$;
  //监听提交
  form.verify({
	  username: function(value, item){ //value：表单的值、item：表单的DOM对象
	    if(!new RegExp("^[a-zA-Z0-9_\u4e00-\u9fa5\\s·]+$").test(value)){
	      return '用户名不能有特殊字符';
	    }
	    if(/(^\_)|(\__)|(\_+$)/.test(value)){
	      return '用户名首尾不能出现下划线\'_\'';
	    }
	    if(/^\d+\d+\d$/.test(value)){
	      return '用户名不能全为数字';
	    }
	  },
	  confirmPwd: function(value, item){
	    if($('#password').val()!=value){
	      return '两次密码不一致';
	    }
	  }
	  ,pass: [
		/((^(?=.*[a-z])(?=.*[A-Z])(?=.*\W)[\da-zA-Z\W]{8,16}$)|(^(?=.*\d)(?=.*[A-Z])(?=.*\W)[\da-zA-Z\W]{8,16}$)|(^(?=.*\d)(?=.*[a-z])(?=.*\W)[\da-zA-Z\W]{8,16}$)|(^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[\da-zA-Z\W]{8,16}$))/
	    ,'密码必须8到16位，且必须包含大小写字母，数字，特殊符号，或至少包含其中的三种'
	   
	  ] 
	});
  
  form.on('submit(layForm)', function(data){
    debugger;
    $.post("PasswordFormPost",data.field,function(result){
    	if(result.code==0){
    		top.layer.msg("修改成功！");
    		setTimeout("top.layer.closeAll();",3000);
    	}
    	else if(result.code==1)top.layer.msg("原始密码不正确！");
    	else top.layer.msg("修改失败！");
    	//top.layer.closeAll();
    },"json");
    return false;
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