<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	Object msg = request.getSession().getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="../layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="../css/common.css"><!-- 自定义css-->
  <link href="https://cdn.bootcss.com/twitter-bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet"><!-- bootstrap.css-->
</head>
<body>
<!-- 左侧 -->
<div class="layui-side layui-bg-black" style="width: 205px;border-right: 1px solid #ddd;">
  <div class="layui-side-scroll" style="background: #fff;width: 225px">
    <div id="hospitalTree" class="tree-hospital" style="width: 205px;margin-top: 15px;"></div>
  </div>
</div>
<!-- 右侧 -->
<div class="layui-body" style="background:#f5f5f5;left: 205px">
  <div class="user-right-content">
    <div class="layui-form tc-tools">
      <div class="layui-inline" style="width: 100px;display: none;">
        <select id="group" lay-verify="">
          <option value="">用户类型</option>
          <c:forEach var="item" items="${form.group.items}">
          <option value="${item.key}">${item.groupname}</option>
          </c:forEach>
        </select> 
      </div>   
      <div class="layui-inline tc-search">
        <input type="" id="seachValue" value="${form.user.value}" placeholder="搜索" class="layui-input">
        <button id="searchBtn" data-type="reload" class="layui-btn layui-btn-sm"><i class="layui-icon layui-icon-search"></i></button>
      </div>   
      <div class="layui-inline" style="display: none;">
        <input type="checkbox" id="currentOrgan" lay-filter="currentOrgan" value="" title="只显示当前机构用户" lay-skin="primary"> 
      </div>
      <input type="hidden" id="showCurrentOrgan" value="0"/>
      <input type="hidden" id="organ" value="0"/>
    </div>
    <table class="layui-hide" id="layTable" lay-filter="layTable"></table>
  </div>
</div>
<!-- 表格工具栏 -->
<script type="text/html" id="toolbar">
  <div class="layui-btn-container">
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="add"><i class="layui-icon layui-icon-add-circle"></i>新建用户</button>
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除用户</button>
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="edit"><i class="layui-icon layui-icon-edit"></i>修改用户</button>
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="downImportTemplate"><i class="layui-icon layui-icon-down"></i>模板下载</button>
    <button class="layui-btn layui-btn-sm layui-btn-radius tc-bg-main" lay-event="import"><i class="layui-icon layui-icon-template"></i>批量导入</button>
</div>
</script>

<script src="https://cdn.bootcss.com/jquery/3.0.0/jquery.min.js"></script><!-- jquery.js -->
<script src="../layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="../js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<script>
function getSons(node){
	var nodes=[];
	var ids=new Array();
	nodes.push(node);
	while(nodes.length){
		var p=nodes.pop();
		ids.push(p.id);
		for(var i=0;i<p.nodes.length;i++){
			nodes.unshift(p.nodes[i]);
		}
	}
	
	return ids;
}
  $(function(){
    // 机构树
    $('#hospitalTree')
    .treeview({
      color: "#666",
      expandIcon: 'glyphicon glyphicon-chevron-right',
      collapseIcon: 'glyphicon glyphicon-chevron-down',
      nodeIcon: "glyphicon glyphicon-bookmark",
      selectedBackColor:"#d4f9fb",
      selectedColor:"#666",
      showTags: true,
      data: defaultData,
      onNodeUnselected: function(event, data) {
        $(this).treeview('selectNode', [ 0 ,{ silent: false } ]);
        // 这里需要加载所有机构的人..
      },
      onNodeSelected: function(event, data) {
        if(data.nodeId!=0)
          $(this).treeview('unselectNode', [0, { silent: false } ]);
          // 这里需要加载当前机构的人..
		  cOrgan=getSons(data);
		  $("#organ").val(cOrgan.toString());
		  $('#searchBtn').click();
      }
    })
    .treeview('selectNode', [ 0, { silent: true } ])
    .on("click",".badge",function(e){
      e.stopPropagation();
      $('html').find(".tc-nav").hide()
      if($(this).parent().find(".tc-nav").length==0){
        if($(this).siblings('.tree-icon').hasClass("tree-third"))
          $(this).parent().append('<ul class="tc-nav"><li><a href="javascript:;" data-type="edit">修改机构</a></li> <li><a href="javascript:;" data-type="delete">删除机构</a></li></ul>')
        else
          $(this).parent().append('<ul class="tc-nav"><li><a href="javascript:;" data-type="edit">修改机构</a></li><li><a href="javascript:;" data-type="delete">删除机构</a></li><li><a href="javascript:;" data-type="add">增加下级</a></li></ul>')
      }
      $(this).parent().find(".tc-nav").show()
    })
    .on("click",".tc-nav",function(e){
      e.stopPropagation();
    })
    .on("click",".tc-nav a",function(e){
      var nodeid = $(this).parents('.list-group-item').data('nodeid');
      var node = $('#hospitalTree').treeview('getNode', nodeid)
      switch($(this).data('type')){
        case 'edit':   //修改机构
          layer.open({
            type: 2, 
            content: 'OrganizationForm?currentId='+node.id+'&currentName='+node.text,
            title:'修改机构',
            area: ['400px', '230px'],
            btn:['确定','取消'], 
            yes:function(index,layero){ 
              var body = layer.getChildFrame('body', index);
              $(body).find("button[lay-submit]").click()
            }
          });
          break;
        case 'delete':   //删除机构
          layer.confirm('确认删除机构 ['+node.text+'] ?', {icon: 3, title:'系统提示'}, function(index){
        	  $.get("dropOrgan?id="+node.id,function(result){
        		  if(result.code==1){
        			  top.layer.msg("删除成功！");
        			  location.reload();
        		  }
        		  else if(result.haveSons!=0){
        			  layer.msg("该机构下存在子机构，不能删除！");
        		  }
        		  else if(result.haveUser!=0){
        			  layer.msg("该机构下存在用户，不能删除！");
        		  }
        		  else{
        			  layer.msg("删除错误！");
        		  }
        	  },"json");
              layer.close(index);
          });
          break;
        case 'add':
          layer.open({
            type: 2, 
            content: 'OrganizationNew?currentId='+node.id+'&currentName='+node.text,
            title:'增加下级机构',
            area: ['400px', '230px'],
            btn:['确定','取消'], 
            yes:function(index,layero){ 
              var body = layer.getChildFrame('body', index);
              $(body).find("button[lay-submit]").click()
            }
          });
          break;
      }
      $('html').find(".tc-nav").hide()
    })
    $("html").on("click",function(){$('html').find(".tc-nav").hide()})

  })
//机构树模拟数据
var defaultData=${organ};
var fla = true;
layui.use(['table','form'], function(){
  var $=layui.$
  var form = layui.form;
  //表格
  form.on('checkbox(currentOrgan)', function (data) {
	  var checked=this.checked ? 'true' : 'false';
      if(checked=='true'){
    	  $("#showCurrentOrgan").val("1");
      }
      else{
    	  $("#showCurrentOrgan").val("0");
      }
      $('#searchBtn').click();
  });
  
  var table = layui.table;
  table.render({
    elem: '#layTable'
    ,url:'getUsers'
    ,toolbar: '#toolbar'
    ,defaultToolbar:[]
    ,height: 'full-40'
    ,title: '用户数据表'
    ,cols: [[
      {type: 'checkbox', width:35}
      ,{field:'account', title:'用户名', width:80}
      ,{field:'username', title:'姓名', width:60, sort: true }
      ,{field:'sex', title:'性别', width:40,templet:function(data){
    	  var sex = data.sex;
    	  if(data.sex == 0){
    		  sex = "男";
    	  } else if(data.sex == 1){
    		  sex = "女";
    	  }
    	  return sex;
      }}
      ,{field:'photo',align:'center', title:'照片', width:100, templet: function(data){
        //if(data.avatar)return '<img src="../webapp/images/headimg/'+data.avatar+'" onerror="this.src=\'../images/photo.png\'" style="height:50px">';
        if(data.avatar)return '<img src="../headimg/'+data.avatar+'"  style="height:50px">';
        else return '<img src="../images/photo.png" style="height:50px">';
      }}
      ,{field:'title', title:'职称'}
      ,{field:'specialty', title:'专长'}
      ,{field:'parentOrg', title:'上级单位', sort: true}
      ,{field:'organization', title:'所属机构', sort: true}
      ,{field:'role', title:'用户类型'}
      ,{field:'tel', title:'电话', width:100,templet: function(data){
    	  	var tel = data.tel;
    	  		if(data.tel == data.tel){
    	  			return tel.substr(12)
    	  		}
    	  	
     		 }
    	  }
      ,{field:'email', title:'邮箱'}
    ]],
    id: 'users',
    page: true
  });
  
  var $ = layui.$
  var active = {
	  reload: function(){
		 //执行重载
		 table.reload('users', {
		   page: {
			 curr: 1 //重新从第 1 页开始
		   },
		   where: {
			 group: $("#group").val(),
			 seachValue:$("#seachValue").val(),
			 belong:$("#organ").val(),
			 type:$("#showCurrentOrgan").val()
		   }
	   });
     }
  }
  
  $('#searchBtn').on('click', function(){
    var type = $(this).data('type');
    active[type] ? active[type].call(this) : '';
  });


  //表头工具栏事件
  table.on('toolbar(layTable)', function(obj){
    var checkStatus = table.checkStatus(obj.config.id);
    var datas = checkStatus.data; //选中的行数据：checkStatus.data
    var count=datas.length; 
    console.log(checkStatus,datas,count,obj.event,'1111')
    switch(obj.event){
      case 'add':
    	  $.post({
    		  url:'getUserSize',
    		  data:'id=0',
    		  success:function(res){
    			  console.log(res);
    			  if(res.status===1){
    				  layer.open({
    			          type: 2, 
    			          content: 'UserForm?action=add&id=0',
    			          title:'新建用户',
    			          area: ['800px', '560px'],
    			          btn:['确定','取消'], 
    			          yes:function(index,layero){ 
    			            var body = layer.getChildFrame('body', index);
    			            $(body).find("button[lay-submit]").click()
    			          }
    			        });
    			  }else if(res.status===0){
    				  layer.msg('系统用户已达上线，不可开户。如要添加用户请联系供应商。'); 
    			  }else{
    				  layer.msg('操作有误。'); 
    			  }
    		  }
    	  })
      break;
      case 'edit':
        if(count==0){
          layer.msg('未选中任何数据！', {icon: 7}); 
          return
        }
        if(count>1){
          layer.msg('一次只能修改一条数据！', {icon: 7}); 
          return
        }
        layer.open({
          type: 2, 
          content: 'UserForm?action=edit&id='+datas[0].id,
          title:'修改用户',
          area: ['800px', '560px'],
          btn:['确定','取消'], 
          yes:function(index,layero){ 
            var body = layer.getChildFrame('body', index);
            $(body).find("button[lay-submit]").click()
          }
        });
      break;
      case 'delete':
        if(count==0)
          layer.msg('未选中任何数据！', {icon: 7}); 
        else{
        	 var str='';
        	 var role ="";
        	 var isContainAdmin = false;
        	 for(var i=0;i<datas.length;i++){
        		 str+=datas[i].id+',';
        		 role = datas[i].role;
        		 if(role === '超级管理员'){
           		  	 isContainAdmin = true;
           		     continue;
        		 }
        		 
        	 }
        	 str=str.substr(0,str.length-1)
        	 console.log(str,'str')
          layer.confirm('确认删除选中的数据?', {icon: 3, title:'系统提示'}, function(index){  	  
        	  if(isContainAdmin){
        		  layer.msg('不能删除管理员！', {icon: 7}); 
        	  }else{
        		  $.post({
            		  url:'batchDelUser',
            		  data:'ids='+str,
            		  success:function(res){
            			  console.log(res);
            			  if(JSON.parse(res).code=="1"){
            				  if(JSON.parse(res).success == true){
            					  layer.msg('操作成功');
            				  }else{
            					  layer.msg('选择的用户中某些存在正在会诊的会诊不能删除该用户！');
            				  }
            				  active.reload();
            			  }
            			  if(JSON.parse(res).success == false){
            				  layer.msg('选择的用户中存在正在会诊的会诊不能删除该用户！');
            			  }
            			  layer.close(index);
            		  }
            	  })
        	  }
        		  
          });
        }
      break;
      
      case 'import':
    	  $.post({
    		  url:'getUserSize',
    		  data:'id=0',
    		  success:function(res){
    			  console.log(res);
    			  if(res.status===1){
    				  layer.open({
    			          type: 2, 
    			          content: 'UserImport',
    			          title:'用户批量导入',
    			          area: ['450px', '180px'],
    			          btn:['取消'], 
    			        });
    			  }else if(res.status===0){
    				  layer.msg('系统用户已达上线，不可开户。如要添加用户请联系供应商。'); 
    			  }else{
    				  layer.msg('操作有误。'); 
    			  }
    		  }
    	  })
      break;
      case 'downImportTemplate':
    	  window.open("../admin/userInfo.xlsx");
      break;
    };
  });
});

// 	function initUpload(){
// 	  layui.use(['table','form','upload'], function(){
// 		  var $=layui.$
// 		  var upload = layui.upload;
// 		  var ImportLoding;
// 		   upload.render({ //允许上传的文件后缀
// 		     elem: 'button[lay-event="import"]'//'#importbtn'
// 		     ,url: '../uploadflv/importExcel'
// 		     ,accept: 'file' //普通文件
// 		     ,exts: 'xls|xlsx' //只允许上传压缩文件
// 		   	 ,before: function(obj){
// 		   		ImportLoding=layer.load(3);
// 		   	 }
// 		     ,done: function(res){
// 		    	 layer.close(ImportLoding);
// 		    	 if(res.code == 1){
// 		    		 location.reload();
// 		    	 }
// 		       	 top.layer.alert(res.data,{maxWidth:600,maxHeight:400});
// 		     }
// 		   });
// 	 });
// 	}

var d = window.setInterval("getNews()", 1000);
function InitAjax() {

	var ajax = false;
	try {

		ajax = new ActiveXObject("Msxml2.XMLHTTP");

	} catch (e) {

		ajax = false;
	}
	if (!ajax && typeof XMLHttpRequest != 'undefined') {

		ajax = new XMLHttpRequest();
	}

	return ajax;

}

function getNews() {

	//需要进行Ajax的URL地址
	var url = "../CheckLogined";
	//实例化Ajax对象
	var ajax = InitAjax();

	//使用Get方式进行请求
	ajax.open("POST", url, true);
	//获取执行状态
	ajax.onreadystatechange = function(){
	//如果执行是状态正常，那么就把返回的内容赋值给上面指定的层
	if (ajax.readyState == 4 && ajax.status == 200){
	
		var str = ajax.responseText;
		if(str != "normal"){
			alert(str);
			window.clearInterval(d);
			  $.post("../Logout",null,function(result){
				window.location.reload();
				window.clearInterval();
			  })
		}
	}
	
		
	};
	
	var msg =	"${msg}";
	if(msg != null && msg != ""){
		//发送空
		ajax.send(null);
		msg = null;
	}
}
</script>

<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
</body>
</html>
