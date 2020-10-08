<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>404错误页面不存在</title>
    <style type="text/css">
        body,div,h3,h4,li,ol{margin:0;padding:0}
        body{font:14px/1.5 'Microsoft YaHei','微软雅黑',Helvetica,Sans-serif;min-width:1200px;background:#f9f9f9;}
        :focus{outline:0}
        h3,h4,strong{font-weight:700}
        a{color:#428bca;text-decoration:none}
        a:hover{text-decoration:underline}
        .error-page{background:#f0f1f3;}
        .error-page-container{position:relative;z-index:1}
        .error-page-main{position:relative;background:#f9f9f9;margin:0 auto;width:100%;-ms-box-sizing:border-box;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;padding:50px 50px 70px}
        .error-page-main:before{content:'';display:block;height:7px;position:absolute;top:-7px;width:100%;left:0}
        .error-page-main h3{font-size:24px;font-weight:400;border-bottom:1px solid #d0d0d0}
        .error-page-main h3 strong{font-size:54px;font-weight:400;margin-right:20px}
        .error-page-main h4{font-size:20px;font-weight:400;color:#333}
        .error-page-actions{font-size:0;z-index:100}
        .error-page-actions div{font-size:14px;display:inline-block;padding:30px 0 0 10px;width:50%;-ms-box-sizing:border-box;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;color:#838383}
        .error-page-actions ol{list-style:decimal;padding-left:20px}
        .error-page-actions li{line-height:2.5em}
    </style>
</head>
<body>
<div class="error-page">
    <div class="error-page-container">
        <div class="error-page-main">
            <h3>
                <strong>404</strong>很抱歉，您要访问的页面不存在！
            </h3>
            <div class="error-page-actions">
                <div>
                    <h4>可能原因：</h4>
                    <ol>
                        <li>网络信号差不稳定</li>
                        <li>找不到请求的页面</li>
                        <li>输入的网址不正确</li>
                        <li>操作不当出现异常</li>
                    </ol>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>