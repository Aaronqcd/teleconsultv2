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
  <script src="https://cdn.bootcss.com/jquery/3.4.0/jquery.min.js"></script>
</head>
<body>
<div class="layui-form tc-form-p20" action="">
    <div class="layui-row">
        <div class="tc-form-item layui-col-sm12">
          <label class="layui-form-label" style="width: 100px;margin-left: 0px;padding-left: 0px">选择共享用户 </label>
          <div class="layui-input-block" id="mySelect">
            <select name="attend" lay-verify="required" id="attend" autocomplete="off" xm-select="select" xm-select-type="4" xm-select-max="7" xm-select-search=""  >
              <option  value="" >-请选择-</option>
              <c:forEach var="item" items="${users}">
              	<c:if test="${item.id != 1 }">
              		
              		 <c:if test="${item.isck == 1 }">
              	      <option value="${item.id}" selected="selected">${item.name}</option>
              	     </c:if>
              	     <c:if test="${item.isck != 1 }">
              	      <option value="${item.id}" >${item.name}</option>
              	     </c:if>
              	</c:if>
              </c:forEach>
            </select>
          </div>
          <br/><br/>
          <div style="text-align:right;margin-top:110px;margin-right: 10px">
	          <button type="button" class="layui-btn tc-bg-main"  onClick="saveSetShareFile();" style="width: 60px;height: 30px;line-height: 30px">提交</button>
	          <button type="button" class="layui-btn tc-bg-main" onClick="closeWin();"  style="width: 60px;height: 30px;line-height: 30px;background-color: #ccc">取消</button>
          </div>
          
        </div>
      </div>
      
</div>
 
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/formSelects-v4.js"></script>

<script src="js/formSelects-v4.js"></script>
<script>
var aid="${fileId}";
var mid="${mid}";


function closeWin(){
	parent.location.reload();
	parent.layer.close(parent.layer.getFrameIndex(window.name));
}



layui.use(['form','upload','laydate'], function(){
	  var form = layui.form;
	  var $=layui.$;
	  });

function saveSetShareFile(){
	var attend=[];
	var span=$(".xm-select-label span");
	for(var i=0;i<span.length;i++){
		attend.push($(span[i]).attr("value"));
	}
	if(attend.length<=0){
		layer.msg('请选择共享用户');
		return;
	}
 	$.ajax({ 
	     type: 'POST',
	     dataType: 'json',
         async: false,
         contentType: "application/x-www-form-urlencoded;charset=UTF-8",
         encoding: "UTF-8",
	     url: '${pageContext.request.contextPath}/saveSetShareFile',
	     data: {aid:aid,mid:mid,attend:attend.join(',')},
	     success: function(data){ 
	    	 if(data.code==200){
	    		 layer.msg('设置成功');
	    		 parent.location.reload();
	    		 parent.layer.close(parent.layer.getFrameIndex(window.name));
   	    	 }else{
   	    		layer.msg('设置失败，请刷新后重试。')
   	    	 }
	     }
	});
	
	
	
}





</script>


</body>
</html>