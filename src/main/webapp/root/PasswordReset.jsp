<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="../layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="../css/common.css"><!-- 自定义css-->
</head>
<body class="login-body">
  <div class="login-vessel">
    <div class="login-big-title">欢迎使用${titleName}远程会诊系统</div>
    <div class="login-box">
      <div class="login-head">密码找回</div>
      <form class="layui-form login-form reset-form" action="">
        <div class="layui-form-item">
          <input type="text" name="email" id="email" required lay-verify="required" autocomplete="off" placeholder="邮箱" class="layui-input">
        </div>
          <div class="layui-row" style="margin-top:10px">
              <div class="tc-form-item layui-col-xs7">
                  <div id="v_container" style=" height: 50px;margin:0 auto;margin-top:10px;"></div>
              </div>
              <div class="tc-form-item layui-col-xs5">
                  <input type="text" id="code_input" name="ImgCode" required style="margin-top:10px;margin-left: 16px" lay-verify="required" autocomplete="off" placeholder="图片验证码" class="layui-input">
              </div>
          </div>
        <div class="layui-row">
          <div class="tc-form-item layui-col-xs7">
              <input type="text" name="code" required lay-verify="required" placeholder="验证码" autocomplete="off" class="layui-input">
          </div>
          <div class="tc-form-item layui-col-xs5">
            <input type="button" id="getCodeBtn" class="layui-btn layui-btn-normal layui-btn-radius" style="margin-top: 4px;margin-left: 16px" oldvalue="获取验证码" value="获取验证码">
          </div>
        </div>
        <div class="tc-form-item" style="padding-top: 16px">
          <button class="layui-btn layui-btn-fluid tc-bg-main layui-btn-radius layui-btn-lg" lay-submit lay-filter="layForm">确 定</button>
        </div>
        <div class="tc-form-item" style="padding-top: 10px">
          <a class="right-link" href="Login">账号密码登录</a>
        </div>
      </form>
    </div>
  </div>
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
  <script src="../js/utils/jsencrypt.js"></script>
  <script src="../js/gVerify.js"></script>
<script>
//Demo
var $;
var difftime=60;

layui.use(['form'], function(){
  var form = layui.form;
  var verifyCode;
  $=layui.$;
  //监听提交
  //   var verifyCode = new GVerify("v_container");
    $.ajax({

        url:"/teleconsult/CodeTest",
        type:"post",
        success:function(data){
            var decryptor = new JSEncrypt();  // 新建JSEncrypt对象
            decryptor.setPrivateKey("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM+eodFA0PUxQ+pcrjDU71qXH0djkI3jwF3YUizzDECdnm/hQGAsvY8145OgjCa5+F+LSvMWOsNidR9efHuNxJwXjp8JUbKfzEHkkjzOUnUrs2b/FTLwq2wj6046Hqb3pDRYjlhqT/p6rb3H2Qws9skPlCsZz6L4sGpOyR43/tcNAgMBAAECgYAVKk3CwEHNDSWoSaR0U/DGomtM4siEvngI2RmffXN9TtQtAaCHbWgxpkO58/71n7XDP7b3SwfYjTv1Y7N7APvO4syVG+dN6vKigEs/Fv6ijvu6ISmDhbjvlp2EIUSHn8m84Uuc7wvksV/mW4YVGXUox7QfKfMm0HaK4JdsIaPSXQJBAO3vJ0FIOVvTWeEgne+7dlfAsCVY52SekegO5KB0fo0zhR0f1TwsRgXoRJtTip+9QDjCWYDL/wFC2wuViTOoIYsCQQDfYj1nsNBGcRdqQnmnYnqlkqXNrdyIKZ36OAooy0S4ZxRZ4BO6Mm3JxoSnKMq3tYVvIsn4Qn2MeEaH0R3O9szHAkEA2Z015ftMmrN/LOxMBwsJfdD6Se46FEkDYZ7dc/OYG0TXpn+K43IKyTRaK0YJL3hD2KXIfogVPu4KsVmfFuPbaQJBAK6JTb0k07cWSdtGkVMNiRKxYEcyXyssiTimbJmvKMSEFcybXg6PtGSBbchGAQ5FEDrjjbciDIKiv0kDRS0efKUCQEanoMDkYmyHT1LmGiCBcLagma3le6e6Sqwdt9PSkjSzYWhIUjR29gwVPP3K4f9fHuM0ipYCaL2u1TPVAl7ImEw="); // 设置私钥
          verifyCode = new GVerify("v_container",decryptor.decrypt(data));
        }
    })
  $('#getCodeBtn').on('click',function(e){
      // var res = verifyCode.validate(document.getElementById("code_input").value);
      // if(res) {
          var sendtime=parseInt(getCookie("sendtime"));
          var time=Date.parse(new Date())/1000;
          if(sendtime>=time-difftime){
              layer.msg("发送邮件的时间过于频繁！");
              return;
          }

          // $.get("../sendEailCode?email="+$("#email").val(),function(data){
    $.get("../sendEailCode?email="+$("#email").val()+"&ImgCode="+$("#code_input").val(),function(data){
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
            if(data.code !== "3"){
              if(data.code==1){
                setCookie("sendtime",time);
                layer.msg("已经将验证码发送到你的邮箱，请查看！");
              }else
              if(data.code==2){
                layer.msg("每天只能发送10次验证码，您已达到限制！");
              }
              else{
                layer.msg("邮箱输入错误，发送失败！");
              }
            }else{
              layer.msg("验证码错误！");
              // verifyCode.refresh()
            }

          },"json");
      // }
  });

  form.on('submit(layForm)', function(data){
    $.get("../resetPassword",data.field,function(result){
    	layer.msg(result.data,{icon:1},function(){
    		if(result.code==1){
        		self.location.href="Login";
        	}
    	});
    },"json");
    return false;
  });

  setInterval(updatetime,1000);
});

function updatetime(){
	var sendtime=parseInt(getCookie("sendtime"));
	var time=Date.parse(new Date())/1000;
	var leavetime=sendtime+difftime-time;

	var oldvalue=$("#getCodeBtn").attr("oldvalue");
	if(leavetime>0){
		$("#getCodeBtn").val(oldvalue+"("+leavetime+")");
	}
	else{
		$("#getCodeBtn").val(oldvalue);
	}
}

function setCookie(c_name,value,expiredays){
	var exdate=new Date()
	exdate.setDate(exdate.getDate()+expiredays)
	document.cookie=c_name+ "=" +escape(value)+
	((expiredays==null) ? "" : ";expires="+exdate.toGMTString())
}

function getCookie(c_name){
  if (document.cookie.length>0){
  c_start=document.cookie.indexOf(c_name + "=")
  if (c_start!=-1)
    {
    c_start=c_start + c_name.length+1
    c_end=document.cookie.indexOf(";",c_start)
    if (c_end==-1) c_end=document.cookie.length
    return unescape(document.cookie.substring(c_start,c_end))
    }
  }
  return "";
}
</script>

<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>
