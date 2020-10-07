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
        <div class="layui-row" style="margin-top:10px">
          <div class="tc-form-item layui-col-xs7">
            <div id="v_container" style="height: 42px;margin:0 auto;padding-top: 8px;"></div>
          </div>
          <div class="tc-form-item layui-col-xs5">
            <input type="text" id="code_input" name="code" required style="margin-top:10px;margin-left: 16px" lay-verify="required" autocomplete="off" placeholder="图片验证码" class="layui-input">
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
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/utils/md5.js"></script>
  <script src="js/utils/jsencrypt.js"></script>
  <script src="js/gVerify.js"></script>
<script>
//Demo、
var $;
layui.use(['form'], function(){
  var form = layui.form;
  var verifyCode
  $=layui.$;
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
  //监听提交
  form.on('submit(layForm)', function(data){
    // var res = verifyCode.validate(document.getElementById("code_input").value);

        data=data.field;
        //密码MD5加密
        var md5Pwd = hex_md5(data.password);
        data.password = md5Pwd;
        data.type = 3
        if(!data.remember)data.remember=0;
        var code = $("input[name='code']").val();
        //使用公钥加密
        var encrypt = new JSEncrypt();
        encrypt.setPublicKey('-----BEGIN RSA PRIVATE KEY-----MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPnqHRQND1MUPqXK4w1O9alx9HY5CN48Bd2FIs8wxAnZ5v4UBgLL2PNeOToIwmufhfi0rzFjrDYnUfXnx7jcScF46fCVGyn8xB5JI8zlJ1K7Nm/xUy8KtsI+tOOh6m96Q0WI5Yak/6eq29x9kMLPbJD5QrGc+i+LBqTskeN/7XDQIDAQAB-----END RSA PRIVATE KEY-----');
        data = encodeURI(encrypt.encrypt(JSON.stringify(data)));
        console.log('加密后数据:%o', data);
        var username = $("input[name='user']").val();
        $.post("LoginPost",data,function(result){
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
          if(result.resType !== '3'){
              document.getElementById("code_input").value = ""
              if(result.logintype == 1){
            	  layer.msg("第一次登录需要修改密码")
                  layer.open({
                      type: 2,
                      content: '/teleconsult/toupdatepssword?username='+result.user,
                      title:'修改密码',
                      area: ['500px', '280px'],
                      btn:['确定','取消'], 
                      yes:function(index,layero){ 
                        var body = layer.getChildFrame('body', index);
                        $(body).find(".layui-layer-inputbtn0").click();
                      }
                  });
                  return;
              }
              if(result.status==true && result.resType === "0"){
                  // setCookie("currentUser",result.user);
                  // setCookie("password",result.password);
                  location.href="toDefault?currentUser="+result.user;

              }else if(result.resType === "1"){
                  layer.msg('后台用户不能登陆前台系统，用户名不存在！', {icon: 7});
              }else{
            	  layer.msg("用户名或密码错误",{time:3000});
            		setTimeout(toLogin,4000);
                  
              }
          }else{
            layer.msg("验证码错误！",{time:3000});
            // verifyCode.refresh()
          }
        },"json");

  });

  /* rediric = function(){

    	alert("用户名或密码错误");

	  location.href="/teleconsult/toLogin";

  } */
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
