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
<form class="layui-form tc-form-p20">
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm12">
          <label class="layui-form-label">说明 <span style="color:#f00;">*</span></label>
          <div class="layui-input-block">
           <input type="hidden" name="confKey" value="${form.conf_key}" >
            <input type="text" name="remark" value="${form.remark}" readonly="readonly"  lay-verify="remark"  autocomplete="off" class="layui-input">
          </div>
        </div>
      </div>
      <c:if test="${form.conf_key eq 'SYS_LOGO'}">
      		<div class="tc-form-item layui-col-sm6" style="text-align:left; padding-left:40px;">
	          <img src='' style="display:none;height:100px" id='upImgLoGo'/>
	          <button type="button"  class="layui-btn" style="margin-left:20px;" id="uploadLOGO">
	             <i class="layui-icon" id='imgIcon'>&#xe67c;</i>上传照片 
	          </button>
	          <input type="hidden" name="confValue" id="confValue" value="${form.conf_value}">
	        </div>
      
      </c:if>
       <c:if test="${form.conf_key  eq 'CONF_TEUDBORAD'}">
	      <div class="layui-row">
	        <div class="tc-form-item layui-col-sm12">
	          <label class="layui-form-label">内容<span style="color:#f00;">*</span></label>
	          <div class="layui-input-block">
	          	<select name="confValue" lay-verify="required">
		          	 <c:if test="${form.conf_value eq '0'}">
		          	 	 <option value="0"  selected="selected" >不开通</option>
		              <option value="1">开通</option>
		          	 </c:if>
		          	 <c:if test="${form.conf_value eq '1'}">
		          	 	 <option value="0" >不开通</option>
		              <option value="1" selected="selected">开通</option>
		          	 </c:if>
	            </select>
	        </div>
	      </div>
	    </div>
      </c:if>
      
      <c:if test="${form.conf_key  eq 'MEETING_CYCLE'}">
	      <div class="layui-row">
	        <div class="tc-form-item layui-col-sm5">
	          <label class="layui-form-label">内容<span style="color:#f00;">*</span></label>
	          <div class="layui-input-block">
	          	<select name="confValue_1" lay-verify="required" lay-filter="confValue_1" >
		          	 <c:if test="${form.conf_value eq '0'}">
		          	 	 <option value="0"  selected="selected">不删除</option>
		              	 <option value="00">一个月</option>
		              	 <option value="000">三个月</option>
		              	 <option value="0000">六个月</option>
		              	 <option value="00000">自定义周期</option>
		          	 </c:if>
		          	 <c:if test="${form.conf_value eq '00'}">
		          	 	 <option value="0">不删除</option>
		              	 <option value="00"  selected="selected">一个月</option>
		              	 <option value="000">三个月</option>
		              	 <option value="0000">六个月</option>
		              	 <option value="00000">自定义周期</option>
		          	 </c:if>
		          	 <c:if test="${form.conf_value eq '000'}">
		          	 	 <option value="0">不删除</option>
		              	 <option value="00">一个月</option>
		              	 <option value="000"  selected="selected">三个月</option>
		              	 <option value="0000">六个月</option>
		              	 <option value="00000">自定义周期</option>
		          	 </c:if>
		          	 <c:if test="${form.conf_value eq '0000'}">
		          	 	 <option value="0">不删除</option>
		              	 <option value="00">一个月</option>
		              	 <option value="000">三个月</option>
		              	 <option value="0000" selected="selected">六个月</option>
		              	 <option value="00000">自定义周期</option>
		          	 </c:if>
		          	 <c:if test="${form.conf_value ne '0' && form.conf_value ne '00' && form.conf_value ne '000' && form.conf_value ne '0000' }">
		          	 	 <option value="0">不删除</option>
		              	 <option value="00">一个月</option>
		              	 <option value="000">三个月</option>
		              	 <option value="0000">六个月</option>
		              	 <option value="00000" selected="selected" >自定义周期</option>
		          	 </c:if>
	            </select>
	            <input type="text" name="confValue" value="${form.conf_value}" style="display:${(form.conf_value eq '0' || form.conf_value eq '00' || form.conf_value eq '000' || form.conf_value eq '0000') ?'none':'' }" id="confValue"    lay-verify="required|number"  placeholder="请输内容"   autocomplete="off" class="layui-input">
	        </div>
	      </div>
	    </div>
      </c:if>
      <c:if test="${form.conf_key  ne 'SYS_LOGO' && form.conf_key  ne 'CONF_TEUDBORAD' && form.conf_key  ne 'MEETING_CYCLE' }">
	      <div class="layui-row">
	        <div class="tc-form-item layui-col-sm12">
	          <label class="layui-form-label">内容<span style="color:#f00;">*</span></label>
	          <div class="layui-input-block">
	          <input type="text" name="confValue" value="${form.conf_value}"   lay-verify="confValue"  placeholder="请输内容"   autocomplete="off" class="layui-input">
	        </div>
	      </div>
	    </div>
      </c:if>
    <div class="tc-form-item " style="display: none">
      <button class="layui-btn layui-btn-normal" lay-submit lay-filter="layForm">提交</button>
    </div>
</form>
 
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script>
//Demo
layui.use(['element','form','upload'], function(){
  var form = layui.form;
  var $=layui.$;
  
  //监听提交
  form.on('submit(layForm)', function(data){
	var index = layer.load(1);
    $.post("SysConFormPost?id=${id}",data.field,function(result){
    	layer.close(index);
    	parent.layui.table.reload('SysConfs');
		top.layer.msg("修改配置成功！");
		parent.layui.layer.closeAll();
    },"json");
    return false;
  });
  
  form.on('select(confValue_1)', function(data){
	  if(data.value == "00000"){
		  $("#confValue").css("display","");
		  $("#confValue").attr("value","");
	  }else{
		  $("#confValue").css("display","none");
		  $("#confValue").attr("value",data.value);
	  }
  });
  
  //图片上传 
  var imgsrc="${form.conf_value}";
  if(imgsrc){
	  $("#upImgLoGo").attr("src","../headimg/"+imgsrc).show();
  }
  var upload = layui.upload;
  var uploadInst = upload.render({
    elem: '#uploadLOGO' //绑定元素
    ,url: '../uploadflv/upload' //上传接口
    ,headers:{
   		enctype:"multipart/form-data"
    }
	,
	data:{
		type:'1'
	}
    ,done: function(res){
    	if(res.code=='1'){
    	   var src='../headimg/'+res.data;
     	   console.log(JSON.stringify(res.data))
     	   $('#upImgLoGo').attr('src',src).show();
     	   $('#confValue').val(res.data);
     	  layer.msg('上传成功')
        }else{
     	   layer.msg('请重试')
        }
    }
    ,error: function(){
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