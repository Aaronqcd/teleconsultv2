<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  
   <%session.setAttribute("path", request.getContextPath());%>
  <title>资源管理</title>
  <link rel="stylesheet" type="text/css" href="${path}/layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="${path}/css/common.css"><!-- 自定义css-->
  <link href="https://cdn.bootcss.com/twitter-bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet"><!-- bootstrap.css-->
  <style type="text/css">
  	#fm1 table{width: 350px;}
  	#fm1 table input{width: 220px}
  	#fm2 table{width: 350px;}
  	#fm2 table input{width: 220px}
  	#fm3 table{width: 400px;}
  	#fm3 table input{width: 220px}
  	#fm3 table font{width: 30px}
  	.fm1_tb input{line-height: 25px;}
  	.fm1_tb font{font-size: 18px}
  	.fm2_tb input{line-height: 25px;}
  	.fm2_tb font{font-size: 18px}
  	.fm3_tb input{line-height: 25px;}
  	.fm3_tb font{font-size: 18px}  	
  	#fm1 label{width: 139px;}
  	#fm1 .layui-input-block{margin-left: 140px;min-height: 36px;}
  	#fm2 label{width: 139px;}
  	#fm2 .layui-input-block{margin-left: 140px;min-height: 36px;}
  	#fm3 label{width: 139px;}
  	#fm3 .layui-input-block{margin-left: 140px;min-height: 36px;}
  </style>
</head>
<body>
<!-- 左侧 -->
<div class="layui-side" style="width: 230px;border-right: 1px solid #ddd;background: #fff">
	<div class="layui-side-scroll" style="background: #fff;width: 250px">
    	<div id="tt" class="tree-hospital" style="width: 230px;margin-top: 15px;"></div>
  	</div>
</div>
<!-- 右侧 -->   
<div class="layui-body" id="layui-body" style="background:#f5f5f5;left: 230px">
<div class="user-right-content" id="user-right-content">
  	<!-- 上方工具 -->
  	<div class="layui-table-tool" id="layui-table-tool">
  	<div class="layui-inline tc-search">
        <input id="searchLike" placeholder="搜索服务器名、视频名" class="layui-input">
        <button id="searchResource" data-type="reload" class="layui-btn layui-btn-sm"><i class="layui-icon layui-icon-search"></i></button>
     </div>
		<div class="layui-form" style="display: inline-block;float: right;padding-top: 5px;font-size: 12px">
			<div class="layui-inline" style="width: 100px">
				<select name="interest" id="selectRes">
			        <option value="">资源类型</option>
			          <c:forEach var="item" items="${resourceTypeList}">
			          	<option value="${item.typeeng}">${item.resourcetype}</option>
			          </c:forEach>
			      </select>
			</div>				
			<button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main"  id="addResource"><i class="layui-icon layui-icon-add-circle"></i>新增资源</button>
			<button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main"  id="delResource"><i class="layui-icon layui-icon-delete"></i>删除资源</button>
			<button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main"  id="updateResource"><i class="layui-icon layui-icon-edit"></i>修改资源</button>
 		</div>
  	</div>
  	
  	<div class="layui-table-box" style="background-color: #fff;min-height: 810px;" id="layui-table-box">
  	<!--<div style="width: 500;margin: auto;" id="layui-table-box"> background-color: #f5f5f5-->
  		<form id="fm1" method="post" style="display: none;width: 50%;height:650px;background: #ffffff;margin:0 auto;padding-top: 80px">
			  	<input name="orgid" id="hiddenInput" value="${currentOrgId}" type="hidden" ><!-- 当前机构 用作his资源增加-->
			  	
			  	<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">服务器名称</label>
					  <div class="layui-input-block">
						<input name="servername" class="layui-input" title="服务器名称" id="form1_servername" maxLength="15" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;" class="layui-input"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">服务器IP</label>
					  <div class="layui-input-block">
						<input name="serverip" class="layui-input" title="服务器IP"  maxLength="15" id="serverIp1" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">端口</label>
					  <div class="layui-input-block">
						<input name="dbport" class="layui-input" title="端口"  maxLength="15" id="db1port" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">数据库</label>
					  <div class="layui-input-block">
						<input name="datesource" class="layui-input" title="数据库"  maxLength="15" id="form1_datesource" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">登录名</label>
					  <div class="layui-input-block">
						<input name="loginname" class="layui-input" title="登录名"  maxLength="15" id="form1_loginname" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">登录口令</label>
					  <div class="layui-input-block">
						<input name="loginkey" class="layui-input" title="登录口令"  maxLength="15" onkeyup="this.value=this.value.replace(/\s+/g,'')" type="password" id="form1_loginkey" />
					  </div>
					</div>
				</div>
  			    <div style="text-align:center; vertical-align:middel;" id="linkbutton1">
  			    	<input type="button" class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" id="submit1" value="提交" style="width: 50px;height: 28px;">
  			    	<input type="button" value="样式框" style="width: 30px;height: 35px;visibility:hidden">
  			    	<input type="button" class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" id="cancel1" value="取消" style="width: 50px;height: 28px;">
  			    </div>
  		</form> 
  		<form id="fm2" method="post" style="display: none;width: 50%;height:650px;background: #ffffff;margin:0px auto;padding-top: 30px">
 			  	<input name="orgid" value="${currentOrgId}" type="hidden"><!-- 当前机构 用作pacs资源增加 --->
 			  	
 			  	<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">服务器</label>
					  <div class="layui-input-block">
						<input name="server" class="layui-input" title="服务器"  maxLength="15" id="form2_server" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">类型</label>
					  <div class="layui-input-block">
						<input name="type" class="layui-input" title="类型"  maxLength="10" id="form2_type" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">IP地址</label>
					  <div class="layui-input-block">
						<input name="ipaddress" class="layui-input" title="IP地址"  maxLength="15" id="serverIp2" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">端口</label>
					  <div class="layui-input-block">
						<input name="port" class="layui-input" title="端口"  maxLength="5" id="port2" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">主叫AET</label>
					  <div class="layui-input-block">
						<input name="zaet" class="layui-input" title="主叫AET"  maxLength="25" id="form2_zaet" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">被叫AET</label>
					  <div class="layui-input-block">
						<input name="baet" class="layui-input" title="被叫AET" maxLength="25" id="form2_baet" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">数据库IP</label>
					  <div class="layui-input-block">
						<input name="dbip" class="layui-input" title="数据库IP" maxLength="40" id="db2ip" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">数据库端口</label>
					  <div class="layui-input-block">
						<input name="dbport" class="layui-input" title="数据库端口" maxLength="40" id="db2port" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">数据库</label>
					  <div class="layui-input-block">
						<input name="dbname" class="layui-input" title="数据库" maxLength="40" id="db2name" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">登录名</label>
					  <div class="layui-input-block">
						<input name="dbuser" class="layui-input" title="登录名" maxLength="40" id="db2user" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">登录口令</label>
					  <div class="layui-input-block">
						<input name="dbpwd" class="layui-input" title="登录口令" maxLength="40" id="db2pwd" type="password" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
  			     <div style="text-align:center; vertical-align:middel;" id="linkbutton2">
  			   		<input type="button" class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main"  id="submit2" value="提交"  style="width: 50px;height: 28px;">
  			   		<input type="button"  value="样式框" style="width: 50px;height: 35px;visibility:hidden;">
  			    	<input type="button"  class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" id="cancel2" value="取消" style="width: 50px;height: 28px;">
  			    </div> 
  			</form>
  			<form id="fm3" method="post" style="display: none;background: #ffffff;width: 50%;margin:0px auto;padding-top: 20px">
 			  	<input name="orgid" title="隐藏机构名" value="${currentOrgId}" type="hidden"><!-- 当前机构 用作video资源增加-->
 			  	
 			  	<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">手术室名称</label>
					  <div class="layui-input-block">
						<input name="roomname" class="layui-input" title="手术室名称" style="width: 90%;"  maxLength="15" id="form3_roomname" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">手术室类型</label>
					  <div class="layui-input-block">
						<select name="roomtype" id="selectRoomId" title="资源类型"  style="width: 90%;" lay-verify="required" class="layui-input layui-select">
				           	<c:forEach var="item" items="${roomType}">
				          		<option value="${item.engname}">${item.roomtype}</option>
				          	</c:forEach> 
					     </select>
					  </div>
					</div>
				</div>
				<div class="layui-row">
					<div class="tc-form-item layui-col">
					  <label class="layui-form-label">视频设备ID</label>
					  <div class="layui-input-block">
						<input name="roomip" class="layui-input" title="手术室IP"  style="width: 90%;" maxLength="15" id="serverIp3" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;"/>
					  </div>
					</div>
				</div>
				
				<div id="videoDiv"></div>
				
				<div class="layui-row">
					<div class="tc-form-item layui-col" style="margin-bottom: -30px;">
					  <label class="layui-form-label"></label>
					  <div class="layui-input-block" style="display: -webkit-box;">
						<label style="width: 90%;display: block;"></label>
						<a class="addVideo layui-icon layui-icon-add-circle" title="新增" style="font-size: 20px;display: block;margin-top: -49px; margin-left: 5px;"></a>
					  </div>
					</div>
				</div>
				
			    <div style="text-align:center; vertical-align:middel;" id="linkbutton3">
  			   		<input type="button" class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" id="submit3" value="提交"  style="width: 50px;height: 28px;">
  			   		<input type="button" value="样式框"  style="width: 50px;height: 35px;visibility:hidden">
  			    	<input type="button" class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" id="cancel3" value="取消" style="width: 50px;height: 28px;">
  			    </div> 
  			</form> 
   	<input name="hideId" type="hidden" id="hideId" value=""><!-- 用来区分是增加还是修改操作 -->
   	<input type="hidden" id="gropuname" value="${gropuname}">
	</div> 

</div>
</div>

  <script src="${path}/layui-v2.4.5/layui.js"></script><!-- layui.js -->
  <script type="text/javascript" src="${path}/js/jquery-7.min.js"></script>
   <script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
 <script src="js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<script src="js/jquery.tmpl.js"></script>

<script id="videoTemplate" type="text/x-jquery-tmpl">
<div id="video{{= index}}">
	<div class="layui-row">
		<div class="tc-form-item layui-col">
		  <label class="layui-form-label">视频源{{= index}}名称</label>
		  <div class="layui-input-block" style="display: -webkit-box;">
			<input name="v{{= index}}name" disabled="disabled" class="layui-input" title="视频源{{= index}}名称" value="{{= vname}}" maxLength="15" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;" id="form3_v{{= index}}name" style="width: 90%;"/>
			<label class="deleteVideo" index="{{= index}}" title="删除" style="width: 20px;margin-left: 5px;margin-top: 5px;"><a class="layui-icon layui-icon-delete" style="font-size: 22px;"></a></label>
		  </div>
		</div>
	</div>
	<div class="layui-row">
		<div class="tc-form-item layui-col">
		  <label class="layui-form-label">播放地址{{= index}}</label>
		  <div class="layui-input-block" style="display: -webkit-box;">
			<input name="v{{= index}}add" disabled="disabled" class="layui-input" title="播放地址{{= index}}" value="{{= vadd}}" maxLength="250" onKeypress="javascript:if(event.keyCode == 32)event.returnValue = false;" id="form3_v{{= index}}add" style="width: 90%;"/>
		  </div>
		</div>
	</div>
</div>
</script>

<script type="text/javascript">
var type = "";//资源类型的英文名称
var insertType = "";//选中资源类型的名称如：HIS
var currentResourceId = "";//当前资源的id
var groupPower = "";//admins  managers users
$(function() {
	document.querySelector('#searchLike').onkeydown = function (event) {
		 var e = event || window.event || arguments.callee.caller.arguments[0];
			                if (e && e.keyCode == 13) {
			                    var servername = $("#searchLike").val().trim();
			if(servername == ""){
				layer.msg('查询字段不能为空！', {icon: 7});
			}else{
				//var url = "/teleconsult/findResource?id="+$("#hiddenInput").val() +"&servername="+servername;
				//$.get 传到后台中文乱码
				$.ajax({
					 data: {
    	                "id":$("#hiddenInput").val(),
    	                "servername":servername
    	            }, 
    	            type: "post",
    	            contentType: "application/x-www-form-urlencoded;charset=UTF-8",//指定发送请求报文头 编码格式
    	            encoding: "UTF-8",
    	            url: "/teleconsult/findResource",
    	            success: function (data) {
    	            	var dataType = "";
    						console.log(data);
    						var result = JSON.parse(data);
    						if(!isObjEmpty(result)){
    							var node = {};
    							if(result.HIS){
    								node.text = "HIS";
    								result = result.HIS;
    							}else if(result.LIS){
    								node.text = "LIS";
    								result = result.LIS;
    							}else if(result.PACS){
    								node.text = "PACS";
    								result = result.PACS;
    							}else if(result.RIS){
    								node.text = "RIS";
    								result = result.RIS;
    							}else if(result.VIDEO){
    								node.text = "VIDEO";
    								result = result.VIDEO;
    							}
    							dataType = node.text;
    							//$('#tt').treeview('selectNode', [ nodeId, { silent: true } ]);
    							 if(node.text == "HIS" || node.text == "LIS"){
    								 $("#fm1 input[name='servername']").val(result.servername);
    								 $("#fm1 input[name='serverip']").val(result.serverip);
    								 $("#fm1 input[name='dbport']").val(result.dbport);
    								 $("#fm1 input[name='datesource']").val(result.datesource);
    								 $("#fm1 input[name='loginname']").val(result.loginname);
    								 $("#fm1 input[name='loginkey']").val(result.loginkey);
    								 $("#fm1 input[name='servername']").attr("disabled","disabled");
    								 $("#fm1 input[name='serverip']").attr("disabled","disabled");
    								 $("#fm1 input[name='dbport']").attr("disabled","disabled");
    								 $("#fm1 input[name='datesource']").attr("disabled","disabled");
    								 $("#fm1 input[name='loginkey']").attr("disabled","disabled");
    								 $("#fm1 input[name='loginname']").attr("disabled","disabled");
    								 //如果 其它两个form表单是显示的，则需要隐藏，并清空
    								 var display2 =$('#fm2').css('display');
    								 if(display2 != 'none'){
    									 var nod = {};node.text = "RIS";//随便去了一个
    									 clearForm(nod);
    									 $('#fm2').hide();
    								 }
    								 var display3 =$('#fm3').css('display');
    								 if(display3 != 'none'){
    									 var nod = {};node.text = "VIDEO";
    									 clearForm(nod);
    									 $('#fm3').hide();
    								 }
    								 $("#linkbutton1").hide();
    								 $("#fm1").show();
    								 $("#submit1").attr("disabled",true);
    				 	 		}else if(node.text == "PACS" || node.text == "RIS"){
    				 	 			 //$("#fm2 input[name='servername']").val(result.servername);
    								 $("#fm2 input[name='server']").val(result.server);
    								 $("#fm2 input[name='type']").val(result.type);
    								 $("#fm2 input[name='ipaddress']").val(result.ipaddress);
    								 $("#fm2 input[name='port']").val(result.port);
    								 $("#fm2 input[name='zaet']").val(result.zaet);
    								 $("#fm2 input[name='baet']").val(result.baet);
    								 $("#fm2 input[name='dbip']").val(result.dbip);
    								 $("#fm2 input[name='dbport']").val(result.dbport);
    								 $("#fm2 input[name='dbname']").val(result.dbname);
    								 $("#fm2 input[name='dbuser']").val(result.dbuser);
    								 $("#fm2 input[name='dbpwd']").val(result.dbpwd);
    								 //$("#fm2 input[name='servername']").attr("disabled","disabled");
    								 $("#fm2 input[name='server']").attr("disabled","disabled");
    								 $("#fm2 input[name='serverip']").attr("disabled","disabled");
    								 $("#fm2 input[name='type']").attr("disabled","disabled");
    								 $("#fm2 input[name='ipaddress']").attr("disabled","disabled");
    								 $("#fm2 input[name='port']").attr("disabled","disabled");
    								 $("#fm2 input[name='zaet']").attr("disabled","disabled");
    								 $("#fm2 input[name='baet']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbip']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbport']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbname']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbuser']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbpwd']").attr("disabled","disabled");
    								 //如果 其它两个form表单是显示的，则需要隐藏，并清空
    								 var display1 =$('#fm1').css('display');
    								 if(display1 != 'none'){
    									 var nod = {};node.text = "HIS";
    									 clearForm(nod);
    									 $('#fm1').hide();
    								 }
    								 var display3 =$('#fm3').css('display');
    								 if(display3 != 'none'){
    									 var nod = {};node.text = "VIDEO";
    									 clearForm(nod);
    									 $('#fm3').hide();
    								 }
    								 $("#linkbutton2").hide();
    								 $("#fm2").show();
    								 $("#submit2").attr("disabled",true);
    				 	 		}else if(node.text == "VIDEO"){
    				 	 			loadVideo(result);
    				 	 		}
    							
    							currentResourceId = result.id;
     							//加载列表
     							var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
   								$.get(path,function(treeData){
   									result.text = dataType;
   									loadtree(treeData,result,servername);
   								});    							 
    						}else{
    							layer.msg('查询无数据！', {icon: 7});
    						}
    	            },
				})
			}
	    }
	};
	
	function loadVideo(result,isEdit){
		$("#fm1").hide();
		$("#fm2").hide();
		$("#fm3").show();
		
		var data = getVideo(result);

		$("#fm3 input[name='roomname']").val(data.roomname);
		$("#fm3 input[name='roomip']").val(data.roomip);
		$("#fm3 select[name='roomtype']").val(data.roomtype);
		$('#videoDiv').html("");
		$('#videoTemplate').tmpl(data.video).appendTo('#videoDiv');
		$("#selectRoomId").find("option:contains('"+result.roomtype+"')").attr("selected",true);
		$("#selectRoomId").removeAttr("disabled");
		if(isEdit == "edit" || isEdit == "add"){
			if(isEdit == "add"){
 				$(".addVideo").click();
 			}
 	 	  	rmDisable({text:"VIDEO"});//移除表单不可编辑状态
 	 		$("#fm3 .operateVideo").show();
 	 		$("#linkbutton3").show();
 	 		$("#submit3").attr("disabled",false);
 	 		$("#selectRoomId").removeAttr("disabled");
 	 		//显示添加按钮
 	 		$(".addVideo").show();
 	 		$(".deleteVideo").on('click',deleteVideo);
		} else {
 			$("#fm3 input[name='roomname']").attr("disabled",true);
 			$("#fm3 input[name='roomip']").attr("disabled",true);
 			$("#fm3 select[name='roomtype']").attr("disabled",true);
 			$(".addVideo").hide();
 			$(".deleteVideo").hide();
 			
 	 		$("#fm3 .operateVideo").hide();
			$("#linkbutton3").hide();
			$("#submit3").attr("disabled",true);
		}
		var count = data.video.length;
		
	}
	$(".addVideo").on('click',function(){
		var arr = getVideoNotIndex();
		if(arr.length > 0){
 	 	  	rmDisable({text:"VIDEO"});//移除表单不可编辑状态
			$('#videoTemplate').tmpl([{index:arr[0]}]).appendTo('#videoDiv');
			$('#videoDiv input').removeAttr("disabled");
			$("#fm3 .operateVideo").show();
 	 		$(".deleteVideo").on('click',deleteVideo);
		} else {
			layer.msg('无法继续增加视频输入', {icon: 7});
		}
	});	
	function deleteVideo(){
		var arr = getVideoNotIndex();
		/*if(arr.length >= 7){
			layer.msg('至少要保留一个', {icon: 7});
 			return;
		} else {*/
			var thisDiv = $(this).parents(".layui-row").parent();
			//layer.confirm('您确认想要删除吗', {icon: 0}, function(index){
 	 			//layer.close(index);
 	 			thisDiv.remove();
 	 			/*count --;
			});
		}*/
	}
	function getVideoNotIndex(){
		var existIndex = [];
		for(var i=1;i<=8;i++){
			if($("#video"+i).html()){
				existIndex.push(i);
			}
		}
		var notIndex = [];
		for(var i=1;i<=8;i++){
			var exist = false;
			for(var j=0;j<existIndex.length;j++){
				if(existIndex[j]==i){
					exist = true;
					break;
				}
			}
			if(!exist){
				notIndex.push(i);
			}
		}
		if(notIndex.length > 0){
			return notIndex;
		}
		return notIndex;
	}
	//获取视频信息
	function getVideo(result){
		var arr = [];
		var index = 1;
		if(result.v1name){
			arr.push({vname:result.v1name,vadd:result.v1add,index:index});
			index ++;
		}
		if(result.v2name){
			arr.push({vname:result.v2name,vadd:result.v2add,index:index});
			index ++;
		}
		if(result.v3name){
			arr.push({vname:result.v3name,vadd:result.v3add,index:index});
			index ++;
		}
		if(result.v4name){
			arr.push({vname:result.v4name,vadd:result.v4add,index:index});
			index ++;
		}
		if(result.v5name){
			arr.push({vname:result.v5name,vadd:result.v5add,index:index});
			index ++;
		}
		if(result.v6name){
			arr.push({vname:result.v6name,vadd:result.v6add,index:index});
			index ++;
		}
		if(result.v7name){
			arr.push({vname:result.v7name,vadd:result.v7add,index:index});
			index ++;
		}
		if(result.v8name){
			arr.push({vname:result.v8name,vadd:result.v8add,index:index});
			index ++;
		}
		result.video = arr;
		return result;
	}
	var layuibody = document.querySelector('#layui-body')
	var layuicontent = document.querySelector('#user-right-content')
	var latuitool = document.querySelector('#layui-table-tool')
	var layuiTableBox = document.querySelector('#layui-table-box');
	layui.use('form', function(){
		
	});
	groupPower = $("#gropuname").val();
	$.get("/teleconsult/loadtree?currentId=" + $("#hiddenInput").val(),function(result){
		loadtree(result);
	});
	function loadtree(result,selectObj,keyWord){
		if(selectObj){
			result = proSelectTree(result,selectObj);
		}
		$('#tt').treeview({
		      color: "#666",
		      expandIcon: 'glyphicon glyphicon-chevron-right',
		      collapseIcon: 'glyphicon glyphicon-chevron-down',
		      nodeIcon: "glyphicon glyphicon-bookmark",
		      selectedBackColor:"#d4f9fb",
		      showBorder:false,
		      selectedColor:"#666",
		      showTags: true,
		      data: result,
		      onNodeSelected: function(event, data) {
		    	  currentResourceId = data.id;
		    	  searchRes();
		      }
		    });
		if(selectObj){
			searchRes();
		}
	}
	function proSelectTree(treeData,selectObj){
		var tree = JSON.parse(treeData);
		var servername = $("#searchLike").val().trim();
		for(var i=0;i<tree.length;i++){
			var nodes = tree[i].nodes;
			for(var j=0;j<nodes.length;j++){
				var child = nodes[j].nodes;
				var isExpanded = nodes[j].text==selectObj.text;
				if(nodes[j].text=='视频源' && selectObj.text=='VIDEO'){
					isExpanded = true;
				}
				for(var index=0;index<child.length;index++){
					if(child[index].text2 == selectObj.text && child[index].id == selectObj.id){
						isExpanded = true;
						var state = {selected: true};
						child[index].state = state;
					}
					if(servername && child[index].text.indexOf(servername) >= 0){
						isExpanded = true;
						child[index].text = child[index].text.replace(servername,"<font color='#06b4bf'>"+servername+"</font>");
					}
				}
				nodes[j].state = {expanded: isExpanded};
			}
		}
		return JSON.stringify(tree);
	}
 	//四个按钮之一 add
 	$("#addResource").click(function(){
 		if(groupPower == "admins" || groupPower == "managers"){
 			//$('#fm').form('clear');//清理表格数据，同时也清除了隐藏hidden标识数据,会导致提交问题
 	 		 type = $("#selectRes option:selected").val();//要增加的资源类型
 	 		 $("#hideId").val("add");
 	 		 var node = {};node.text = type;
 	 		 clearForm(node);//清空form表单
 	 		 rmDisable(node);//移除不可编辑状态
 	 		if(type == ""){
 	 			layer.msg('请选择您要增加的资源类型！', {icon: 7}); 
 	 		}else{
 	 			if(type == "HIS" || type == "LIS"){
 	 	 	 	 	//layuiTableBox.style.height = layuibody.offsetHeight - latuitool.offsetHeight -35 + 'px';
 	 				 var display2 =$('#fm2').css('display');
 	 				 if(display2 != 'none'){
 	 					 var nod = {};node.text = "RIS";//随便去了一个
 	 					 clearForm(nod);
 	 					 $('#fm2').hide();
 	 				 }
 	 				 var display3 =$('#fm3').css('display');
 	 				 if(display3 != 'none'){
 	 					 var nod = {};node.text = "VIDEO";
 	 					 clearForm(nod);
 	 					 $('#fm3').hide();
 	 				 }
 	 				 if($("#submit1").attr("disabled") == "disabled"){
 	 					$("#submit1").removeAttr("disabled");
 	 				 }
 	 				 $("#linkbutton1").show();
 	 				 $("#fm1").show();
 	 			}else if(type == "PACS" || type=="RIS"){
 	 	 	 	 	//layuiTableBox.style.height = layuibody.offsetHeight - latuitool.offsetHeight -35 + 'px';
 	 				var display1 =$('#fm1').css('display');
 	 				 if(display1 != 'none'){
 	 					 var nod = {};node.text = "HIS";//随便去了一个
 	 					 clearForm(nod);
 	 					 $('#fm1').hide();
 	 				 }
 	 				 var display3 =$('#fm3').css('display');
 	 				 if(display3 != 'none'){
 	 					 var nod = {};node.text = "VIDEO";
 	 					 clearForm(nod);
 	 					 $('#fm3').hide();
 	 				 }
 	 				if($("#submit2").attr("disabled") == "disabled"){
 	 					$("#submit2").removeAttr("disabled");
 	 				}
 	 				$("#linkbutton2").show();
 	 				$("#fm2").show();
 	 			}else if(type == "VIDEO"){
 	 				loadVideo({},"add");
 	 			}
 	 		}
 		}else{
 			layer.msg('您的权限不足,无法增加资源!！', {icon: 7}); 
 		}
 		 
 	})
 	//四个按钮之一 delete
 	$("#delResource").click(function(){
 		if(groupPower == "admins" || groupPower == "managers"){
 			var nodeAry = $('#tt').treeview('getSelected');//返回的是个数组
 			if(nodeAry.length != 0){
 				var node = {};node.text=nodeAry[0].text2;node.id=nodeAry[0].id;
 				if(node.text==undefined){
	 	 	 			layer.msg('非资源节点,请重新选择！', {icon: 7});
	 	 	 		}else{
	 	 	 			layer.confirm('您确认想要删除记录吗', {icon: 0}, function(index){
	 	 	 			  layer.close(index);
		 	 	 			var url = "";
		 	 	 			if(node.text == "HIS"){
		 	 	 				url = "/teleconsult/deleteHisResource?id=" + node.id;
		 	 	 			}else if(node.text == "LIS"){
		 	 	 				url = "/teleconsult/deleteLisResource?id=" + node.id;
		 	 	 			}else if(node.text == "PACS"){
		 	 	 				url = "/teleconsult/deletePacsResource?id=" + node.id;
		 	 	 			}else if(node.text == "RIS"){
		 	 	 				url = "/teleconsult/deleteRisResource?id=" + node.id;
		 	 	 			}else if(node.text == "VIDEO"){
		 	 	 				url = "/teleconsult/deleteVideoResource?id=" + node.id;
		 	 	 			}
		 	  	 			$.get(url,function(result){
		 							if (result == 1) {
		 								if(node.text == "HIS" || node.text == "LIS"){
		 									$("#fm1").hide();
		 								}else if(node.text == "PACS" || node.text == "RIS"){
		 									$("#fm2").hide();
		 								}else if(node.text == "VIDEO"){
		 									$("#fm3").hide();
		 								}
		 								var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
		 								$.get(path,function(result){
		 									layer.msg('数据删除成功');
		 									loadtree(result,{text:node.text});
		 								})
		 							} else {
		 								layer.msg('数据删除失败！', {icon: 7});
		 							}
		 	 	 			}) 
	 	 	 			});
	 	 	 		}
 			}else{
 				layer.msg('请选择您要删除的资源节点!', {icon: 7});
 			}
 	 	 	
 		}else{
 			layer.msg('您的权限不足,无法删除资源！', {icon: 7});
 		}
 	})
 	var isObjEmpty = function(result) {
						for(var key in result) {
							return false;
						}
						return true;
					}
 	//四个按钮之一  根据服务器名称查找
 	$("#searchResource").click(function(){
			var servername = $("#searchLike").val().trim();
			if(servername == ""){
				layer.msg('查询字段不能为空！', {icon: 7});
			}else{
				//var url = "/teleconsult/findResource?id="+$("#hiddenInput").val() +"&servername="+servername;
				//$.get 传到后台中文乱码
				$.ajax({
					 data: {
    	                "id":$("#hiddenInput").val(),
    	                "servername":servername
    	            }, 
    	            type: "post",
    	            contentType: "application/x-www-form-urlencoded;charset=UTF-8",//指定发送请求报文头 编码格式
    	            encoding: "UTF-8",
    	            url: "/teleconsult/findResource",
    	            success: function (data) {
    	            	var dataType = "";
    						console.log(data);
    						var result = JSON.parse(data);
    						if(!isObjEmpty(result)){
    							var node = {};
    							if(result.HIS){
    								node.text = "HIS";
    								result = result.HIS;
    							}else if(result.LIS){
    								node.text = "LIS";
    								result = result.LIS;
    							}else if(result.PACS){
    								node.text = "PACS";
    								result = result.PACS;
    							}else if(result.RIS){
    								node.text = "RIS";
    								result = result.RIS;
    							}else if(result.VIDEO){
    								node.text = "VIDEO";
    								result = result.VIDEO;
    							}
    							dataType = node.text;
    							//$('#tt').treeview('selectNode', [ nodeId, { silent: true } ]);
    							 if(node.text == "HIS" || node.text == "LIS"){
    								 //layuiTableBox.style.height = layuibody.offsetHeight - latuitool.offsetHeight -35 + 'px';
    								 $("#fm1 input[name='servername']").val(result.servername);
    								 $("#fm1 input[name='serverip']").val(result.serverip);
    								 $("#fm1 input[name='dbport']").val(result.dbport);
    								 $("#fm1 input[name='datesource']").val(result.datesource);
    								 $("#fm1 input[name='loginname']").val(result.loginname);
    								 $("#fm1 input[name='loginkey']").val(result.loginkey);
    								 $("#fm1 input[name='servername']").attr("disabled","disabled");
    								 $("#fm1 input[name='serverip']").attr("disabled","disabled");
    								 $("#fm1 input[name='dbport']").attr("disabled","disabled");
    								 $("#fm1 input[name='datesource']").attr("disabled","disabled");
    								 $("#fm1 input[name='loginkey']").attr("disabled","disabled");
    								 $("#fm1 input[name='loginname']").attr("disabled","disabled");
    								 //如果 其它两个form表单是显示的，则需要隐藏，并清空
    								 var display2 =$('#fm2').css('display');
    								 if(display2 != 'none'){
    									 var nod = {};node.text = "RIS";//随便去了一个
    									 clearForm(nod);
    									 $('#fm2').hide();
    								 }
    								 var display3 =$('#fm3').css('display');
    								 if(display3 != 'none'){
    									 var nod = {};node.text = "VIDEO";
    									 clearForm(nod);
    									 $('#fm3').hide();
    								 }
    								 $("#linkbutton1").hide();
    								 $("#fm1").show();
    								 $("#submit1").attr("disabled",true);
    				 	 		}else if(node.text == "PACS" || node.text == "RIS"){
    				 	 			 //layuiTableBox.style.height = layuibody.offsetHeight - latuitool.offsetHeight -35 + 'px';
    				 	 			 //$("#fm2 input[name='servername']").val(result.servername);
    								 $("#fm2 input[name='server']").val(result.server);
    								 $("#fm2 input[name='type']").val(result.type);
    								 $("#fm2 input[name='ipaddress']").val(result.ipaddress);
    								 $("#fm2 input[name='port']").val(result.port);
    								 $("#fm2 input[name='zaet']").val(result.zaet);
    								 $("#fm2 input[name='baet']").val(result.baet);
    								 $("#fm2 input[name='dbip']").val(result.dbip);
    								 $("#fm2 input[name='dbport']").val(result.dbport);
    								 $("#fm2 input[name='dbname']").val(result.dbname);
    								 $("#fm2 input[name='dbuser']").val(result.dbuser);
    								 $("#fm2 input[name='dbpwd']").val(result.dbpwd);
    								 //$("#fm2 input[name='servername']").attr("disabled","disabled");
    								 $("#fm2 input[name='server']").attr("disabled","disabled");
    								 $("#fm2 input[name='serverip']").attr("disabled","disabled");
    								 $("#fm2 input[name='type']").attr("disabled","disabled");
    								 $("#fm2 input[name='ipaddress']").attr("disabled","disabled");
    								 $("#fm2 input[name='port']").attr("disabled","disabled");
    								 $("#fm2 input[name='zaet']").attr("disabled","disabled");
    								 $("#fm2 input[name='baet']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbip']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbport']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbname']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbuser']").attr("disabled","disabled");
    								 $("#fm2 input[name='dbpwd']").attr("disabled","disabled");
    								 //如果 其它两个form表单是显示的，则需要隐藏，并清空
    								 var display1 =$('#fm1').css('display');
    								 if(display1 != 'none'){
    									 var nod = {};node.text = "HIS";
    									 clearForm(nod);
    									 $('#fm1').hide();
    								 }
    								 var display3 =$('#fm3').css('display');
    								 if(display3 != 'none'){
    									 var nod = {};node.text = "VIDEO";
    									 clearForm(nod);
    									 $('#fm3').hide();
    								 }
    								 $("#linkbutton2").hide();
    								 $("#fm2").show();
    								 $("#submit2").attr("disabled",true);
    				 	 		}else if(node.text == "VIDEO"){
    				 	 			loadVideo(result);
    				 	 		}
    							currentResourceId = result.id;
    							//加载列表
    							var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
   								$.get(path,function(treeData){
   									result.text = dataType;
   									loadtree(treeData,result,servername);
   								}) 
    						}else{
    							layer.msg('查询无数据！', {icon: 7});
    						}
    	            },
				})
			}
 	})
 	//选择节点 展示详情
 	function searchRes(){
 		var nodeAry = $('#tt').treeview('getSelected');//返回的是个数组
		var node = {};node.text=nodeAry[0].text2;node.id=nodeAry[0].id;
	 	if(node.text == undefined){
	 		// do nothing
	 	}else{
	 		var url = "";
			if(node.text == "HIS"){
				url = "/teleconsult/searchHisResource?id=" + node.id;
			}else if(node.text == "LIS"){
				url = "/teleconsult/searchLisResource?id=" + node.id;
			}else if(node.text == "PACS"){
				url = "/teleconsult/searchPacsResource?id=" + node.id;
			}else if(node.text == "RIS"){
				url = "/teleconsult/searchRisResource?id=" + node.id;
			}else if(node.text == "VIDEO"){
				url = "/teleconsult/searchVideoResource?id=" + node.id;
			}
			$.get(url,function(data){
				if (data != null) {
					 var result = JSON.parse(data)[0];
					 if(node.text == "HIS" || node.text == "LIS"){
					 	 //layuiTableBox.style.height = layuibody.offsetHeight - latuitool.offsetHeight -35 + 'px';
						 $("#fm1 input[name='servername']").val(result.servername);
						 $("#fm1 input[name='serverip']").val(result.serverip);
						 $("#fm1 input[name='dbport']").val(result.dbport);
						 $("#fm1 input[name='datesource']").val(result.datesource);
						 $("#fm1 input[name='loginname']").val(result.loginname);
						 $("#fm1 input[name='loginkey']").val(result.loginkey);
						 $("#fm1 input[name='servername']").attr("disabled","disabled");
						 $("#fm1 input[name='serverip']").attr("disabled","disabled");
						 $("#fm1 input[name='dbport']").attr("disabled","disabled");
						 $("#fm1 input[name='datesource']").attr("disabled","disabled");
						 $("#fm1 input[name='loginkey']").attr("disabled","disabled");
						 $("#fm1 input[name='loginname']").attr("disabled","disabled");
						 //如果 其它两个form表单是显示的，则需要隐藏，并清空
						 var display2 =$('#fm2').css('display');
						 if(display2 != 'none'){
							 var nod = {};node.text = "RIS";//随便去了一个
							 clearForm(nod);
							 $('#fm2').hide();
						 }
						 var display3 =$('#fm3').css('display');
						 if(display3 != 'none'){
							 var nod = {};node.text = "VIDEO";
							 clearForm(nod);
							 $('#fm3').hide();
						 }
						 $("#fm1").show();
						 $("#linkbutton1").hide();
						 $("#submit1").attr("disabled",true);
		 	 		}else if(node.text == "PACS" || node.text == "RIS"){
				 	 	 //layuiTableBox.style.height = layuibody.offsetHeight - latuitool.offsetHeight -35 + 'px';
		 	 			// $("#fm2 input[name='servername']").val(result.servername);
						 $("#fm2 input[name='server']").val(result.server);
						 $("#fm2 input[name='type']").val(result.type);
						 $("#fm2 input[name='ipaddress']").val(result.ipaddress);
						 $("#fm2 input[name='port']").val(result.port);
						 $("#fm2 input[name='zaet']").val(result.zaet);
						 $("#fm2 input[name='baet']").val(result.baet);
						 $("#fm2 input[name='dbip']").val(result.dbip);
						 $("#fm2 input[name='dbport']").val(result.dbport);
						 $("#fm2 input[name='dbname']").val(result.dbname);
						 $("#fm2 input[name='dbuser']").val(result.dbuser);
						 $("#fm2 input[name='dbpwd']").val(result.dbpwd);
						 //$("#fm2 input[name='servername']").attr("disabled","disabled");
						 $("#fm2 input[name='server']").attr("disabled","disabled");
						 $("#fm2 input[name='serverip']").attr("disabled","disabled");
						 $("#fm2 input[name='type']").attr("disabled","disabled");
						 $("#fm2 input[name='ipaddress']").attr("disabled","disabled");
						 $("#fm2 input[name='port']").attr("disabled","disabled");
						 $("#fm2 input[name='zaet']").attr("disabled","disabled");
						 $("#fm2 input[name='baet']").attr("disabled","disabled");
						 $("#fm2 input[name='dbip']").attr("disabled","disabled");
						 $("#fm2 input[name='dbport']").attr("disabled","disabled");
						 $("#fm2 input[name='dbname']").attr("disabled","disabled");
						 $("#fm2 input[name='dbuser']").attr("disabled","disabled");
						 $("#fm2 input[name='dbpwd']").attr("disabled","disabled");
						 //如果 其它两个form表单是显示的，则需要隐藏，并清空
						 var display1 =$('#fm1').css('display');
						 if(display1 != 'none'){
							 var nod = {};node.text = "HIS";
							 clearForm(nod);
							 $('#fm1').hide();
						 }
						 var display3 =$('#fm3').css('display');
						 if(display3 != 'none'){
							 var nod = {};node.text = "VIDEO";
							 clearForm(nod);
							 $('#fm3').hide();
						 }
						 $("#linkbutton2").hide();
						 $("#fm2").show();
						 $("#submit2").attr("disabled",true);
		 	 		}else if(node.text == "VIDEO"){
		 	 			loadVideo(result);
		 	 		}
				} 
			})
	 	}
			
 	}
 	
 	//四个按钮之一 update 
  	$("#updateResource").click(function(){
  		if(groupPower == "admins" || groupPower == "managers"){
  			var nodeAry = $('#tt').treeview('getSelected');//返回的是个数组
  			if(nodeAry.length != 0){
  				var node = {};node.text=nodeAry[0].text2;node.id=nodeAry[0].id;
  				if (node){
  	  	 	 		if(node.text == undefined){
  	  	 	 			layer.msg('非资源节点,请重新选择！', {icon: 7});
  	  	 	 		}else{
  	  	 	 			node.text = node.text.split("_")[0];
  	  	 	 			clearForm(node);//修改之前先清空数据
  	  	 	 	  		$("#hideId").val("modify");//表示为修改操作
  	  	 	 	  		rmDisable(node);//移除表单不可编辑状态
  	  	 	 	  		var url = "";
  	  	 	 	  		if(node.text == "HIS"){
  	  	 	 	  			$("#fm1").attr("title","修改HIS表单");
  	  	 	 	  			url = "/teleconsult/searchHisResource?id=" + node.id;
  	  	 	 	  		}else if(node.text == "LIS"){
  	  	 	 	  			$("#fm1").attr("title","修改LIS表单");
  	  	 	 	  			url = "/teleconsult/searchLisResource?id=" + node.id;
  	  	 	 	  		}else if(node.text == "PACS"){
  	  	 	 	  			$("#fm2").attr("title","修改PACS表单");
  	  	 	 	  			url = "/teleconsult/searchPacsResource?id=" + node.id;
  	  	 	 	  		}else if(node.text == "RIS"){
  	  	 	 	  			$("#fm2").attr("title","修改RIS表单");
  	  	 	 	  			url = "/teleconsult/searchRisResource?id=" + node.id;
  	  	 	 	  		}else if(node.text == "VIDEO"){
  	  	 	 	  			$("#fm3").attr("title","修改VIDEO表单");
  	  	 	 	  			url = "/teleconsult/searchVideoResource?id=" + node.id;
  	  	 	 	  		}
  	  	 	 			$.get(url,function(data){
  	  	 	 				var result = JSON.parse(data)[0];
  	  	 	 				if(result.length != 0){
  	  		 	 				if(node.text == "HIS" || node.text == "LIS"){
						 	 		//layuiTableBox.style.height = layuibody.offsetHeight - latuitool.offsetHeight -35 + 'px';
  	  		 	 					 $("#fm1 input[name='servername']").val(result.servername);
  	  								 $("#fm1 input[name='serverip']").val(result.serverip);
  	  								 $("#fm1 input[name='dbport']").val(result.dbport);
  	  								 $("#fm1 input[name='datesource']").val(result.datesource);
  	  								 $("#fm1 input[name='loginname']").val(result.loginname);
  	  								 $("#fm1 input[name='loginkey']").val(result.loginkey);
  	  								 $("#linkbutton1").show();
  	  								 $("#fm1").show();
  	  								 if($("#submit1").attr("disabled") == "disabled"){
  	  					 					$("#submit1").removeAttr("disabled");
  	  					 			 }
  	  								 if($("#fm2").css("display")=="block"){
  	  									$("#fm2").hide();
  	  	 	 						 }
  	  								 if($("#fm3").css("display")=="block"){
  	  									$("#fm3").hide();
  	  	 	 						 } 
  	  								 //移除确定按钮不可编辑
  	  		 	 	 	  		}else if(node.text == "PACS" || node.text == "RIS"){
					 	 		//layuiTableBox.style.height = layuibody.offsetHeight - latuitool.offsetHeight -35 + 'px';
  	  				 	 			 //$("#fm2 input[name='servername']").val(result.servername);
  	  								 $("#fm2 input[name='server']").val(result.server);
  	  								 $("#fm2 input[name='type']").val(result.type);
  	  								 $("#fm2 input[name='ipaddress']").val(result.ipaddress);
  	  								 $("#fm2 input[name='port']").val(result.port);
  	  								 $("#fm2 input[name='zaet']").val(result.zaet);
		 	  						 $("#fm2 input[name='baet']").val(result.baet);
		 	  						 $("#fm2 input[name='dbip']").val(result.dbip);
		 	  						 $("#fm2 input[name='dbport']").val(result.dbport);
		 	  						 $("#fm2 input[name='dbname']").val(result.dbname);
		 	  						 $("#fm2 input[name='dbuser']").val(result.dbuser);
		 	  						 $("#fm2 input[name='dbpwd']").val(result.dbpwd);
  	  								 $("#linkbutton2").show();
  	  								 $("#fm2").show();
  	  								 if($("#submit2").attr("disabled") == "disabled"){
  	  					 					$("#submit2").removeAttr("disabled");
  	  					 			 }
  	  							     if($("#fm1").css("display")=="block"){
	  									$("#fm1").hide();
	  	 	 						 }
	  								 if($("#fm3").css("display")=="block"){
	  									$("#fm3").hide();
	  	 	 						 } 
  	  		 	 	 	  		}else if(node.text == "VIDEO"){
  	  		 	 	 	  			loadVideo(result,"edit");
  	  		 	 	 	  		}
  	  	 	 				}
  	  							
  	  	 	 			})
  	  	 	 		}
  	  	 	 	}
  			}else{
  	  	 	 	layer.msg('请选择您要修改的资源节点！', {icon: 7});
  			}
  		}else{
  			layer.msg('您的权限不足,无法修改资源!', {icon: 7});
  		}
 	})

 	//提交HIS 与 LIS资源   add/updata
 	$("#submit1").click(function(){
 		var hideVal = $("#hideId").val();
 		if(hideVal == "add"){
 			addHisOrLis();
 		}else if(hideVal == "modify"){
 			updateHisOrLis();
 		}
 	})
 	//提交PACS 与 RIS资源   add/updata
 	$("#submit2").click(function(){
 		var hideVal = $("#hideId").val();
 		if(hideVal == "add"){
 			addPacsOrRis();
 		}else if(hideVal == "modify"){
 			updatePacsOrRis();
 		}
 	})
 	//提交  Video资源   add/updata
 	$("#submit3").click(function(){
 		var hideVal = $("#hideId").val();
 		if(hideVal == "add"){
 			addVideo();
 		}else if(hideVal == "modify"){
 			updateVideo();
 		}
 	})
 	//点击提交按钮 增加His/Lis
 	function addHisOrLis() {
 		var url = "";
 		var bool = checkForm("fm1");
		if(bool){
			if(type == "HIS"){
				url= "/teleconsult/insertHisResource";
			}else if(type == "LIS"){
				url= "/teleconsult/insertLisResource";
			}
			$('#fm1').form('submit', {
					url : url,
					success : function(result) {
						var data = JSON.parse(result);
						if (data.success == 1) {
							var node = {};node.text = "HIS";clearForm(node);
							$("#fm1").hide();
							var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
								$.get(path,function(result){
									layer.msg('数据增加成功');
									loadtree(result,{id:data.id,text:type});
								})
						} else if(data.success == 2){
							layer.msg('数据已存在！', {icon: 7});
						} else {
							layer.msg('数据增加失败！', {icon: 7});
						}
					},
					onLoadError : function() {
						alert("表单数据加载失败");
					}
				});
		}
 	}
 	//点击提交按钮 增加Pacs/Ris  
 	function addPacsOrRis() {
 		var url = "";
 		var bool = checkForm("fm2");
		if(bool){
			if(type == "PACS"){
				url= "/teleconsult/insertPacsResource";
			}else if(type == "RIS"){
				url= "/teleconsult/insertRisResource";
			}
			$('#fm2').form('submit', {
					url : url,
					success : function(result) {
						var data = JSON.parse(result);
						if (data.success == 1) {
							$("#fm2").hide();
							var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
							$.get(path,function(result){
								layer.msg('数据增加成功');
								loadtree(result,{id:data.id,text:type});
							}) 
						} else if(data.success == 2){
							layer.msg('数据已存在！', {icon: 7});
						} else {
							layer.msg('数据增加失败！', {icon: 7});
						}
					},
					onLoadError : function() {
						alert("表单数据加载失败");
					}
				});
		}
 	}
 	//点击提交按钮 增加Video 
 	function addVideo() {
		type = $("#selectRoomId option:selected").text();//要增加的资源类型
		$("#roomtypeId").val(type);
 		var url = "/teleconsult/insertVideoResource";
 		var bool = checkForm("fm3");
		if(bool){
			$.ajax({
				 data: {
						"orgid": $("#fm3 input[name='orgid']").val(),
						//"vname" : $("#fm3 input[name='vname']").val(),
						"roomname" : $("#fm3 input[name='roomname']").val(),
						"roomip" : $("#fm3 input[name='roomip']").val(),
						"roomtype" : $("#fm3 #selectRoomId").val(),
						
						"v1name" : $("#fm3 input[name='v1name']").val(),
						"v1add" : $("#fm3 input[name='v1add']").val(),
						"v2name" : $("#fm3 input[name='v2name']").val(),
						"v2add" : $("#fm3 input[name='v2add']").val(),
						"v3name" : $("#fm3 input[name='v3name']").val(),
						"v3add" : $("#fm3 input[name='v3add']").val(),
						"v4name" : $("#fm3 input[name='v4name']").val(),
						"v4add" : $("#fm3 input[name='v4add']").val(),
						"v5name" : $("#fm3 input[name='v5name']").val(),
						"v5add" : $("#fm3 input[name='v5add']").val(),
						"v6name" : $("#fm3 input[name='v6name']").val(),
						"v6add" : $("#fm3 input[name='v6add']").val(),
						"v7name" : $("#fm3 input[name='v7name']").val(),
						"v7add" : $("#fm3 input[name='v7add']").val(),
						"v8name" : $("#fm3 input[name='v8name']").val(),
						"v8add" : $("#fm3 input[name='v8add']").val(),
	            }, 
	            type: "post",
	            contentType: "application/x-www-form-urlencoded;charset=UTF-8",//指定发送请求报文头 编码格式
	            encoding: "UTF-8",
	            url: url,
	            success: function (result) {
	            	var data = JSON.parse(result);
					if (data.success == 1) {
	            		$("#fm3").hide();
						var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
						$.get(path,function(result){
							layer.msg('数据增加成功');
							loadtree(result,{id:data.id,text:"VIDEO"});
						})  
					} else if(data.success == 2){
						layer.msg('数据已存在！', {icon: 7});
					} else {
						layer.msg('数据增加失败！', {icon: 7});
					}
	            }
	       })
		}
			
 	}
 	//点击提交按钮 修改His /LIS  
  	function updateHisOrLis() {
 		var url = "";
 		var type = "";
		if($("#fm1").attr("title") == "修改HIS表单"){//执行的是HIS修改操作  LIS资源   
			url = "/teleconsult/updateHisResource?id="+currentResourceId ;
			type = "HIS";
		}else if($("#fm1").attr("title") == "修改LIS表单"){
			url = "/teleconsult/updateLisResource?id="+currentResourceId;
			type = "LIS";
		}
		var bool = checkForm("fm1");
		if (bool) {
			$('#fm1').form('submit', {
				url : url,
				success : function(result) {
					var data = JSON.parse(result);
					if (data.success == 1) {
						$("#fm1").attr("title","");
						$("#fm1").hide();
						var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
						$.get(path,function(result){
							layer.msg('数据修改成功');
							loadtree(result,{id:currentResourceId,text:type});
						})   
					} else if(data.success == 2){
						layer.msg('数据已存在！', {icon: 7});
					} else {
						layer.msg('数据修改失败！', {icon: 7});
					}
				}
			});
		}
 	} 
 	//点击提交按钮 修改Pacs  Ris  
  	function updatePacsOrRis() {
 		var url = "";									//修改PACS资源              修改RIS资源
 		var type = "";
   		//var dlgTitle = $('#dlg2').panel('options').title;//要么做更新PACS操作，要么做更细RIS操作
 		if($("#fm2").attr("title") == "修改PACS表单"){//执行的是HIS修改操作  LIS资源   
			url = "/teleconsult/updatePacsResource?id="+currentResourceId ;
			type = "PACS";
		}else if($("#fm2").attr("title") == "修改RIS表单"){
			url = "/teleconsult/updateRisResource?id="+currentResourceId;
			type = "RIS";
		}
		var bool = checkForm("fm2");
		if(bool){
			$('#fm2').form('submit', {
					url : url,
					success : function(result) {
						var data = JSON.parse(result);
						if (data.success == 1) {
							$("#fm2").attr("title","");
	 						$("#fm2").hide();
							var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
							$.get(path,function(result){
								layer.msg('数据修改成功');
								loadtree(result,{id:currentResourceId,text:type});
							})   
						} else if(data.success == 2){
							layer.msg('数据已存在！', {icon: 7});
						} else {
							layer.msg('数据修改失败！', {icon: 7});
						}
					},
					onLoadError : function() {
						alert("表单数据加载失败");
					}
				});
		}
 	} 
 	//点击提交按钮 修改VIDEO 
 	function updateVideo() {
 		var url = "/teleconsult/updateVideoResource?id="+currentResourceId ;
		var bool = checkForm("fm3");
		if(bool){
			var data = {
				"orgid": $("#fm3 input[name='orgid']").val(),
				//"vname" : $("#fm3 input[name='vname']").val(),
				"roomname" : $("#fm3 input[name='roomname']").val(),
				"roomip" : $("#fm3 input[name='roomip']").val(),
				"roomtype" : $("#fm3 #selectRoomId").val(),
				"v1name" : $("#fm3 input[name='v1name']").val(),
				"v1add" : $("#fm3 input[name='v1add']").val(),
				"v2name" : $("#fm3 input[name='v2name']").val(),
				"v2add" : $("#fm3 input[name='v2add']").val(),
				"v3name" : $("#fm3 input[name='v3name']").val(),
				"v3add" : $("#fm3 input[name='v3add']").val(),
				"v4name" : $("#fm3 input[name='v4name']").val(),
				"v4add" : $("#fm3 input[name='v4add']").val(),
				"v5name" : $("#fm3 input[name='v5name']").val(),
				"v5add" : $("#fm3 input[name='v5add']").val(),
				"v6name" : $("#fm3 input[name='v6name']").val(),
				"v6add" : $("#fm3 input[name='v6add']").val(),
				"v7name" : $("#fm3 input[name='v7name']").val(),
				"v7add" : $("#fm3 input[name='v7add']").val(),
				"v8name" : $("#fm3 input[name='v8name']").val(),
				"v8add" : $("#fm3 input[name='v8add']").val(),
        };
			//不通过form表单提交（乱码到后台解析出错）
			$.ajax({
					 data: data, 
    	            type: "post",
    	            contentType: "application/x-www-form-urlencoded;charset=UTF-8",//指定发送请求报文头 编码格式
    	            encoding: "UTF-8",
    	            url: url,
    	            success: function (result) {
						var data = JSON.parse(result);
						if (data.success == 1) {
	    	            	$("#fm3").attr("title","");
	 						$("#fm3").hide();
							var path = "/teleconsult/loadtree?currentId=" + $("#hiddenInput").val();
							$.get(path,function(result){
								layer.msg('数据修改成功');
								loadtree(result,{id:currentResourceId,text:"VIDEO"});
							})   
						} else if(data.success == 2){
							layer.msg('数据已存在！', {icon: 7});
						} else {
							layer.msg('数据修改失败！', {icon: 7});
						}
    	            }
    	       })
		}
 	}
 	
 	$("#cancel1").click(function(){
 		var node = {};
 		node.text = "HIS";
 		clearForm(node);
 		$("#fm1").hide();
 		searchRes();
 	});
 	$("#cancel2").click(function(){
 		var node = {};
 		node.text = "PACS";
 		clearForm(node);
 		$("#fm2").hide();
 		searchRes();
 	});
 	$("#cancel3").click(function(){
 		var node = {};
 		node.text = "VIDEO";
 		clearForm(node);
 		$("#fm3").hide();
 		searchRes();
 	});
 	$("#delete1").click(function(){
 		var id = this.id.substring(6);
 		if(id == "1"){
 			layer.msg('至少要保留一个', {icon: 7});
 			return;
 		}
 		$(".trclass" + id).remove();
 		searchRes();
 	});
 	
 	function swit(rowNum){
 		switch (rowNum) {
		case 4:
			return	1;
		case 6:
			return	2;
		case 8:
			return	3;
		case 10:
			return	4;
		case 12:
			return	5;
		case 14:
			return	6;
		case 16:
			return	7;
		case 18:
			return	8;
		default:
			break;
		}
 	}
 	//id:fm表单id 
 	function checkForm(id){
 		var myform = document.getElementById(id);   //获得form表单对象
 		var pattern = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;//ip校验正则
 		var pattern2 = /^[0-9]*$/;
 		var p1 = $("#serverIp1").val();
 		var p2 = $("#serverIp2").val();
 		var p3 = $("#serverIp3").val();
 		var chineseP = /[\u4e00-\u9fa5]/;
 		var form3_voneipadd = $("#form3_voneipadd").val();
 		var form3_veightipadd = $("#form3_veightipadd").val();
 		var pat=/[@#\$%\^&\*]+/g; //循环form表单 不能输入！@#￥%……&* 
 		if(id == "fm1"){
 			if(!pattern.test(p1)){
 				layer.msg($("#serverIp1").attr("title") + "校验不通过,请检查!", {icon: 5});
 				//layer.tips("ip地址校验不通过,请检查!", '#serverIp1', {tips: [2,'#0FA6D8']});
 				return false;
 			}
 			for(var i=0;i<myform.length;i++){ 
	 			if($("#"+myform.elements[i].id).attr("type") != "password" && pat.test(myform.elements[i].value)){
	 				//所有不能包含特殊字符
	 				layer.msg($("#"+myform.elements[i].id).attr("title") +"含有非法字符!", {icon: 5});
            		return false;   	 
	 			}
 			} 			
 		}
 		if(id == "fm2"){
 			if(!pattern.test(p2)){
 				layer.msg($("#serverIp2").attr("title") + "校验不通过,请检查!", {icon: 5});
 				//layer.tips("ip地址校验不通过,请检查!", '#serverIp2', {tips: [2,'#0FA6D8']});
 				return false;
 			} 
 			if(!pattern2.test($("#port2").val())){
 				layer.msg($("#port2").attr("title") + "校验不通过,请检查!", {icon: 5});
 				//layer.tips("ip地址校验不通过,请检查!", '#serverIp2', {tips: [2,'#0FA6D8']});
 				return false;
 			}
 			//所有不能包含特殊字符
 			for(var i=0;i<myform.length;i++){ 
 	              if($("#"+myform.elements[i].id).attr("type") != "password" && pat.test(myform.elements[i].value)){
 	            	 layer.msg($("#"+myform.elements[i].id).attr("title") +"含有非法字符!", {icon: 5});
                		return false;
 	              }
 	        }
 			
 		}
 		if(id == "fm3"){
 			for(var i=0;i<myform.length;i++){ 
 				if($("#"+myform.elements[i].id).attr("id") != "form3_v1ipadd" &&
 						$("#"+myform.elements[i].id).attr("id") != "form3_v1add" && 
 						$("#"+myform.elements[i].id).attr("id") != "form3_v2add" && 
 						$("#"+myform.elements[i].id).attr("id") != "form3_v3add" && 
 						$("#"+myform.elements[i].id).attr("id") != "form3_v4add" && 
 						$("#"+myform.elements[i].id).attr("id") != "form3_v5add" && 
 						$("#"+myform.elements[i].id).attr("id") != "form3_v6add" && 
 						$("#"+myform.elements[i].id).attr("id") != "form3_v7add" && 
 						$("#"+myform.elements[i].id).attr("id") != "form3_v8add"){
	              if(pat.test(myform.elements[i].value)){
	              	layer.msg($("#"+myform.elements[i].id).attr("title") +"含有非法字符!", {icon: 5});
              		return false;
	              }
        	  	}
	        }
 		}
        for(var i=0;i<myform.length;i++){ 
              if(myform.elements[i].value==""){
            	 if(myform.elements[i].id != "selectRoomId"){
            		 if(myform.elements[i].id == "form3_vonename" || myform.elements[i].id == "form3_voneipadd"){
            			console.log("视频源1为空，不做校验");
            		 }else if(myform.elements[i].id == "form3_veightname" || myform.elements[i].id == "form3_veightipadd"){ 
            			 console.log("视频源2为空，不做校验");
            		 }else{
	      				layer.msg($("#"+myform.elements[i].id).attr("title") + "不能为空!", {icon: 5});
            			 myform.elements[i].focus();       
                         return false;
            		 }
            	  }else{
            		 layer.msg("资源类型不能为空!", {icon: 5});
            		 return false;
              	  	//layer.tips("资源类型不能为空!", '#selectRoomId2', {tips: [2,'#0FA6D8']});
            	  } 
              }
              if(pat.test(myform.elements[i].value)){
            	  	if($("#"+myform.elements[i].id).attr("id") != "form3_v1ipadd" || $("#"+myform.elements[i].id).attr("id") != "form3_v2name" || $("#"+myform.elements[i].id).attr("id") != "form3_v3name"
            	  		|| $("#"+myform.elements[i].id).attr("id") != "form3_v4name"|| $("#"+myform.elements[i].id).attr("id") != "form3_v5name"|| $("#"+myform.elements[i].id).attr("id") != "form3_v6name"
            	  			|| $("#"+myform.elements[i].id).attr("id") != "form3_v7name"|| $("#"+myform.elements[i].id).attr("id") != "form3_v8name"){
            	  		
            	  	}else{
	    				layer.msg($("#"+myform.elements[i].id).attr("title") +"含有非法字符!", {icon: 5});
	                    myform.elements[i].focus();             
                   		return false;
            	  	}
              }
        }
 		return true;
 	}
 	
 	//清除表单内容
 	function clearForm(node){
 		if(node.text == "HIS" || node.text == "LIS"){
	 		 $("#fm1 input[name='servername']").val("");
			 $("#fm1 input[name='serverip']").val("");
			 $("#fm1 input[name='dbport']").val("");
			 $("#fm1 input[name='datesource']").val("");
			 $("#fm1 input[name='loginname']").val("");
			 $("#fm1 input[name='loginkey']").val("");
 		}else if(node.text == "PACS" || node.text == "RIS"){
	 		 //$("#fm2 input[name='servername']").val("");
			 $("#fm2 input[name='server']").val("");
			 $("#fm2 input[name='type']").val("");
			 $("#fm2 input[name='ipaddress']").val("");
			 $("#fm2 input[name='port']").val("");
			 $("#fm2 input[name='zaet']").val("");
			 $("#fm2 input[name='baet']").val("");
			 $("#fm2 input[name='dbip']").val("");
			 $("#fm2 input[name='dbport']").val("");
			 $("#fm2 input[name='dbname']").val("");
			 $("#fm2 input[name='dbuser']").val("");
			 $("#fm2 input[name='dbpwd']").val("");
 		}else if(node.text == "VIDEO"){
 	 		 //$("#fm3 input[name='vname']").val("");
 			 $("#fm3 input[name='roomname']").val("");
 			 $("#fm3 input[name='roomip']").val("");
 			 $("#fm3 input[name='roomtype']").val("");
 			 $("#fm3 input[name='v1name']").val("");
 			 $("#fm3 input[name='v1add']").val("");
 			 $("#fm3 input[name='v2name']").val("");
 			 $("#fm3 input[name='v2add']").val("");
 			 $("#fm3 input[name='v3name']").val("");
 			 $("#fm3 input[name='v3add']").val("");
 			 $("#fm3 input[name='v4name']").val("");
 			 $("#fm3 input[name='v4add']").val("");
 			 $("#fm3 input[name='v5name']").val("");
 			 $("#fm3 input[name='v5add']").val("");
 			 $("#fm3 input[name='v6name']").val("");
 			 $("#fm3 input[name='v6add']").val("");
 			 $("#fm3 input[name='v7name']").val("");
 			 $("#fm3 input[name='v7add']").val("");
 			 $("#fm3 input[name='v8name']").val("");
 			 $("#fm3 input[name='v8add']").val("");
 		}
 	}
 	//移除表单不可编辑状态
 	function rmDisable(node){
 		if(node.text == "HIS" || node.text == "LIS"){
 			$("#fm1 input[name='servername']").removeAttr("disabled");
 			$("#fm1 input[name='serverip']").removeAttr("disabled");
 			$("#fm1 input[name='dbport']").removeAttr("disabled");
 			$("#fm1 input[name='datesource']").removeAttr("disabled");
 			$("#fm1 input[name='loginkey']").removeAttr("disabled");
 			$("#fm1 input[name='loginname']").removeAttr("disabled");	
 		}else if(node.text == "PACS" || node.text == "RIS"){
 			//$("#fm2 input[name='servername']").removeAttr("disabled");
 			$("#fm2 input[name='server']").removeAttr("disabled");
 			$("#fm2 input[name='type']").removeAttr("disabled");
 			$("#fm2 input[name='ipaddress']").removeAttr("disabled");
 			$("#fm2 input[name='port']").removeAttr("disabled");
 			$("#fm2 input[name='zaet']").removeAttr("disabled");
 			$("#fm2 input[name='baet']").removeAttr("disabled");
 			$("#fm2 input[name='dbip']").removeAttr("disabled");
 			$("#fm2 input[name='dbport']").removeAttr("disabled");
 			$("#fm2 input[name='dbname']").removeAttr("disabled");
 			$("#fm2 input[name='dbuser']").removeAttr("disabled");
 			$("#fm2 input[name='dbpwd']").removeAttr("disabled");
 		}else if(node.text == "VIDEO"){
 			//$("#fm3 input[name='vname']").removeAttr("disabled");
 			$("#fm3 input[name='roomname']").removeAttr("disabled");
 			$("#fm3 input[name='roomip']").removeAttr("disabled");
 			$("#fm3 input[name='roomtype']").removeAttr("disabled");
 			$("#fm3 input[name='v1name']").removeAttr("disabled");	
 			$("#fm3 input[name='v1add']").removeAttr("disabled");	
 			$("#fm3 input[name='v2name']").removeAttr("disabled");	
 			$("#fm3 input[name='v2add']").removeAttr("disabled");	
 			$("#fm3 input[name='v3name']").removeAttr("disabled");	
 			$("#fm3 input[name='v3add']").removeAttr("disabled");	
 			$("#fm3 input[name='v4name']").removeAttr("disabled");	
 			$("#fm3 input[name='v4add']").removeAttr("disabled");	
 			$("#fm3 input[name='v5name']").removeAttr("disabled");	
 			$("#fm3 input[name='v5add']").removeAttr("disabled");	
 			$("#fm3 input[name='v6name']").removeAttr("disabled");	
 			$("#fm3 input[name='v6add']").removeAttr("disabled");	
 			$("#fm3 input[name='v7name']").removeAttr("disabled");	
 			$("#fm3 input[name='v7add']").removeAttr("disabled");	
 			$("#fm3 input[name='v8name']").removeAttr("disabled");	
 			$("#fm3 input[name='v8add']").removeAttr("disabled");	
 		}				
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