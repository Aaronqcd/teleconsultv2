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
<form class="layui-form tc-form-p20" action="">
  <fieldset class="layui-elem-field">
    <legend>文件详情</legend>
    <div class="layui-field-box">

        <input type="hidden" value="${meetingInfo.id}" id="id"> </input>

        <table lay-filter="layTable"  class="layui-table" id="layTable"  >

		</table>


    </div>
  </fieldset>


   <div class="layui-field-box" style="display: none" id="uploadFileDiv" >
        <div class="tc-form-item layui-col-sm6" style="min-height: 120px;width: 100%;text-align: right;">
          <button type="button" class="layui-btn tc-bg-main" id="uploadFiles">
            <i class="layui-icon" >&#xe67c;</i>上传文件
          </button>
          <div id="fileName"></div>
          <input type="hidden" id="attaId" name="attaId" required  lay-verify="attaId" autocomplete="off"  class="layui-input" />
        </div>
     </div>




</form>

 <script type="text/html" id="switchTpl">
  <input type="checkbox" name="sex" value="{{d.id}}" lay-skin="switch" lay-text="共享|关闭" lay-filter="sexDemo" {{ d.isShare == 1 ? 'checked' : '' }}>

</script>
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/formSelects-v4.js"></script>
<script>
var table = null;
layui.use('table', function(){
	table = layui.table;
	table.render({
	    elem: '#layTable'
		    ,url:'/teleconsult/pathform?id='+'${form.id}'
		    ,cols: [[
		      {field:'fileName', title:'文件名称',align:'center', width:200}
		      ,{field:'path', title:'路径',align:'center', width:300,}
		      ,{field:'sysName', title:'预览/下载',align:'center', width:100,templet:function(d){
		    	  var href ="";
		    	  if(d.sysName){
		    		  href = "window.open('headimg/"+d.sysName+"')";
		    	  }

		    	  if(d.dcmURL){
		    		  var curWwwPath = window.document.location.href;

		    		  var slashPos = curWwwPath.indexOf("//");
		    		  var colonPos = curWwwPath.indexOf(":",slashPos+"//".length);
		    		  if( colonPos < 0 )
		    		  {
		    			colonPos = curWwwPath.indexOf("/",slashPos+"//".length);
		    		  }
		    		  var host = curWwwPath.substring( slashPos+2, colonPos);

		    		  var url = d.dcmURL.replace("{server}", host);

		    		  console.log( url );
		    		  //alert( curWwwPath + "," + host + "," + url );

		    		  href = "window.open('"+url+"')";
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }

		    	  if(d.sysName.toLowerCase().endWith(".bmp") || d.sysName.endWith(".BMP")){
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }
		    	  if(d.sysName.toLowerCase().endWith(".jpg") || d.sysName.endWith(".JPG")){
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }
		    	  if(d.sysName.toLowerCase().endWith(".png") || d.sysName.endWith(".PNG")){
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }
		    	  if(d.sysName.toLowerCase().endWith(".tif") || d.sysName.endWith(".TIF")){
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }
		    	  if(d.sysName.toLowerCase().endWith(".gif") || d.sysName.endWith(".GIF")){
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }
		    	  if(d.sysName.toLowerCase().endWith(".pcx") || d.sysName.endWith(".PCX")){
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }
		    	  if(d.sysName.toLowerCase().endWith(".psd") || d.sysName.endWith(".PSD")){
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }
		    	  if(d.sysName.toLowerCase().endWith(".svg") || d.sysName.endWith(".SVG")){
		    		  return '<a  onclick="findFile('+href+')">查看</a>'
		    	  }
		    	var downloadstr="download='"+d.fileName+"'";
		    	var astr='<a  href="headimg/'+d.sysName+'"  '+downloadstr+' >查看</a>';
		        return astr;
		      }}
		     	,  {field:'sex', title:'操作', width:130, templet: '#switchTpl', unresize: true}]]
		      ,response:{
		    	  statusName: 'status',
		    	  statusCode: 200
		      }
		  });
	});


layui.use(['laydate', 'form'], function(){
    form = layui.form;
    //监听提交
    form.on('switch(sexDemo)', function (data) {

    	if(data.elem.checked==1){
    		layer.open({
    	          type: 2,
    	          closeBtn: 0,
    	          content: 'setsharefile?id='+'${form.id}'+'&fileId='+data.value,
    	          title:'设置共享文件',
    	          area: ['600px', '300px'],
    	          response:{
    		    	  statusName: 'status',
    		    	  statusCode: 200
    		      }
    	     });
    	}else{
    		 $.ajax({
   	            url: "selectSetShareFile",
   	            type: 'post',
   	            dataType: 'json',
   	            contentType: "application/x-www-form-urlencoded; charset=utf-8",
   	            data: {ishare:data.elem.checked?'1':'0',aid:data.value,mid:'${form.id}' },
   	            	success: function (data) {
   	                if (data && data.code == 200) {
   	              		layer.msg("成功");
   	                }else{
   	                	layer.msg("失败，请刷新后重试！");
   	                }
   	            }
   	        });
    	}

        return false;
    });
  });

String.prototype.endWith=function(endStr){
      var d=this.length-endStr.length;
      return (d>=0&&this.lastIndexOf(endStr)==d);
 }


function setsharefilebtu(fileId){
	layer.open({
          type: 2,
          content: 'setsharefile?id='+'${form.id}'+'&fileId='+fileId,
          title:'设置共享文件',
          area: ['600px', '300px'],
          response:{
	    	  statusName: 'status',
	    	  statusCode: 200
	      }
     });
};


function setissharefilebtu(obj,fileId){
	alert($(obj).val());

}

function findFile(href){
	if(href === undefined || ""=== href || href === null){
		layer.msg('该用户未上传相应资料！');
	}else{
		return true;
	}

}
</script>

<script src="js/formSelects-v4.js"></script>
<script>
var number="${number}";
$(function (){
	if(number>0){
		$("#uploadFileDiv").show();
	}
})


layui.use(['form','upload','laydate'], function(){
  var form = layui.form;
  var $=layui.$;
 //文件上传
 /*  var imgsrc="${form.attaId.value}";
  if(imgsrc){
	  $("#uploadFiles").attr("src","../webapp/images/headimg/"+imgsrc).show();
  } */
  var upload = layui.upload;
  var files = "";
  var attaid = "";
  var i=0;
  var uploadInst = upload.render({
    elem: '#uploadFiles' //绑定元素
    ,url: '../teleconsult/uploadflv/upload' //上传接口 http://localhost:8080/teleconsult/uploadflv/upload
    // ,exts: 'ppt|pptx|doc|docx|xls|xlsx|pdf|jpg|png|bmp|zip|rar|7z|jpeg|txt|avi|mp4'
    ,exts: 'ppt|doc|docx|dcm|xls|xlsx|txt|mp4|7z|flv|rmvb|mvb|pdf|xps|pptx|wps|zip|conf|html|htm|mhtml|mht|xml|rar|exe|dll|sys|vsd|rtf|txt|log|dat|bin|js|css|java|hex|ini|rb|jpg|jpeg|gif|png|bmp|tif|tga|pcx|ai|psd|cdr|pcd|dxf|raw|WMF|webp|rm|rmvb |mov|mtv|dat|wmv|avi|3gp|amv|dmv|flv |mp4|mkv'
    ,accept:'file'
    ,number:number
    ,before: function(obj){
	  //预读本地文件示例，不支持ie8
      obj.preview(function(index, file, result){
    	 //alert(file.name);
    	  //$("#fileName").html(files);
        //$('#demo1').attr('src', result); //图片链接（base64）
      });
    }
  	,data:{
    	type:'1',
    	source:"applyMeeting"
    }
  	,multiple:true
    ,done: function(res){
    	if(res.code==-4947){
    		layer.msg('文件上传失败！');
    		return;
		}else if (res.code == '302'){
            layer.msg("不支持上传该类型附件！！！");
        }else {
            $("#attaId").val(res.id);
            layer.msg("文件上传成功！");
        }
     	if(number<=0){
    		layer.msg('文件超过最多允许上传八个');
    		return;
     	}
		var attaId=res.id;


    	$.ajax({
    	     type: 'POST',
    	     dataType: 'json',
             async: false,
             contentType: "application/x-www-form-urlencoded;charset=UTF-8",
             encoding: "UTF-8",
    	     url: '${pageContext.request.contextPath}/saveMeetingAttaId',
    	     data: {attaId:attaId,id:$("#id").val()},
    	     success: function(data){
    	    	 if(data.code==200){
    	    		number=data.number;
    	    		if(number<=0){
    	    			$("#uploadFileDiv").hide();
    	    		}
	   	    		table.render({
		       	    elem: '#layTable'
		       		    ,url:'/teleconsult/pathform?id='+'${form.id}'
		       		    ,cols: [[
		       		      {field:'fileName', title:'文件名称',align:'center', width:200}
		       		      ,{field:'path', title:'路径',align:'center', width:300,}
		       		      ,{field:'sysName', title:'预览/下载',align:'center', width:100,templet:function(d){
		       		    	  var href ="";
		       		    	  if(d.sysName){
		       		    		  href = "window.open('headimg/"+d.sysName+"')";
		       		    	  }

			       		    	if(d.dcmURL){
			  		    		  var curWwwPath = window.document.location.href;

			  		    		  var slashPos = curWwwPath.indexOf("//");
			  		    		  var colonPos = curWwwPath.indexOf(":",slashPos+"//".length);
			  		    		  if( colonPos < 0 )
			  		    		  {
			  		    			colonPos = curWwwPath.indexOf("/",slashPos+"//".length);
			  		    		  }
			  		    		  var host = curWwwPath.substring( slashPos+2, colonPos);

			  		    		  var url = d.dcmURL.replace("{server}", host);

			  		    		  console.log( url );
			  		    		  //alert( curWwwPath + "," + host + "," + url );

			  		    		  href = "window.open('"+url+"')";
			  		    		  return '<a  onclick="findFile('+href+')">查看</a>';
			  		    	  }

		       		        return '<a  onclick="findFile('+href+')">查看</a>';
		       		      }}
		       		   ,  {field:'sex', title:'操作', width:130, templet: '#switchTpl', unresize: true}]]
		       		      ,response:{
		       		    	  statusName: 'status',
		       		    	  statusCode: 200
		       		      }
		       		  });
	    	    	} else if (data.code==510){
	    	    		layer.msg('文件超过最多允许上传八个');
	    	    	}else{
	    	    		layer.msg('上传失败，请刷新后重试！')
	    	    	}
    	     }
    	});


    }
    ,error: function(){
      //请求异常回调
      layer.msg('服务器开小差啦')
    }
  });




});
</script>


</body>
</html>
