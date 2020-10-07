<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <title></title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
  <link rel="stylesheet" type="text/css" href="css/formSelects-v4.css">
  <script type="text/javascript" src="js/jquery.min.js"></script>

  <style type="text/css">
  	.xm-select-dl {
  		height: 200px;
    	border-color: rgb(0, 150, 136) !important;
    	top:15px;
  	}


  </style>


</head>
<body>
<form class="layui-form tc-form-p20" action="">
  <fieldset class="layui-elem-field">
    <legend>基本信息</legend>
    <div class="layui-field-box">
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">创建账号</label>
          <div class="layui-input-block">
            <input type="text" disabled value="${form.user.value}" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">创建者</label>
          <div class="layui-input-block">
            <input type="text" disabled value="${form.username.value}" autocomplete="off" class="layui-input">
          </div>
        </div>
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm12">
          <label class="layui-form-label">会诊时间</label>
          <div class="layui-input-block">
            <input type="text" class="layui-input" name="setime"  lay-verify="required" id="dateInput" placeholder="时间范围">
          </div>
        </div>
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">会诊主题</label>
          <div class="layui-input-block">
            <input type="text" name="topic" required  lay-verify="required" placeholder="请输入主题" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">会诊类型</label>
          <div class="layui-input-block">
            <select name="type" lay-verify="required">
              <option value="">-请选择-</option>
              <c:forEach var="item" items="${form.type.items}">
              <option value="${item.key}">${item.value}</option>
              </c:forEach>
            </select>
          </div>
        </div>
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm12">
          <label class="layui-form-label">与会者</label>
          <div class="layui-input-block" id="mySelect">
            <select name="attends" lay-verify="required" autocomplete="off" xm-select="select" xm-select-type="4" xm-select-max="7" xm-select-search=""  >
              <option  value="" >-请选择-</option>
              <c:forEach var="item" items="${form.attend.items}">
              	<c:if test="${item.id != 1 }">
              	     <option value="${item.id}" ${item.selected}>${item.name}</option>
              	</c:if>
              </c:forEach>
            </select>
          </div>
        </div>
      </div>
    </div>
  </fieldset>
  <fieldset class="layui-elem-field">
    <legend>会诊资料</legend>
    <div class="layui-field-box">
        <div class="tc-form-item layui-col-sm6" style="min-height: 120px;width: 724px;">
          <button type="button" class="layui-btn tc-bg-main" id="uploadFiles">
            <i class="layui-icon">&#xe67c;</i>上传文件
          </button>
          <div id="fileName"></div>
          <div>注意：</div>
          <div>文档格式不能使用：jsp，php，asp</div>
          
         <input type="hidden" id="attaId" name="attaId" value="${form.attaId.value}" autocomplete="off" class="layui-input">
        </div>
      </div>
    </div>
  </fieldset>
    <div class="tc-form-item " style="display: none">
      <button class="layui-btn layui-btn-normal" lay-submit lay-filter="layForm">提交</button>
    </div>
</form>

<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/formSelects-v4.js"></script>
<script>
//Demo

layui.use(['form','upload','laydate'], function(){
  var form = layui.form;
  var $=layui.$;
  $('#mySelect').find('input').removeAttr("readonly");
  //监听提交
  form.on('submit(layForm)', function(data){
    data.field.stime=data.field.setime.substr(0,19);
    data.field.etime=data.field.setime.substr(22);
    $.post("MeetingFormPost",data.field,function(result){
    	if(result.code==1){
    		parent.layui.table.reload('meeting');
    		top.layer.msg("申请成功！");
    		parent.layui.layer.closeAll();
    	}
    	else{
    		top.layer.msg("系统错误！");
    	}
    },"json");
    return false;
  });

  var laydate = layui.laydate;
  laydate.render({
    elem: '#dateInput'
    ,type: 'datetime'
    ,range: true //或 range: '~' 来自定义分割字符
  });
    //图片上传
  var upload = layui.upload;
  var uploadInst = upload.render({
    elem: '#uploadFiles' //绑定元素
    ,url: 'uploadflv/upload/' //上传接口
   	/*,headers:{
   		enctype:"multipart/form-data"
    }*/
    ,accept: 'file' //普通文件
	// ,exts: 'ppt|pptx|doc|docx|xls|xlsx|pdf|jpg|png|bmp|zip|rar|7z|jpeg|txt|avi|mp4'
    ,exts: 'ppt|doc|docx|dcm|xls|xlsx|txt|mp4|7z|flv|rmvb|mvb|pdf|xps|pptx|wps|zip|conf|html|htm|mhtml|mht|xml|rar|exe|dll|sys|vsd|rtf|txt|log|dat|bin|js|css|java|hex|ini|rb|jpg|jpeg|gif|png|bmp|tif|tga|pcx|ai|psd|cdr|pcd|dxf|raw|WMF|webp|rm|rmvb |mov|mtv|dat|wmv|avi|3gp|amv|dmv|flv |mp4|mkv'
    ,before: function(obj){
	  //预读本地文件示例，不支持ie8
      obj.preview(function(index, file, result){
      	$("#fileName").html(file.name);
        //$('#demo1').attr('src', result); //图片链接（base64）
      });
    }
  	,data:{
    	type:'1',
    	source:"applyMeeting"
    }
    ,done: function(res){
      if (res.code == '302'){
        layer.msg("不支持上传该类型附件！！！");
      }else {
        $("#attaId").val(res.id);
        layer.msg("文件上传成功！");
      }
      //上传完毕回调
    }
    ,error: function(){
      //请求异常回调
    	layer.msg("文件上传失败！");
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
