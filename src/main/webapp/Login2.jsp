<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
</head>
<body class="login-body">
  <div class="login-vessel">
    <div class="login-big-title">欢迎使用${titleName}远程会诊系统</div>
    <div class="login-box">
      <div class="login-head">账号密码登录</div>
      <form class="layui-form layui-form-pane login-form" action="">
        <div class="layui-form-item">
          <label class="layui-form-label"><i class="layui-icon layui-icon-username"></i></label>
          <div class="layui-input-block">
            <input type="text" name="user" id="user" required lay-verify="required" autocomplete="off" placeholder="用户名" value="${cookies.lastloginuser}" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label"><i class="layui-icon layui-icon-password"></i></label>
          <div class="layui-input-block">
            <input type="password" name="password" id="password" required lay-verify="required" value="${cookies.lastloginpassword}" placeholder="密码" autocomplete="off" class="layui-input">
          </div>
        </div>

        <div class="layui-form-item">
          <label class="layui-form-label"><i class="layui-icon layui-icon-theme"></i></label>
          <div class="layui-input-block">
            <input type="text" name="code" required lay-verify="required"  placeholder="验证码" class="layui-input" autocomplete="off" style="width:75px;float:left">
<%--            <input type="text" name="codes"  autocomplete="off" class="layui-input" disabled="disabled" style="width:65px;float: left">--%>
<%--            <a class="layui-btn tc-bg-main layui-btn-radius" href="javascript:random()" style="width:100px;float: left">获取验证码</a>--%>
              <div id="v_container" style="height: 50px;margin:0 auto;float:right"></div>
          </div>

        </div>

        <div>
<%--          <input type="checkbox" checked="checked" name="remember" id="remember" value="1" title="记住我" lay-skin="primary" class="check-box">--%>
            <a class="right-link" href="PasswordReset">忘记密码</a>
        </div>
        <div class="tc-form-item">
          <a class="layui-btn layui-btn-fluid tc-bg-main layui-btn-radius layui-btn-lg" style="margin-top: 15px;" lay-submit lay-filter="layForm">立即登录</a>
        </div>
      </form>
    </div>
  </div>
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->\
<script src="js/utils/md5.js"></script>
  <script src="js/utils/jsencrypt.js"></script>
  <script src="js/gVerify.js"></script>
<script>
//Demo
var $
layui.use(['form'], function(){
  var form = layui.form;
  $=layui.$;
    var verifyCode;
  if(self != top) {
	  top.location.href=self.location.href;
  }
    $.ajax({

        url:"/teleconsult/CodeTest",
        type:"post",
        success:function(data){
            var decryptor = new JSEncrypt();  // 新建JSEncrypt对象
            decryptor.setPrivateKey("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM+eodFA0PUxQ+pcrjDU71qXH0djkI3jwF3YUizzDECdnm/hQGAsvY8145OgjCa5+F+LSvMWOsNidR9efHuNxJwXjp8JUbKfzEHkkjzOUnUrs2b/FTLwq2wj6046Hqb3pDRYjlhqT/p6rb3H2Qws9skPlCsZz6L4sGpOyR43/tcNAgMBAAECgYAVKk3CwEHNDSWoSaR0U/DGomtM4siEvngI2RmffXN9TtQtAaCHbWgxpkO58/71n7XDP7b3SwfYjTv1Y7N7APvO4syVG+dN6vKigEs/Fv6ijvu6ISmDhbjvlp2EIUSHn8m84Uuc7wvksV/mW4YVGXUox7QfKfMm0HaK4JdsIaPSXQJBAO3vJ0FIOVvTWeEgne+7dlfAsCVY52SekegO5KB0fo0zhR0f1TwsRgXoRJtTip+9QDjCWYDL/wFC2wuViTOoIYsCQQDfYj1nsNBGcRdqQnmnYnqlkqXNrdyIKZ36OAooy0S4ZxRZ4BO6Mm3JxoSnKMq3tYVvIsn4Qn2MeEaH0R3O9szHAkEA2Z015ftMmrN/LOxMBwsJfdD6Se46FEkDYZ7dc/OYG0TXpn+K43IKyTRaK0YJL3hD2KXIfogVPu4KsVmfFuPbaQJBAK6JTb0k07cWSdtGkVMNiRKxYEcyXyssiTimbJmvKMSEFcybXg6PtGSBbchGAQ5FEDrjjbciDIKiv0kDRS0efKUCQEanoMDkYmyHT1LmGiCBcLagma3le6e6Sqwdt9PSkjSzYWhIUjR29gwVPP3K4f9fHuM0ipYCaL2u1TPVAl7ImEw="); // 设置私钥
            verifyCode = new GVerify("v_container",decryptor.decrypt(data));
        }
    })

  $("#refreshCode").click(function(){
		$(this).prop("src","<%=request.getContextPath() %>/CodeTest?"+new Date().getTime());
	})

  //监听提交
  form.on('submit(layForm)', function(data){
      data=data.field;
      data.type = 3
      if(!data.remember)data.remember=0;
      var code = $("input[name='code']").val();
      var username = $("input[name='user']").val();
      var password = $("input[name='password']").val();
      //密码MD5加密
      password =  hex_md5(password);
      data.password = password;
      var encrypt = new JSEncrypt();
      encrypt.setPublicKey('-----BEGIN RSA PRIVATE KEY-----\n' +
          'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPnqHRQND1MUPqXK4w1O9alx9HY5CN48Bd2FIs8wxAnZ5v4UBgLL2PNeOToIwmufhfi0rzFjrDYnUfXnx7jcScF46fCVGyn8xB5JI8zlJ1K7Nm/xUy8KtsI+tOOh6m96Q0WI5Yak/6eq29x9kMLPbJD5QrGc+i+LBqTskeN/7XDQIDAQAB'+
          '-----END RSA PRIVATE KEY-----');
      var rsa = encodeURI(encrypt.encrypt(JSON.stringify(data)));
      console.log("加密："+rsa);
		    	  $.ajax({
	    			  url:"Default",
	    			  type:"post",
                      data:rsa,
                      success:function(da){
                          $.ajax({

                              url:"/teleconsult/CodeTest",
                              type:"post",
                              success:function(data){
                                  var decryptor = new JSEncrypt();  // 新建JSEncrypt对象
                                  decryptor.setPrivateKey("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM+eodFA0PUxQ+pcrjDU71qXH0djkI3jwF3YUizzDECdnm/hQGAsvY8145OgjCa5+F+LSvMWOsNidR9efHuNxJwXjp8JUbKfzEHkkjzOUnUrs2b/FTLwq2wj6046Hqb3pDRYjlhqT/p6rb3H2Qws9skPlCsZz6L4sGpOyR43/tcNAgMBAAECgYAVKk3CwEHNDSWoSaR0U/DGomtM4siEvngI2RmffXN9TtQtAaCHbWgxpkO58/71n7XDP7b3SwfYjTv1Y7N7APvO4syVG+dN6vKigEs/Fv6ijvu6ISmDhbjvlp2EIUSHn8m84Uuc7wvksV/mW4YVGXUox7QfKfMm0HaK4JdsIaPSXQJBAO3vJ0FIOVvTWeEgne+7dlfAsCVY52SekegO5KB0fo0zhR0f1TwsRgXoRJtTip+9QDjCWYDL/wFC2wuViTOoIYsCQQDfYj1nsNBGcRdqQnmnYnqlkqXNrdyIKZ36OAooy0S4ZxRZ4BO6Mm3JxoSnKMq3tYVvIsn4Qn2MeEaH0R3O9szHAkEA2Z015ftMmrN/LOxMBwsJfdD6Se46FEkDYZ7dc/OYG0TXpn+K43IKyTRaK0YJL3hD2KXIfogVPu4KsVmfFuPbaQJBAK6JTb0k07cWSdtGkVMNiRKxYEcyXyssiTimbJmvKMSEFcybXg6PtGSBbchGAQ5FEDrjjbciDIKiv0kDRS0efKUCQEanoMDkYmyHT1LmGiCBcLagma3le6e6Sqwdt9PSkjSzYWhIUjR29gwVPP3K4f9fHuM0ipYCaL2u1TPVAl7ImEw="); // 设置私钥
                                  // var verifyCode = new GVerify("v_container",decryptor.decrypt(data));
                                  verifyCode.refresh(decryptor.decrypt(data))
                              }
                          })
	    				  if(da == "2"){
	    					  layer.msg('验证码错误！',{icon:7,time:2000})
	    					  return;
	    				  }
	    				  if(da == "1"){
	    					  alert("用户名错误");
	    					  return;
	    				  }
	    				  if(da == "3"){
	    					  layer.msg('该用户被锁定,10分钟后重试！',{icon:7,time:2000})
	    					  return;
	    				  }
	    				  if(da == "4"){
	    					  layer.msg('密码错误！',{icon:7,time:2000})
	    					  return;
	    				  }
	    				  if(da == "5"){
	    					  $.post("LoginPost",rsa,function(result){
	    			    	    	if(result.status==true){//==false我同时也写个方法穿个值给后台
	    			    	    		setCookie("currentUser",result.user);
	    			    	    		// setCookie("password",result.password);
	    			    	    		location.href="toDefault?currentUser="+result.user;

	    			    	    	}else{
	    			    	    		alert("-200");
	    			    	    	}
	    			    	    },"json");
	    				  }
	    			  }
	    		  })
		  })

})
</script>

<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>
