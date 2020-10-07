<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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


<form class="layui-form " action="../uploadflv/importExcel" enctype="multipart/form-data">
        <div class="layui-form-item">
          <label class="layui-form-label" style="width:130px  ">导入EXCEL文件 <span style="color:#f00;">*</span></label>
          <div class="layui-form-label" style="margin-left:10px;width:200px" >
            <input type="file" name="file" id="test20">
          </div>
        </div>
</form>

<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script language="JavaScript">
layui.use('upload', function(){
	  var $ = layui.jquery
	  ,upload = layui.upload;
	//绑定原始文件域
	  upload.render({
	     elem: '#test20'
	     ,url: '../uploadflv/importExcel'
	     ,accept: 'file' //普通文件
	     ,exts: 'xls|xlsx' //只允许上传压缩文件
	   	 ,before: function(obj){
	   		ImportLoding=layer.load(3);
	   	 }
	     ,done: function(res){
	    	 parent.layui.layer.closeAll();
	    	 if(res.code == 1){
	    		 parent.layui.table.reload('users');
	    	 }
	       	 top.layer.alert(res.data,{maxWidth:600,maxHeight:400});
	     }
	   });
	});


</script>
</body>
</html>
