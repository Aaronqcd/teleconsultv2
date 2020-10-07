<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <title></title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
  <style type="text/css">
    form{width: 1000px;margin: 0 auto;background: #fff;padding: 40px;margin-top: 50px;border: 1px solid #ddd}
  </style>
</head>
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<body style="background:#f5f5f5">
<form:form modelAttribute="contentModel" class="layui-form">
  <fieldset class="layui-elem-field layui-field-title">
    <legend>账号信息</legend>
    <div class="layui-field-box">
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">账号</label>
          <div class="layui-input-block">
            <input type="text" disabled="" value="${form.user}" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">账号类型</label>
          <div class="layui-input-block">
            <input type="text" disabled="" value="${form.groupname}" class="layui-input">
          </div>
        </div>
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">上级单位</label>
          <div class="layui-input-block">
            <input type="text" disabled="" value="${form.parentOrgan}" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">所属机构</label>
          <div class="layui-input-block">
            <input type="text" disabled="" value="${form.selfOrgan}" class="layui-input">
          </div>
        </div>
      </div>
    </div>
  </fieldset>
  <fieldset class="layui-elem-field layui-field-title">
    <legend>个人信息</legend>
    <div class="layui-field-box">
      <%--<div class="layui-row">--%>
        <%--<div class="tc-form-item layui-col-sm6">--%>
          <%--<div class="layui-row">--%>
            <%--<div class="tc-form-item layui-col-sm12">--%>
              <%--<label class="layui-form-label">姓名</label>--%>
              <%--<div class="layui-input-block">--%>
                <%--<input type="text" name="name" value="${form.name}" required  lay-verify="required" placeholder="请输入姓名" autocomplete="off" class="layui-input">--%>
              <%--</div>--%>
            <%--</div>--%>
          <%--</div>--%>
          <%--<div class="layui-row">--%>
            <%--<div class="tc-form-item layui-col-sm12">--%>
              <%--<label class="layui-form-label">性别</label>--%>
              <%--<div class="layui-input-block">--%>
                <%--<input type="radio" name="sex" value="0" title="男" ${form.sex0}>--%>
                <%--<input type="radio" name="sex" value="1" title="女" ${form.sex1}>--%>
              <%--</div>--%>
            <%--</div>--%>
          <%--</div>--%>
          <%--<div class="layui-row">--%>
            <%--<div class="tc-form-item layui-col-sm12">--%>
              <%--<label class="layui-form-label">音视频比例</label>--%>
              <%--<div class="layui-input-block">--%>
                <%--<input type="radio" name="videoRole" value="user169" ${form.videoRole_user169} title="16:9">--%>
                <%--<input type="radio" name="videoRole" value="user43" ${form.videoRole_user43} title="4:3">--%>
              <%--</div>--%>
            <%--</div>--%>
          <%--</div>--%>
        <%--</div>--%>

      <%--</div>--%>

      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">姓名</label>
          <div class="layui-input-block">
            <input type="text" name="name" value="${form.name}" required  lay-verify="required" placeholder="请输入姓名" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6" style="text-align:left; padding-left:40px;">
          <img src='' style="display:none;height:100px" id='upImgs'/>
          <button type="button"  class="layui-btn" style="margin-left:20px;" id="uploadPhoto">
            <i class="layui-icon" id='imgIcon'>&#xe67c;</i>上传照片
          </button>
          <input type="hidden" name="avatar" id="avatar" value="${form.avatar}">
        </div>
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">性别</label>
          <div class="layui-input-block">
            <input type="radio" name="sex" value="0" title="男" ${form.sex0}>
            <input type="radio" name="sex" value="1" title="女" ${form.sex1}>
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6" style="padding-left: 40px;">
          <label class="layui-form-label">音视频比例</label>
          <div class="layui-input-block">
            <input type="radio" name="videoRole" value="user169" ${form.videoRole_user169} title="16:9">
            <input type="radio" name="videoRole" value="user43" ${form.videoRole_user43} title="4:3">
          </div>
        </div>
      </div>

      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">职称</label>
          <div class="layui-input-block">
            <input type="text" name="job" value="${form.job}" placeholder="请输入职称" lay-verify="required" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">专长</label>
          <div class="layui-input-block">
            <input type="text" name="special" value="${form.special}" placeholder="请输入专长" lay-verify="required" autocomplete="off" class="layui-input">
          </div>
        </div>
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">电话</label>
          <div class="layui-input-block">
            <input type="text" name="phone" value="${form.phone}" required  lay-verify="phone" placeholder="请输入电话" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">邮箱</label>
          <div class="layui-input-block">
            <input type="text" name="email" value="${form.email}" required  lay-verify="email" placeholder="请输入邮箱" autocomplete="off" class="layui-input">
          </div>
        </div>
      </div>
    </div>
  </fieldset>
    <div class="tc-form-item " style="text-align:center">
      <button class="layui-btn tc-bg-main" lay-submit lay-filter="layForm">保 存</button>
    </div>
</form:form>
 
<script>
//统一接口

layui.use(['form','upload'], function(){
  var form = layui.form;
  var $=layui.$;
   form.on('submit(layForm)', function(data){
     $.post('PersonalSave',data.field,function(result){
    	if(result.code == 1){
    		layer.msg("修改成功");
    	} else if(result.code == -2){
    		top.layer.msg(data.field.email+" 邮箱已存在!");
    	}
     	else layer.msg("修改错误");
     },"json");
     return false;
   });

  //图片上传   http://192.168.1.157:8080/teleconsult/admin/upload
  var imgsrc="${form.avatar}";
  if(imgsrc){
	  $("#upImgs").attr("src","headimg/"+imgsrc).show();
  }
  var upload = layui.upload;
  var uploadInst = upload.render({
    elem: '#uploadPhoto' //绑定元素
    ,url:'uploadflv/upload' //上传接口
    ,headers:{
    	 enctype:"multipart/form-data"
    },
    data:{
    	type:'1'
    }
    ,done: function(res){   //上传完毕回调
    	debugger;
      console.log(res);
       if(res.code=='1'){
    	   var src='headimg/'+res.data;
    	   console.log(JSON.stringify(res.data))
    	   $('#upImgs').attr('src',src).show();
    	   $('#avatar').val(res.data);
    	  layer.msg('上传成功')
       }else{
    	   layer.msg('请重试')
       }
    }
    ,error: function(err){
      //请求异常回调
      layer.msg('服务器开小差啦')
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