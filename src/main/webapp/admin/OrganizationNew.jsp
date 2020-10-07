<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <title></title>
  <link rel="stylesheet" type="text/css" href="../layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="../css/common.css"><!-- 自定义css-->
</head>
<body>
<form class="layui-form tc-form-p20" action="">
  <div class="layui-row">
    <div class="tc-form-item layui-col-xs11">
      <label class="layui-form-label">当前机构</label>
      <div class="layui-input-block">
        <input id="old" type="text" name="oldname" value="${currentName}" disabled class="layui-input">
      </div>
    </div>
  </div>
  <div class="layui-row">
    <div class="tc-form-item layui-col-xs11">
      <label class="layui-form-label">下级机构</label>
      <div class="layui-input-block">
        <input type="text" name="name" required lay-verify="required" placeholder="请输入新名称" autocomplete="off" class="layui-input">
      </div>
    </div>
  </div>
  <div class="layui-form-item" style="display: none;">
    <button class="layui-btn layui-btn-normal" lay-submit lay-filter="layForm">提交</button>
  </div>
  <input type="hidden" name="pid" value="${currentId}"/>
</form>
 
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="../js/common.js"></script>
<script>
//Demo
layui.use(['form','jquery'], function(){
  var $ = layui.$
  var form = layui.form;
  //$("#old").val(Utility.getUrlParam("currentName"))
  //监听提交
  form.on('submit(layForm)', function(data){
  $.post("OrganizationNewPost",data.field,function(result){
    	if(result.code==1){
    		top.layer.msg("添加成功！");
    		parent.location.reload();
    	}
    	else{
    		top.layer.msg("添加失败！");
    	}
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