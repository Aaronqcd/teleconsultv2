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
<input type="hidden" id="dianhua" value="${form.phone.value}">
<form class="layui-form tc-form-p20">
  <fieldset class="layui-elem-field">
    <legend>账号信息</legend>
    <div class="layui-field-box">
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm12">
          <label class="layui-form-label">账号 <span style="color:#f00;">*</span></label>
          <div class="layui-input-block">
          <c:if test="${form.user.value eq 'admin' }" >
          	<input type="text" name="user" value="${form.user.value}" required readonly="readonly"  lay-verify="username" placeholder="请输入账号" autocomplete="off" class="layui-input">
          </c:if>
          <c:if test="${form.user.value ne 'admin'}">
            <input type="text" name="user" value="${form.user.value}" required  lay-verify="username" placeholder="请输入账号" autocomplete="off" class="layui-input">
          </c:if>
          </div>
        </div>
      <!--   <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">密码</label>
          <div class="layui-input-block">
            <input type="password" name="password" required lay-verify="required" placeholder="请输入密码" autocomplete="off" class="layui-input">
          </div>
        </div> -->
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">所属机构 <span style="color:#f00;">*</span></label>
          <div class="layui-input-block">
            <select name="belong" lay-verify="required">
              <option value="">-请选择-</option>
              <c:forEach var="item" items="${form.belong.items}">
              <option value="${item.id}" ${item.selected}>${item.name}</option>
              </c:forEach>
            </select>
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
        	<c:if test="${form.user.value eq 'admin' }">
              	<label class="layui-form-label">账号类型 </label>
		          <div class="layui-input-block">
		            <label class="layui-form-label">系统管理员 </label>
		            <input type="hidden" name="group" value="admins" >
		          </div>
             </c:if>
        	<c:if test="${form.user.value ne 'admin' }">
	          <label class="layui-form-label">账号类型 <span style="color:#f00;">*</span></label>
	          <div class="layui-input-block">
	            <select name="group" lay-verify="required">
	              <option value="">-请选择-</option>
	              <c:forEach var="item" items="${form.group.items}">
	              <option value="${item.key}" ${item.selected}>${item.groupname}</option>
	              </c:forEach>
	            </select>
	          </div>
	         </c:if>
        </div>
      </div>
    </div>
  </fieldset>
  <fieldset class="layui-elem-field">
    <legend>个人信息</legend>
    <div class="layui-field-box">
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <div class="layui-row">
            <div class="tc-form-item layui-col-sm12">
              <label class="layui-form-label">姓名 <span style="color:#f00;">*</span></label>
              <div class="layui-input-block">
                <input type="text" name="name" value="${form.name.value}" required  lay-verify="required" placeholder="请输入姓名" autocomplete="off" class="layui-input">
              </div>
            </div>
          </div>
          <div class="layui-row">
            <div class="tc-form-item layui-col-sm12">
              <label class="layui-form-label">性别 <span style="color:#f00;">*</span></label>
              <div class="layui-input-block">
              <c:forEach var="item" items="${form.sex.items}">
                <input type="radio" name="sex" value="${item.key}" title="${item.value}" ${item.checked}>
              </c:forEach>
              </div>
            </div>
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6" style="text-align:left; padding-left:40px;">
          <img src='' style="display:none;height:100px" id='upImgs'/>
          <button type="button"  class="layui-btn" style="margin-left:20px;" id="uploadPhoto">
             <i class="layui-icon" id='imgIcon'>&#xe67c;</i>上传照片 
          </button>
          <input type="hidden" name="avatar" id="avatar" value="${form.avatar.value}">
        </div>
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">职称 <span style="color:#f00;">*</span></label>
          <div class="layui-input-block">
            <input type="text" name="job" value="${form.job.value}" required lay-verify="required" placeholder="请输入职称" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">专长 <span style="color:#f00;">*</span></label>
          <div class="layui-input-block">
            <input type="text" name="special" value="${form.special.value}" required lay-verify="required" placeholder="请输入专长" autocomplete="off" class="layui-input">
          </div>
        </div>
      </div>
      <div class="layui-row">
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">电话 <span style="color:#f00;">*</span></label>
          <div class="layui-input-block">
            <input type="text" name="phone" value="" required  lay-verify="phone" placeholder="请输入电话" autocomplete="off" class="layui-input">
          </div>
        </div>
        <div class="tc-form-item layui-col-sm6">
          <label class="layui-form-label">邮箱 <span style="color:#f00;">*</span></label>
          <div class="layui-input-block">
            <input type="text" name="email" value="${form.email.value}" required  lay-verify="email" placeholder="请输入邮箱" autocomplete="off" class="layui-input">
          </div>
        </div>
      </div>
    </div>
  </fieldset>
    <div class="tc-form-item " style="display: none">
      <button class="layui-btn layui-btn-normal" lay-submit lay-filter="layForm">提交</button>
    </div>
</form>
 
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script>
//Demo
layui.use(['form','upload'], function(){
  var form = layui.form;
  var $=layui.$;
  
  var dh = $("#dianhua").val()
  var dhz = dh.substr(12);
  $("input[name='phone']").val(dhz);
  
  form.verify({
	  
	  
	  username: function(value, item){ //value：表单的值、item：表单的DOM对象
	    if(!new RegExp("^[a-zA-Z0-9_]+$").test(value)){
	      return '用户名不能有特殊字符';
	    }
	    if(/(^\_)|(\__)|(\_+$)/.test(value)){
	      return '用户名首尾不能出现下划线\'_\'';
	    }
	    if(/^\d+\d+\d$/.test(value)){
	      return '用户名不能全为数字';
	    }
	  },
	  phone:function(value, item){
		  if(/^1(65|70)/.test(value)){
			  return "手机号禁用165，170开头！"
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
  
  //监听提交
  form.on('submit(layForm)', function(data){
	var index = layer.load(1);
    //layer.msg(JSON.stringify(data.field));
    $.post("UserFormPost?action=${action}&id=${id}",data.field,function(result){
    	layer.close(index);
    	action="${action}";
    	/*if(action=="add"){*/
    		if(result.code==1){
    			parent.layui.table.reload('users');
        		top.layer.msg(action=="add"?"添加用户成功！":"修改用户成功！");
        		parent.layui.layer.closeAll();
        	} else if(result.code==0){
        		top.layer.msg("该机构已存在机构管理员!");
        	} else if(result.code==-1){
        		top.layer.msg(data.field.user+" 账号已存在!");
        	} else if(result.code==-2){
        		top.layer.msg(data.field.email+" 邮箱已存在!");
        	}
        	else{
        		top.layer.msg(result.msg);
        	}/*
    	}else{
    		if(result.code==1){
    			parent.layui.table.reload('users');
        		top.layer.msg("修改用户成功！");
        		parent.layui.layer.closeAll();
        	} else if(result.code==0){
        		top.layer.msg("该机构已存在机构管理员!");
        	} else if(result.code==-1){
        		top.layer.msg(data.field.user+" 账号已存在!");
        	} else if(result.code==-2){
        		top.layer.msg(data.field.email+" 邮箱已存在!");
        	}
        	else{
        		top.layer.msg(result.msg);
        	}
    	}*/
    	
    },"json");
    return false;
  });

  //图片上传
  var imgsrc="${form.avatar.value}";
  if(imgsrc){
	  $("#upImgs").attr("src","../headimg/"+imgsrc).show();
  }
  var upload = layui.upload;
  var uploadInst = upload.render({
    elem: '#uploadPhoto' //绑定元素
    ,url: '../uploadflv/upload' //上传接口
    ,headers:{
   		enctype:"multipart/form-data"
    }
	,
    acceptMime: 'image/jpg, image/png, image/jpeg, image/bmp, image/gif',
	data:{
		type:'1'
	}
    ,done: function(res){
    	if(res.code=='1'){
    	   var src='../headimg/'+res.data;
     	   console.log(JSON.stringify(res.data))
     	   $('#upImgs').attr('src',src).show();
     	   $('#avatar').val(res.data);
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