<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
    String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    <title>上传视频</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
</head>

<body>
    <div class="panel panel-default">
        <div class="panel-body">
        <div class="panel-heading" align="center"><h2 class="sub-header h3">${json.code} : ${json.data}</h2></div>
            <div class="panel-heading" align="center"><h1 class="sub-header h3">文件上传</h1></div>
               <hr>
            <form class="form-horizontal" id="upload" method="post" action="uploadflv/upload" enctype="multipart/form-data">
                <div class="form-group" align="center">
                    <div class="col-md-4 col-sm-4  col-xs-4 col-lg-4">文件上传
                        <input type="file" class="form-control" name="file" id="file"><br>
                        <input type="submit" value="上传">
                        <input type="" name="type" value="1" >
                    </div>
                </div>
            </form>
            
        </div>
        <div>
        	<form class="form-horizontal" method="post" action="uploadflv/importExcel" enctype="multipart/form-data">
		        <input type="file" id="excelFile" name="file"/>
		        <button type="submit">批量上传</button>
		    </form>
        </div>
        
       
        
    </div>
</body>
</html>