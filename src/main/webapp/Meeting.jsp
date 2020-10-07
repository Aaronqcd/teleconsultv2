<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/formSelects-v4.css">
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
  <link rel="stylesheet" type="text/css" href="css/meeting.css"><!-- 自定义css-->
  <link rel="stylesheet" type="text/css" href="css/font/iconfont.css"><!-- 自定义css-->
  <link href="https://cdn.bootcss.com/twitter-bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet"><!-- bootstrap.css-->
  <link rel="stylesheet" type="text/css" href="css/jquery.cxcolor.css">
  <link rel="stylesheet" type="text/css" href="css/meeting.board.css">
  <link rel="stylesheet" type="text/css" href="js/video-v7.5.4/video.css">
 <style type="text/css">
#divMain{
    overflow: hidden;
}
 #divMain,.meeting-container,#divMainRow,.meeting-content-left,#divCamera,.meeting-content-middle,.meeting-content-middle .layui-row,.meeting-content-left  .layui-row{
 height:100% !important;
 }
 .meeting-chat{
 height:calc(100% - 285px) !important;
     max-height: inherit !important;
 }

 .meeting-content-right-content,#cvs-body{
  height:calc(100% - 50px) !important;
 }
 .meeting-tool{
 margin-top:0;
 }
 .layui-tab{
 margin:0;
 }
 .meeting-chat{
 top:285px !important;
 }
 .meeting-chat-content{
  height:calc(100% - 30px - 180px) !important;
    max-height: inherit !important;
 }
 .meeting-chat-send{
 height:180px !important
 }
 .meeting-chat-send .layui-layedit{
 height: auto !important;
 }
.meeting-tool .layui-tab-content{
height:235px !important;
}
 .meeting-content-right-item span.joined{
 left:5px !important;
 }
 .meeting-info-btn{
     padding-left: 0 !important;
    position: relative !important;
    
    height: 30px !important;
    margin:14px 0 !important;
 }
 #meeting-info-form{
 height:100%;
 float:left;
 width:100%;
 }
 .font{
 width:100%;
 margin:0;
 }
 .font .new{
 width:100%;
 }
 	 
			
				@media only screen and (min-device-width : 768px) and (max-device-width : 1024px) {
				.meeting-chat-send {
				height:130px !important
				}
			 .meeting-chat-content{
			     height: calc(100% - 30px - 130px) !important;
			 }
		 
			}
		 
 </style>
</head>
<body>
<input id="currentUser" type="hidden" value="${loginId}">
<input id="currentMeetingId" type="hidden" value="${currentMeetingId}">
<input id="currentOrg" type="hidden" value="${currentOrgId}">
<input id="isteudborad" type="hidden" value="${isteudborad}">
<input id="hdPresenter" type="hidden" value="${isPresenter}" >
<input id="basePath" type="hidden" value="${basePath}" >
<!-- 左侧 -->
<div class="layui-side layui-bg-black pad-size">
  <div class="layui-side-scroll pad-size" style="background:#06b4bf">
    <div class="meeting-nav">
      <%if(Boolean.FALSE.equals(request.getAttribute("isPresenter"))) {%>
        <div class="meeting-nav-item apply-host"><div><img src="images/meeting/main_icon.png"></div><p>我要主持</p></div>
      <% }%>
      <%if(Boolean.TRUE.equals(request.getAttribute("isPresenter"))) {%>
      <%-- <div class="meeting-nav-item"><div><img src="images/meeting/live_icon.png"></div><p>开始直播</p></div> --%>
      <div class="meeting-nav-item" id="btnShare"><div><img src="images/meeting/screenshots_icon.png"></div><p>屏幕共享</p></div>
	  <%--  <div class="meeting-nav-item capture"><div><img src="images/meeting/screenshots_icon.png"></div><p>抓拍</p></div> --%>
	  <div class="meeting-nav-item div-noshow" id="btnStartRecord"><div><img src="images/meeting/record_start.png"></div><p>开始录制</p></div>
	  <div class="meeting-nav-item div-noshow" id="btnPauseRecord"><div><img src="images/meeting/record_pause.png"></div><p>暂停录制</p></div>
	  <div class="meeting-nav-item div-noshow" id="btnResumeRecord"><div><img src="images/meeting/record_resume.png"></div><p>继续录制</p></div>
	  <div class="meeting-nav-item div-noshow" id="btnStopRecord"><div><img src="images/meeting/record_stop.png"></div><p>停止录制</p></div>
      <div class="meeting-nav-item invite-expert"><div><img src="images/meeting/invitation_icon.png"></div><p>邀请专家</p></div>
      <div class="meeting-nav-item transfer-presenter"><div><img src="images/meeting/switch_icon.png"></div><p>转让主讲</p></div>
      <div class="meeting-nav-item fileuploadform-info"><div><img src="images/meeting/upload_icon.png"></div><p>离线共享</p></div>
      <% }%>
      <div class="meeting-nav-item meeting-info"><div><img src="images/meeting/info_icon.png"></div><p>会诊纪要</p></div>
      <div class="meeting-nav-item exit"><div><img src="images/meeting/exit_icon.png"></div><p>退出</p></div>
    </div>
    <%--<div id="hospitalTree" class="tree-hospital" style="width: 130px;margin-top: 15px;"></div>--%>
  </div>
</div>

<!-- 右侧 -->
<div id="divMain" class="layui-body">
    <div class="layui-fluid meeting-container"> 
        <div id="divMainRow" class="layui-row layui-col-space5"> 
		  <div class="meeting-content-left layui-col-md3">
		  <div class="layui-row layui-col-space1"> 
		    <div class="meeting-tool layui-col-md12">
		    <%if(Boolean.TRUE.equals(request.getAttribute("isPresenter"))) {%>
		    <div class="layui-tab" lay-filter="layTabView">
                  <ul id="tabView" class="layui-tab-title meeting-tool-header">
					  <c:if test="${isteudborad == 1}">
                    		<li class="layui-this layui-col-sm6 meeting-board-tool" lay-id="tabTool" data-tab="board" >电子白板</li>
					  </c:if>
					  <li class="layui-col-sm6" lay-id="tabMix" data-tab="mix">4S系统</li>
                  </ul>
                  <div class="layui-tab-content">
					  <c:if test="${isteudborad == 1}">
						  <div class="layui-tab-item layui-show meeting-board-item-tool">
							<jsp:include page="MeetingBoard.jsp" flush="true"></jsp:include>
						  </div>
					  </c:if>
                    <div class="layui-tab-item">
                    	<div id="treeResource" class="tree-hospital"></div>
                    </div>
                  </div>
                </div>		      
		    <%} else {%>
		    <div class="layui-tab" lay-filter="layTabView">
                  <ul id="tabView" class="layui-tab-title meeting-tool-header">
					  <c:if test="${isteudborad == 1}">
                    	 <li class="layui-this layui-col-sm12" lay-id="tabTool" data-tab="board">电子白板</li>
					  </c:if>
                  </ul>
                  <div class="layui-tab-content">
					  <c:if test="${isteudborad == 1}">
						  <div class="layui-tab-item layui-show">
							  <jsp:include page="MeetingBoard.jsp" flush="true"></jsp:include>
						  </div>
					  </c:if>

                  </div>
                </div>
              <% } %>
		    </div>
		    <div class="meeting-chat layui-col-md12">
		      <div class="meeting-chat-header">即时通讯</div>
		        <div class="meeting-chat-content">
		            <%--<div class="meeting-chat-info other">--%>
		                <%--<div><span class="meeting-chat-img"><img src="images/meeting/tree_cloud_icon.png"></span><span class="meeting-chat-name">李教授</span></div>--%>
		                <%--<div class="meeting-chat-text">你好!</div>--%>
		            <%--</div>--%>
		            <%--<div class="meeting-chat-info other">--%>
		                <%--<div><span class="meeting-chat-img"><img src="images/meeting/tree_cloud_icon.png"></span><span class="meeting-chat-name">李教授</span></div>--%>
		                <%--<div class="meeting-chat-text">我们都知道，冠心病是心血管疾病中比较常见的一种，常常发生于中老年人群，当然随着近年来生活水平的提高，人们生活结构方式的改变，冠心病疾病已经慢慢年轻化，很多年轻人也渐渐加入了冠心病这个队伍中来.!</div>--%>
		            <%--</div>--%>
		            <%--<div class="meeting-chat-info target">--%>
		                <%--<div><span class="meeting-chat-name">王教授</span><span class="meeting-chat-img"><img src="images/meeting/tree_cloud_icon.png"></span></div>--%>
		                <%--<div class="meeting-chat-text">你好！</div>--%>
		            <%--</div>--%>
		            <%--<div class="meeting-chat-info target">--%>
                        <%--<div><span class="meeting-chat-name">王教授</span><span class="meeting-chat-img"><img src="images/meeting/tree_cloud_icon.png"></span></div>--%>
                        <%--<div class="meeting-chat-text">我们都知道的。</div>--%>
                    <%--</div>--%>
                    <%--<div class="meeting-chat-info other">--%>
                        <%--<div><span class="meeting-chat-img"><img src="images/meeting/tree_cloud_icon.png"></span><span class="meeting-chat-name">李教授</span></div>--%>
                        <%--<div class="meeting-chat-text">好的!</div>--%>
                    <%--</div>--%>
		      </div>
                <div class="im-control">
                    <div class="im-face-set">
                        <ul id="emotionUL"></ul>
                    </div>

                    <div class="im-font-set">
                        <span class="font-text">字体</span>
                        <select class="font-name-select" lay-verify="">
                            <option selected style="font-family: Microsoft YaHei;">请选择</option>
                            <option font-class="HWKT" style="font-family: STKaiti;">华文楷体</option>
                            <option font-class="HWXK" style="font-family: HWXK;">华文行楷</option>
                            <option font-class="SONGTI" style="font-family: SimSun,SONGTI;">宋体</option>
                            <option font-class="WRYHC" style="font-family: Microsoft Yahei, WRYHC;">微软雅黑</option>
                        </select>
                        <select class="font-size-select" style="position: relative; top: -2px;">
                            <option>8</option>
                            <option>10</option>
                            <option selected>12</option>
                            <option>14</option>
                            <option>16</option>
                            <option>18</option>
                            <option>20</option>
                            <option>24</option>
                        </select>
                    </div>
                </div>
		      <div class="meeting-chat-send">
                  <div class="im-tools">
                      <i class="iconfont font">&#xec85;</i> 
                      <i class="iconfont face">&#xe614;</i>
                  </div>

		          <textarea id="msg-input" name="" required lay-verify="required" placeholder="输入消息" class="layui-textarea"></textarea>

		          <button class="layui-btn layui-btn-radius layui-btn-sm btn-send-msg layui-icon-down">发送</button>
                  <button class="layui-btn layui-btn-radius layui-btn-sm btn-send-msg">发送 &nbsp;<i class="layui-icon btn-select-quick-key">&#xe61a;</i></button>
                  <div class="quick-send-set" >
                      <div class="quick-send-item enter">
                          <i class="layui-icon quick-send-item-icon enter">&#xe605;</i><span>按Enter键发送消息</span>
                      </div>
                      <div class="quick-send-item ctrl-enter">
                          <i class="layui-icon quick-send-item-icon ctrl-enter active">&#xe605;</i><span>按Ctrl+Enter键发送消息</span>
                      </div>
                  </div>
		      </div>
		    </div>
		    </div>
		  </div>
		  <div class="meeting-content-middle layui-col-md5">
			   <a id="prevBoard"  class="carousel-control-prev" role="button" data-slide="prev">
		            <img src="images/board/second-level/page/prev1.png" style="width:20%;z-index:3000"> 
					            
		        </a>
		        
	          	<a id="nextBoard" class="carousel-control-next" role="button" data-slide="next">
		            <img src="images/board/second-level/page/next1.png" style="width:20%;z-index:3000">			
		            
		        </a>   
		      <div class="layui-row">
			      <div id="cvs-head" class="content-middle-header layui-col-md12">会诊工作台
			          <!-- <img src="images/meeting/enlarge.png"> -->
                      <div class="boardPage cvs-title" data-tab="board"></div>
			      </div>
			                      
			          <!-- <div class="pdf-btn layui-row" data-tab="board">
			          	
				                        <ul>
                          <li id="prevBoard" class = "layui-col-md3 layui-col-md-offset2"><img src="images/board/second-level/page/previous.png">前翻一页</li>
                          <li id="nextBoard" class = "layui-col-md3 layui-col-md-offset2"><img src="images/board/second-level/page/next.png">后翻一页</li>
                       </ul> 
                      </div> -->
                                                                                  
			                   
			      <div id="cvs-body" class="content-middle layui-col-md12" >			      		
					  <div class="select-file" style="position: absolute;width: 35px;top: -40px; background-color: #06b4bf;border: 0px solid #aaa;">
					  	<select id="rack-select" style="padding-left: 30px;"></select>					  
					  </div>
                      <div id="content_borad" class="cvs-page" data-tab="board"></div>
                      <div id="content_mix" class="cvs-page div-noshow" data-tab="mix">
	                      <div id="content_4s" class="div-noshow" data-tab="4s"></div>
	                      <div id="content_live" class="div-noshow" data-tab="live">
	                      	<c:if test="${isPresenter}">
		                      	<div style="margin:10px">
		                      		<span>直播科室：</span><span id="infoLiveRoom" ></span>
		                      		<span style="display:inline-block;width:20px;"></span>
		                      		<span>直播频道：</span><span id="infoLiveChannel"></span>
		                      		<span style="display:inline-block;width:20px;"></span>
		                      		<button type="button" class="layui-btn" onclick="LiveMeeting.closeDevice();">停止直播</button>
		                     	</div>
	                     	</c:if>
	                      	<video id="liveVideo" style="width:100%"
	                      		controls="controls" autoplay="autoplay" muted>
	                      	</video>
    						<canvas id="capture-canvas"></canvas>
	                      </div>
                      </div>
                      <div id="content_info" class="cvs-page div-noshow" data-tab="info">
				         <form id = "meeting-info-form" class="layui-form" >
				             <input name="meetingId" type="hidden" value="${currentMeetingId}">
							  <div class="meeting-info-form-item">
							    <label class="layui-form-label meeting-info-form-label">会诊主题</label>
							    <div class="layui-input-block">
							      <input type="text" name="meetingTitle" lay-verify="required|title" autocomplete="off" placeholder="请输入会诊主题" 
							      class="layui-input"  value="${meetingInfo['topic']}" readonly="readonly" <c:if test="${isPresenter==false}">readonly="readonly"</c:if>>
							    </div>
							  </div>
							  <div class="meeting-info-form-item">
	                            <label class="layui-form-label meeting-info-form-label">会诊时间</label>
	                            <div class="layui-input-block">
	                              <input type="text" name="meetingTime" id="meetingTime" lay-verify="required|datetime" placeholder="yyyy-MM-dd hh:mm:ss" 
	                              class="layui-input"  format='YYYY-MM-DD HH:mm:ss' readonly="readonly" value="${meetingStartTime}" <c:if test="${isPresenter==false}">readonly="readonly"</c:if>>
	                            </div>
	                          </div>
	                          <div class="meeting-info-form-item">
	                            <label class="layui-form-label meeting-info-form-label">与会人</label>
	                            <div class="layui-input-block">
	                            	<input type="text" name="attendacceptsName" autocomplete="off" placeholder="与会人" class="layui-input" 
	                                    attends-id="${meetingInfo['attendaccepts']}" value="${attendacceptsName}" readonly="readonly">
	                            </div>
	                            <!-- 
	                            <div class="layui-input-block">
	                              <c:if test="${isPresenter==true}">
	                              <select name="meetingAttends" xm-select="attends-select" 
	                                xm-select-max="7" <c:if test="${isPresenter==false}">readonly="readonly"</c:if> >
	                                    <option  value="" >-请选择-</option>
	                                    <c:forEach var="item" items="${attends}">
	                                        <option value="${item.id}" selected="selected">${item.name}</option>
	                                    </c:forEach>
	                                  </select>
	                               </c:if>
	                               <c:if test="${isPresenter==false}">
	                                    <input type="text" name="meetingAttends" autocomplete="off" placeholder="与会人" class="layui-input" 
	                                    attends-id="${meetingInfo['attends']}" value="${attendsNames}" readonly="readonly">
	                              </c:if>
	                            </div>
	                             -->
	                          </div>
	                          <div class="meeting-info-form-item">
	                            <label class="layui-form-label meeting-info-form-label">缺席人</label>
	                            <div class="layui-input-block">
	                            	<input type="text" name="meetingAbsentee" autocomplete="off" placeholder="缺席人" class="layui-input" 
	                                    attends-id="${meetingInfo['absentee']}" value="${meetingInfo['absenteeNames']}" readonly="readonly">
	                            </div>
	                            <!-- 
	                            <div class="layui-input-block">
	                                <c:if test="${isPresenter==true}">
	                                <select name="meetingAbsentee" xm-select="absentee-select" 
	                                xm-select-max="7" <c:if test="${isPresenter==false}">readonly="readonly"</c:if> >
							            <option  value="" >-请选择-</option>
							            <c:forEach var="item" items="${attends}">
							                <option value="${item.id}">${item.name}</option>
							            </c:forEach>
							          </select>
							        </c:if>
							        <c:if test="${isPresenter==false}">
	                                    <input type="text" name="meetingAbsentee" autocomplete="off" placeholder="缺席人" class="layui-input" 
	                                    attends-id="${meetingInfo['absentee']}" value="${meetingInfo['absenteeNames']}" readonly="readonly">
	                              </c:if>
	                            </div>
	                             -->
	                          </div>
	                          <div class="meeting-info-form-item layui-form-text" style="height:calc(100% - 310px)">
							    <label class="layui-form-label meeting-info-form-label">会诊结论</label>
							    <div class="layui-input-block" style="height:100% !important" >
							    <textarea rows="14" name="beforeConclusion" style="display: none;">${meetingInfo['conclusion']}</textarea>
							      <textarea rows="27" name="meetingConclusion" id="meetingConclusion" placeholder="请输入结论内容"  style="height:100% !important" class="layui-textarea" 
							      <c:if test="${isPresenter==false}">readonly="readonly"</c:if>>${meetingInfo['conclusion']}</textarea>
							    </div>
							  </div>
							  <c:if test="${isPresenter==true}">
								  <div class="meeting-info-btn" style="padding-left:0;    position: initial;    margin: 10px 0;">
								   
								        <div class = "layui-col-md4" style="float:right;width:80px;margin-left:10px;">
								          <div class="layui-btn layui-btn-radius layui-btn-sm" lay-submit lay-filter="save" style="background-color:#1EB5BF;width:100%">保存</div>
		                              </div>
									   <div class = "layui-col-md4 layui-col-md-offset0" style="width:80px;background: transparent;margin-left:10px;float:right;">
								          <div class="layui-btn layui-btn-radius layui-btn-sm " lay-submit lay-filter="share"style="width:100%;background-color:#1EB5BF;">分享</div>
								      </div>
								      <div class = "layui-col-md4" style="float:right;width:100px;">
								         <div id="startInputText" class="layui-btn layui-btn-radius layui-btn-sm" lay-submit onclick="startRecording()" style="background-color:#1EB5BF;width: 100%;max-width:100px;">开始语音识别</div>
		                                  <div id="stopInputText" class="layui-hide layui-btn-sm" lay-submit onclick="stopRecordingTwo()" style="background-color:#1EB5BF;width: 100%;max-width:100px;">停止语音识别</div>
								      </div>
									     
								  </div>
							  </c:if>
						 </form>
                      </div>
                      <div class="clear"></div>

			      </div>
		      </div>
		  </div>
		  <div id="divCamera" class="meeting-content-right layui-col-md4">
		      <div class="content-right-header">会诊专家列表
				<div class="fright">
					<a id="btnCamMin" class="cabtn" title="最小化"><i class="mticon icon-16 icon-min"></i></a>
					<a id="btnCamRestore2" class="cabtn" style="display:none;" title="还原"><i class="mticon icon-16 icon-restore"></i></a>
					<a id="btnCamMax" class="cabtn" title="最大化"><i class="mticon icon-16 icon-max"></i></a>
				</div>
		      </div>
		      <div class="meeting-content-right-content">
		          <%--<div class="meeting-content-right-item layui-col-xs6"><img src="images/meeting/portrait.jpg" class="portrait"><span>张教授</span><img src="images/meeting/speak.png" class="speak"></div>--%>
		          <%--<div class="meeting-content-right-item layui-col-xs6"><img src="images/meeting/portrait.jpg" class="portrait"><span>张教授</span><img src="images/meeting/speak.png" class="speak"></div>--%>
		          <%--<div class="meeting-content-right-item layui-col-xs6"><img src="images/meeting/portrait.jpg" class="portrait"><span>张教授</span><img src="images/meeting/speak.png" class="speak"></div>--%>
		          <%--<div class="meeting-content-right-item layui-col-xs6"><img src="images/meeting/portrait.jpg" class="portrait"><span>张教授</span><img src="images/meeting/speak.png" class="speak"></div>--%>
		          <%--<div class="meeting-content-right-item layui-col-xs6"><img src="images/meeting/portrait.jpg" class="portrait"><span>张教授</span><img src="images/meeting/speak.png" class="speak"></div>--%>
		          <%--<div class="meeting-content-right-item layui-col-xs6"><img src="images/meeting/portrait.jpg" class="portrait"><span>张教授</span><img src="images/meeting/speak.png" class="speak"></div>--%>
		          <%--<div class="meeting-content-right-item layui-col-xs6"><img src="images/meeting/portrait.jpg" class="portrait"><span>张教授</span><img src="images/meeting/speak.png" class="speak"></div>--%>
		      </div>
		  </div>
	  </div>
    </div>
</div>
<div id="cameraBar" class="camerabar div-noshow">
	<a id="btnCamRestore" class="cabtn" title="还原"><i class="mticon icon-16 icon-restore"></i></a>
</div>
<div id="content_share" class="share-container div-noshow" data-tab="share">
</div>
<form class="layui-form" id ="select-transfer-user" style="padding-top:10px; display:none;">
    <div class="layui-form-item">
       <label class="layui-form-label" style="width:90px">选择用户</label>
       <div class="layui-input-block">
          <select name="attends" lay-verify="required" xm-select="transfer-attends-select"  xm-select-radio=""  >
            <option  value="" >-请选择-</option>
            <c:forEach var="item" items="${attends}">
                <option value="${item.user}">${item.name}</option>
            </c:forEach>
          </select>
         </div>
     </div>
</form>
<form class="layui-form" id ="select-expert-user" style="padding-top:10px; display:none;">
    <div class="layui-form-item">
       <label class="layui-form-label" style="width:90px">选择专家</label>
       <div class="layui-input-block">
          <select name="expert" lay-verify="required" autocomplete="off" xm-select="expert-select" xm-select-type="4" xm-select-max="6" xm-select-search=""  >
            <option  value="" >-请选择-</option>
            <c:forEach var="item" items="${experts}">
                <option value="${item.user}">${item.name}</option>
            </c:forEach>
          </select>
         </div>
     </div>
</form>
<%
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = df.format(d);
	%>

<!--抓拍弹出框    update by and  -->
<div id="addScheduleDiv"   title="抓拍截图名称" style="display:none;padding:35px 35px;">
	<form action="${contextPath}+/MeetingTools/cutPic"  lay-verify="required" id="addZizhiForm" class="layui-form"  enctype="multipart/form-data" method="post">
 		<div class="layui-form-item">
 		<table > 
 		<tr>
		<td>文件名 </td>
		<td>&nbsp;
		<div class="layui-form-item">
	       <div class="layui-input-block">
			<input value="<%=now%>" name="fileName" id="fileName" style="height:35px;width:200px" />
		</div>
		</div>
		</td>
		</tr>
		<tr>
		<td>类型</td>
		<td>
		<div class="layui-form-item">
	       <div class="layui-input-block">
	          <select name="attends" lay-verify="required" id="picSel" autocomplete="off" xm-select="img-type-select"  xm-select-radio=""  >
	            <option value="jpg" selected="selected">jpg</option>
	            <option value="png">png</option>
	             <option value="bmp">bmp</option>
	          </select>
	         </div>
     </div>
		</td>
		</tr>
 		</table> 
 		</div>
	</form>
</div>
<script src="https://cdn.bootcss.com/jquery/3.0.0/jquery.min.js"></script><!-- jquery.js -->
<script src="js/vedio/lib/jquery/jquery.js"></script><!-- layui.js -->
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<script src="js/common.js"></script>
<script src="js/formSelects-v4.js"></script>
<script type="text/javascript" src="js/vedio/lib/bootstrap/bootstrap-table.js"></script>
<script type="text/javascript" src="js/vedio/sdk/json2.js"></script>
<script type="text/javascript" src="js/vedio/sdk/webim.js"></script>
<script type="text/javascript" src="js/vedio/msg/receive_new_msg.js"></script>
<script type="text/javascript" src="js/jquery.cxcolor.js?v=4"></script>
<script src="js/canvas2image.js"></script>
<script type="text/javascript" src="js/html2canvas.min.js"></script>

<!-- Axios SDK -->
<script src="https://tic-res-1259648581.file.myqcloud.com/thirdpart/axios/axios.min.js"></script>

<!-- COS SDK -->
<script src="https://sqimg.qq.com/expert_qq/tedu/cos/5.1.0/cos.min.js"></script>

<!-- WebRTC SDK -->
<!-- <script src="https://sqimg.qq.com/expert_qq/webrtc/3.4.1/WebRTCAPI.min.js"></script> -->
<script src="https://tic-res-1259648581.file.myqcloud.com/webrtc/3.4.1.1/WebRTCAPI.min.js"></script>

<!-- 白板SDK -->
<script src="https://tic-res-1259648581.file.myqcloud.com/board/2.3.4/TEduBoard.min.js"></script>

<!-- TIC SDK -->
<script src="https://tic-res-1259648581.file.myqcloud.com/tic/2.3.2/TIC.min.js"></script>

<!-- TRTC SDK -->
<script src="js/trtc/trtc.js"></script>

<!-- WebRTC Live SDK -->
<script src="https://sqimg.qq.com/expert_qq/webrtcLive/1.0.0/WebRTCAPI.min.js"></script>

<script type="text/javascript" src="js/accont.js"></script>
<script type="text/javascript" src="js/vedio/im_utils.js"></script>
<script type="text/javascript" src="js/vedio/meeting2.js"></script>
<script type="text/javascript" src="js/meeting.board.js"></script>

<script type="text/javascript" src="js/vedio/common/show_one_msg.js"></script>
<script type="text/javascript" src="js/vedio/msg/send_common_msg.js"></script>
<script type="text/javascript" src="js/vedio/msg/receive_group_system_msg.js"></script>
<script type="text/javascript" src="js/vedio/msg/receive_profile_system_msg.js"></script>
<script type="text/javascript" src="js/vedio/logout/logout.js"></script>
<script type="text/javascript" src="js/vedio/msg/send_custom_group_notify_msg.js"></script>
<script type="text/javascript" src="js/vedio/recentcontact/switch_chat_obj.js"></script>

<script type="text/javascript" src="js/video-v7.5.4/video.js"></script>
<script type="text/javascript" src="js/Utils.js"></script>
<script type="text/javascript" src="js/meeting.4s2.js"></script>

<!-- 实时语音识别 -->
<script type="text/javascript" src="js/realtime/HZRecorder.js"></script>
<script type="text/javascript">
	var recorder;
    var ref;

    function startRecording() {
        $("#startInputText").addClass('layui-hide');
        $("#stopInputText").removeClass('layui-hide');
        $("#stopInputText").addClass('layui-btn layui-btn-radius');
        if (recorder != undefined) {
            stopRecording();
        }

        if (ref != undefined) {
            clearInterval(ref);
        }
        HZRecorder.get(function (rec) {
            recorder = rec;
            recorder.start();
            ref = setInterval(function () {
                //console.log(recorder.size());
                if (recorder.size() > 0) {
                    uploadAudio();
                    recorder.clear();
                    recorder.start();
                }
            }, 1000);
        });
    }

    function stopRecording() {
        $("#startInputIMText").removeClass('layui-hide');
        clearInterval(ref);
        recorder.stop();
    }

    function stopRecordingTwo() {
        $("#stopInputText").addClass('layui-hide');
        $("#startInputText").removeClass('layui-hide');

        clearInterval(ref);
        recorder.stop();
    }

    function uploadAudio() {
        console.log("进入方法中")//就这里添加了一句话 把上面的那个引入文件去掉了
        recorder.upload("${pageContext.request.contextPath}/ASR/realtime", function (state, e) {
            switch (state) {
                case 'uploading':
                    break;
                case 'ok':

                    var ele = document.getElementById("meetingConclusion");
                    console.log(e.target.responseText);
                    ele.value = ele.value + e.target.responseText;
                    break;
                case 'error':
                    alert("识别失败");
                    break;
                case 'cancel':
                    alert("上传被取消");
                    break;
            }
        });
    }


    function startChatRecording() {
        if (recorder != undefined) {
            stopRecording();
        }

        if (ref != undefined) {
            clearInterval(ref);
        }
        HZRecorder.get(function (rec) {
            recorder = rec;
            recorder.start();
            ref = setInterval(function () {
                //console.log(recorder.size());
                if (recorder.size() > 0) {
                    uploadChatAudio();
                    recorder.clear();
                    recorder.start();
                }
            }, 1000);
        });
    }


    var ele = document.getElementById("msg-input");//获取文本域的id

    function getFocusTwo(el, iframeDOM) {
        iframeDOM = iframeDOM ? iframeDOM : window;
        if (document.getSelection) {//ie11 10 9 ff safari
            el.focus(); //解决ff不获取焦点无法定位问题
            var range = iframeDOM.document.getSelection();//创建range
            range.selectAllChildren(el);//range 选择obj下所有子内容
            range.collapseToEnd();//光标移至最后
        } else if (document.selection) {//ie10 9 8 7 6 5
            var range = iframeDOM.document.selection.createRange();//创建选择对象
            //var range = document.body.createTextRange();
            range.moveToElementText(el);//range定位到obj
            range.collapse(false);//光标移至最后
            range.select();
        }
    }

    function uploadChatAudio() {
        $("#startInputIMText").addClass('layui-hide');
        console.log("进入聊天方法中")//就这里添加了一句话 把上面的那个引入文件去掉了
        recorder.upload("${pageContext.request.contextPath}/ASR/realtime", function (state, e) {
            switch (state) {
                case 'uploading':
                    break;
                case 'ok':
                    var $edit = $(window.frames['LAY_layedit_1'].document).find("body");
                    console.log("获取到的文本信息：");
                    var msgContent = $edit.html();
                    $edit.html(msgContent + e.target.responseText);
                    /* var iframeDOM = false;
                    getFocusTwo(ele,iframeDOM) */
                    break;
                case 'error':
                    alert("识别失败");
                    break;
                case 'cancel':
                    alert("上传被取消");
                    break;
            }
        });

    }



    layui.config({
        version: '1554901098009' //为了更新 js 缓存，可忽略
    });

    var vedioApiHost = "https://telemed.szvisionapp.cn/tencent-video-api"
    var layer = "";
    var contextPath = "${pageContext.request.contextPath}";
  //注意：导航 依赖 element 模块，否则无法进行功能性操作
  layui.use(['element','form','layer','laydate', 'layedit'], function(){
	var $ = layui.$;
    var element = layui.element;
    layer = layui.layer;
    window.layer = layer
    var formSelects = layui.formSelects;
    var laydate = layui.laydate;
    var form = layui.form;

    var layedit = layui.layedit;
    layedit.build('msg-input',{
        height: 'auto'
        , tool: []
        ,hideTool: ['face']
    });

    onEditorKeydown()

    form.verify({
    	datetime:[/^(\d{4})[-\/](\d{1}|0\d{1}|1[0-2])([-\/](\d{1}|0\d{1}|[1-2][0-9]|3[0-1]))*\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/, "时间格式不正确"],
    });


      //初始化与会者默认值
    var attendArray = [];
    <c:forEach var="item" items="${attends}">
        attendArray.push("${item.id}");
	</c:forEach>
    formSelects.value('attends-select',attendArray);
    //缺席者
    var absenteeArray = [];
    <c:forEach var="item" items="${absentee}">
        absenteeArray.push("${item.id}");
    </c:forEach>
    formSelects.value('absentee-select',absenteeArray);

    //设置聊天窗口大小
    var chatContentHeight = $(window).height() - 340 - 100 - 45 - 85;
    //$(".meeting-chat-content").css("height","67.5%");
    $(".meeting-content-right-content").css("height",$(".meeting-nav").height()-45+"px");
    $(".meeting-chat").css("height", $(".meeting-chat-send").height()+chatContentHeight+60+"px");
    
    //聊天窗口滚动到下方
    $('.meeting-chat-content').scrollTop( $('.meeting-chat-content')[0].scrollHeight);
    //画布高度
    var contentMiddleHeight = $(window).height() - 45;
    $('.content-middle').css("height",contentMiddleHeight+"px");

    $("body").on("click",".exit",function(){
    	if(isEditMeetingConclusion(this)){
    		return false;
    	}   
    	layer.confirm('确认退出会诊?', {icon: 3, title:'系统提示'}, function(index){
            var currentMeetingId = $("#currentMeetingId").val();
            layer.close(index);
            $.get("quitMeeting",{meetingId:currentMeetingId},function(result){
                //layer.msg(result.msg);
                quitMeeting();
            	window.history.back(-1);
            },"json");
          });
    });
    $("body").on("click",".meeting-info",function(){
        if(isEditMeetingConclusion(null)){
    		return false;
    	}
        var isShow = $(this).attr("isShow");
        
        if(isShow == 'undefined' || isShow){
        	isShow = false;
        } else {
        	isShow = true;
        }
        isShowMeetingInfo(isShow);
    });

      function isEditMeetingConclusion(obj){
    	var beforeConclusion = $("#meeting-info-form").find("[name='beforeConclusion']").val();
    	var afterConclusion = $("#meeting-info-form").find("[name='meetingConclusion']").val();
    	var isEdit = (beforeConclusion != afterConclusion);
    	if(isEdit){
    		layer.confirm('您有需修改的内容，是否需要先保存?', {icon: 3, title:'系统提示'}, function(index){
            	$.ajaxSettings.async = false;
    			$("[lay-filter='save']").click();
        		isShowMeetingInfo("false");
            	$.ajaxSettings.async = true;
    			layer.close(index);
    			if(obj){
        			$(obj).click();
    			}
	        },function(index){
	        	$.ajaxSettings.async = false;
	        	isShowMeetingInfo("false");
	        	$.ajaxSettings.async = true;
	        	layer.close(index);
	        	if(obj){
        			$(obj).click();
    			}
	        });
    	} else {
    		//if(obj) {    			
	        //	var operate = $(obj).attr("lay-filter");
	        //	if(operate != "save" && operate != "share"){
		    //		isShowMeetingInfo("true");
	        //	}
        	//}
    	};

    	return beforeConclusion != afterConclusion;
    }
    function isShowMeetingInfo(isShow){
    	if(isShow == "true"){
    		$(".meeting-info").removeClass("select_button");
        	$(".meeting-info").attr("isShow","false");
        	$(".pdf-btn").removeClass("pdf-btn-unshow");
    		// $("#meeting-info-form").hide();
    		LiveMeeting.toggleTabView("info", false);
        } else {
        	$(".pdf-btn").addClass("pdf-btn-unshow");
        	$.get("currentMeeting",{meetingId:$("#currentMeetingId").val()},function(result){
        		var mettingObj = {};
        		mettingObj.meetingTitle = result.meetingInfo.topic;
        		mettingObj.meetingTime = result.meetingStartTime;
        		mettingObj.attendacceptsName = result.attendacceptsName;
        		mettingObj.meetingAbsentee = result.absenteeName;
        		mettingObj.meetingConclusion = result.meetingInfo.conclusion;
        		$("#meeting-info-form").find("[name='beforeConclusion']").val(mettingObj.meetingConclusion);
        		$("#meeting-info-form input[name='meetingAttends']").attr("attends-id",result.attendIds);
        		$("#meeting-info-form input[name='meetingAbsentee']").attr("attends-id",result.absentee);
        		Util.fillFormData("#meeting-info-form",mettingObj);
            },"json");
        	$(".meeting-info").attr("isShow","true");
        	$(".meeting-info").addClass("select_button");
        	// $("#meeting-info-form").show();
    		LiveMeeting.toggleTabView("info", true);
        }

        var boradDiv = $("#content_borad").css("display");
        if(boradDiv == "none"){
        	$(".carousel-control-prev").hide();
        	$(".carousel-control-next").hide();
        } else {
        	$(".carousel-control-prev").show();
        	$(".carousel-control-next").show();
        }
    }
    $("body").on("click",".meeting-tool-tree .layui-icon-down",function(){
    	$(this).hide();
    	$(this).siblings(".meeting-resource-toggle.layui-icon-right").show();
    	$(this).parents(".tree-item").children(".second-item-ul").hide();
    });
    $("body").on("click",".meeting-resource-toggle.layui-icon-right",function(){
    	$(this).hide();
        $(this).siblings(".meeting-resource-toggle.layui-icon-down").show();
        $(this).parents(".tree-item").children(".second-item-ul").show();
    });
    //测试
    /*$("body").on("click",".tool-btn-edit",function(){
    	var currentMeetingId = $("#currentMeetingId").val();
    	$.get("currentMeeting",{meetingId:currentMeetingId},function(result){
    		var jsonStr = JSON.stringify(result); 
            alert(jsonStr);
          },"json");
    });*/
    
    //websocket通信
    var basePath = $("#basePath").val();
    var currentUser = $("#currentUser").val();
    var currentMeetingId = $("#currentMeetingId").val();
    var protocol = window.location.protocol;
    //console.log("basePath: "+basePath)
    var websocket;
    
    function initWebsocket(){
    	// 首先判断是否 支持 WebSocket
        if('WebSocket' in window) {
        	if(protocol==="http:"){
        	     websocket = new WebSocket("ws://"+basePath+"/webSocket?meetingId="+currentMeetingId);
        	}else{
        	     websocket = new WebSocket("wss://"+basePath+"/webSocket?meetingId="+currentMeetingId);
        	}
        } else if('MozWebSocket' in window) {
        	if(protocol==="http:"){
        	    websocket = new MozWebSocket("ws://"+basePath+"/webSocket?meetingId="+currentMeetingId);
        	}else{
        	    websocket = new MozWebSocket("wss://"+basePath+"/webSocket?meetingId="+currentMeetingId);
           }
        } else {
            websocket = new SockJS(protocol+"://"+basePath+"/sockjs/webSocket?meetingId="+currentMeetingId);
        }
    	
        bindWebsocketEvent();
    }
    
    
    function retryConnectWebsocket(){
    	setTimeout(function(){
    		console.log('正在重连websocket')
    		initWebsocket();
    	}, 10)
    }
    
    function checkWebsocketState(){
    	if(websocket != null) {
    		if ([websocket.CLOSED, websocket.CLOSING].indexOf(websocket.readyState) > -1) {
    			console.log('websocket closed')
    			retryConnectWebsocket()
    		}
    	} else {
    		console.log('websocket is null')
    	}
    }
    setInterval(checkWebsocketState, 1000)
    
    function pingWebsocket() {
    	if (websocket != null ) {
    		websocket.send('ping');
    		console.log("websocket ping");
    	}
    }
    
    setInterval(pingWebsocket, 30000)
    
   
    // 打开时
    function websocketOnopen(event) {
	   websocket.send('meeting_'+currentMeetingId+"_"+currentUser);
       console.log("  websocket.onopen  " + event);
    }
    
 // 处理消息时
    function websocketOnMessage(event) {
    	var msg = $.parseJSON(event.data);
    	if(msg.type === "transferPresenter"){//接受转让
    		var meetingId = msg.meetingId;
    		var fromUser = msg.fromUser;
    		var fromUserId = msg.fromUserId;
    		layer.alert('授受'+fromUser+'转让过来的主讲', function(index){
    			var url = "${pageContext.request.contextPath}/acceptPresenter";
    			$.ajax({
    	            data: {
    	                "toUser":fromUser,
    	                "toUserId":fromUserId,
    	                "meetingId":meetingId
    	            },
    	            type: "post",
    	            dataType: 'json',
    	            async: false,
    	            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
    	            encoding: "UTF-8",
    	            url: url,
    	            success: function (data) {
    	            	
    	            	console.log("accepted transfer prensenter");
    	            	window.location.reload(true);
    	            	findMettingName();
    	            	
    	            },
    	            error: function (result) {
    	                layer.alert("accept error:"+result.responseText);
    	            },
    	        });
   			}); 
    	}else if(msg.type === "acceptPresenter"){//刷新转让人页面
    		layui.layer.msg('对方已成为主讲人');
    		window.location.reload(true);
    	}else if(msg.type === "updatePresenter"){
    		//主持人变更后通知其余与会人
    		window.location.reload(true);
    	}else if(msg.type === "quitMeeting"){
    		quitMeeting();
    		layer.alert('会诊已结束', function(index){
    			location.href="toDefault?currentUser="+currentUser;
            }); 
    		
    	}else if(msg.type === "applyHost"){//与会人申请主持
    		var meetingId = msg.meetingId;
    		var fromUser = msg.fromUser;  // 申请人名称
    		var fromUserId = msg.fromUserId; // 申请人id
    		var isAssent = 1; //默认同意请求
    		layer.confirm(fromUser+'请求主持权限？', 
    			{
	    		  btn: ['同意', '拒绝'], //可以无限个按钮
				  btn1: function(index, layero){
	   			  	//按钮【同意】的回调
	   			 	confirmTransfer(isAssent, fromUser, fromUserId, meetingId);
	   			  	layer.close(index);
	   			  }, 
	   			  btn2: function(index){
	   			  	//按钮【拒绝】的回调
	   				isAssent = 0;
	   				confirmTransfer(isAssent, fromUser, fromUserId, meetingId);
	   				layer.close(index);
	   			  },
	   				//右上角关闭回调
	   				cancel: function(){
	   			    return false; //开启该代码可禁止点击该按钮关闭
	   			  }
   			});
    		
    		
    	}else if(msg.type === "confirmTransfer"){//主持人确认转让通知申请者，同意或拒绝
    		var isAssent = msg.isAssent;
    		var applyUserId = msg.applyUserId;
    	    //主持人同意转让，刷新申请人界面
    		if(isAssent === '1'){
    			layui.layer.msg('主持人同意转让');
    			window.location.reload(true);
    	    }else{//主持人拒绝转让，给申请人提示
    	    	layer.alert("对方已拒绝",
    	    	{
					closeBtn: 0//没有关闭按钮
				});
    	    }
    	}else if(msg.type === "confirmInvite"){//专家确认邀请通知主持人，同意或拒绝
    		var isAssent = msg.isAssent;
    		var inviteUserId = msg.inviteUserId;
    		//专家信息
    		var userID = msg.userId;
    		var userName = msg.userName;
    	    //专家拒绝加入会诊
    		if(isAssent != '1'){
    	    	layui.layer.msg(userName + "已拒绝");
    	    }
    	}else if(msg.type == "joinMeetingBroadcast"){ //专家进入会诊的广播,面向其他与会人员
    		//专家信息
    		var userID = msg.userId;
    		var userName = msg.userName;
    		layui.layer.msg(userName + "已加入会诊");
    	}else{
    		if(LiveMeeting){
        		LiveMeeting.onSocketMessage(msg);    			
    		}
    	}
    	
    };
    
    function websocketOnerror(event) {
        console.log("  websocket.onerror  ");
    };

    function websocketOnClose(event) {
        console.log("  websocket.onclose  ");
    };
    
    //绑定websocket事件
    function bindWebsocketEvent() {
    	websocket.onopen = websocketOnopen;
    	websocket.onmessage = websocketOnMessage;
    	websocket.onerror = websocketOnerror;
    	websocket.onclose = websocketOnClose;
    }
    
  //初始化websocket
    initWebsocket()
  
  	//主持人确认转换方法
    function confirmTransfer(isAssent, fromUser, fromUserId, meetingId){
    	//发起确认转换请求
		var url = "${pageContext.request.contextPath}/MeetingTools/confirmTransfer";
    	$.ajax({
            data: {
            	"isAssent":isAssent,
            	"toUser":fromUser,
                "toUserId":fromUserId,
                "meetingId":meetingId
            },
            type: "post",
            dataType: 'json',
            async: false,
            contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            encoding: "UTF-8",
            url: url,
            success: function (data) {
            	//如果主持人同意
            	if(isAssent === 1){
                	console.log("主持人同意转让");
                	window.location.reload(true);
                	return;
            	}
            },
            error: function (result) {
                layer.alert("accept error:"+result.responseText);
            },
        });
    	
    	
    }
    
    
    // 点击了发送消息按钮的响应事件
    $("body").on("click",".transfer-presenter",function(){
    	if(isEditMeetingConclusion(this)){
    		return false;
    	} 
    	layui.formSelects.data('transfer-attends-select', 'server', {
            url: '${pageContext.request.contextPath}/MeetingTools/reloadAttendList?meetingId='+currentMeetingId
        });
    	var url = "${pageContext.request.contextPath}/transferPresenter";
    	layer.open({
   		  title:"选择转让对象"
    	  ,type: 1
    	  ,area: ['500px','300px']
   		  ,content: $('#select-transfer-user') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
   		  ,btn: ['确定', '取消']
    	  ,yes: function(index, layero){
    		  var toUser = layui.formSelects.value('transfer-attends-select', 'val');//取值val数组
    		  if(toUser.length === 0){
    			  layer.alert('请选择转让对象'); 
    		  }else{
    			  $.ajax({
                      data: {
                          "toUser":toUser[0],
                          "meetingId":currentMeetingId
                      },
                      type: "post",
                      dataType: 'json',
                      async: false,
                      contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                      encoding: "UTF-8",
                      url: url,
                      success: function (result) {
                          if(result.code=="500"){
                             layui.layer.msg(result.msg);
                          }else{
                             layui.layer.alert("已发送转让请求，等待对方确认.");
                          }
                      },
                      error: function (result) {
                          layer.alert(result.responseText);
                      },
                  });
    			  layui.formSelects.value('transfer-attends-select',toUser, false);
    			  layer.close(index);
    		  }
    	  }
   		});
    });
    /*
		与会者申请做主持人
		apply-host
	*/
	$("body").on("click",".apply-host",function(){
		if(isEditMeetingConclusion(this)){
			return false;
		} 
		var url = "${pageContext.request.contextPath}/MeetingTools/applyHost";
		$.ajax({
	        data: {
	            "meetingId":currentMeetingId
	        },
	        type: "post",
	        dataType: 'json',
	        async: false,
	        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
	        encoding: "UTF-8",
	        url: url,
	        success: function (result) {
	            if(result.code=="500"){
	               layui.layer.msg(result.msg);
	            }else{
	               layui.layer.alert("已向主持人发送申请，等待对方确认.");
	            }
	        },
	        error: function (result) {
	            layer.alert(result.responseText);
	        },
	    });
	});
    
    /*
    	邀请专家
    	invite-expert
    */
    $("body").on("click",".invite-expert",function(){
    	if(isEditMeetingConclusion(this)){
    		return false;
    	}
    	layui.formSelects.data('expert-select', 'server', {
            url: '${pageContext.request.contextPath}/MeetingTools/reloadExpertSelectList?meetingId='+currentMeetingId
        });
    	var url = "${pageContext.request.contextPath}/MeetingTools/inviteExpert";
    	layer.open({
   		  title:"专家邀请列表"
    	  ,type: 1
    	  ,area: ['500px','300px']
   		  ,content: $('#select-expert-user') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
   		  ,btn: ['确定', '取消']
    	  ,yes: function(index, layero){
    		  var toUser = layui.formSelects.value('expert-select', 'val');//取值val数组
    		  if(toUser.length === 0){
    			  layer.alert('请选择需要邀请的专家'); 
    		  }else{
    			  var userStr = "";
    			  if(toUser.length > 1){
    				  userStr = toUser.join(',');
    			  }else{
    				  userStr = toUser[0];
    			  }
    			  
    			  $.ajax({
                      data: {
                          "toUser": userStr,
                          "meetingId": currentMeetingId
                      },
                      type: "post",
                      dataType: 'json',
                      async: false,
                      contentType: "application/x-www-form-urlencoded;charset=UTF-8",
                      encoding: "UTF-8",
                      url: url,
                      success: function (result) {
                          if(result.code=="500"){
                             layui.layer.msg(result.msg);
                          }else{
                             layui.layer.msg("已发送邀请，等待专家确认.");
                          }
                      },
                      error: function (result) {
                          layer.alert(result.responseText);
                      },
                  });
    			  layui.formSelects.value('expert-select',toUser, false);
    			  layer.close(index);
    		  }
    	  }
   		});
    });
    
	
	  //update by and 
	 function getDivPosition(div){
        var x = div.getBoundingClientRect().left;
        var y = div.getBoundingClientRect().top;
        return {x:x,y:y};
    }
    
  //获取当前页面的缩放值
    function ChangeRatio()
	{
	    var ratio=0;
	    var screen=window.screen;
	    var ua=navigator.userAgent.toLowerCase();
	
	    if(window.devicePixelRatio !== undefined)
	    {
	        ratio=window.devicePixelRatio;    
	    }
	    else if(~ua.indexOf('msie'))
	    {
	        if(screen.deviceXDPI && screen.logicalXDPI)
	        {
	            ratio=screen.deviceXDPI/screen.logicalXDPI;        
	        }
	    
	    }
	    else if(window.outerWidth !== undefined && window.innerWidth !== undefined)
	    {
	        ratio=window.outerWidth/window.innerWidth;
	    }
	
	    if(ratio)
	    {
	        ratio=Math.round(ratio*100);    
	    }
	    return ratio;
	}
    /***
     * 获取div的坐标
     * @param divObj
     * @returns {{width: number, height: number, left: *, top: Window}}
     */
    function getElementPos(elementId){
        var ua = navigator.userAgent.toLowerCase();
        var isOpera = (ua.indexOf('opera') != -1);
        var isIE = (ua.indexOf('msie') != -1 && !isOpera); // not opera spoof
        var el = document.getElementById(elementId);
        if (el.parentNode === null || el.style.display == 'none') {
            return false;
        }
        var parent = null;
        var pos = [];
        var box;
        if (el.getBoundingClientRect) //IE
        {
            box = el.getBoundingClientRect();
            var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
            var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
            return {
                x: box.left + scrollLeft,
                y: box.top + scrollTop
            };
        }
        else 
            if (document.getBoxObjectFor) // gecko    
            {
                box = document.getBoxObjectFor(el);
                var borderLeft = (el.style.borderLeftWidth) ? parseInt(el.style.borderLeftWidth) : 0;
                var borderTop = (el.style.borderTopWidth) ? parseInt(el.style.borderTopWidth) : 0;
                pos = [box.x - borderLeft, box.y - borderTop];
            }
            else // safari & opera    
            {
                pos = [el.offsetLeft, el.offsetTop];
                parent = el.offsetParent;
                if (parent != el) {
                    while (parent) {
                        pos[0] += parent.offsetLeft;
                        pos[1] += parent.offsetTop;
                        parent = parent.offsetParent;
                    }
                }
                if (ua.indexOf('opera') != -1 || (ua.indexOf('safari') != -1 && el.style.position == 'absolute')) {
                    pos[0] -= document.body.offsetLeft;
                    pos[1] -= document.body.offsetTop;
                }
            }
        if (el.parentNode) {
            parent = el.parentNode;
        }
        else {
            parent = null;
        }
        while (parent && parent.tagName != 'BODY' && parent.tagName != 'HTML') { // account for any scrolled ancestors
            pos[0] -= parent.scrollLeft;
            pos[1] -= parent.scrollTop;
            if (parent.parentNode) {
                parent = parent.parentNode;
            }
            else {
                parent = null;
            }
        }
        return {
            x: pos[0],
            y: pos[1]
        };
    }


     
    function func7() {
    	var proportion = ChangeRatio()/1.5/100;
    	$('.tx_board_canvas_wrap canvas:eq(1)').attr("id","tx_board_canvas_box_id");
    	var pos=getElementPos('tx_board_canvas_box_id');
    	var pos_head = getElementPos('cvs-head');
    	var pos_head_x= Math.round(pos_head.x * proportion);
    	
    	var pos_body = getElementPos('cvs-body');
    	var pos_body_x =Math.round(pos_body.x * proportion);
    	
    	var x= Math.round(pos.x );
    	var width = Math.round($('#tx_board_canvas_box_id').width());
        var height = Math.round($('#tx_board_canvas_box_id').height());
    	var maxWidth = $(window).width();
    	var maxHeight = $(window).height();
    	var canvWidth = Math.round($('#cvs-body').width());
    	var canvHeight = Math.round($('#cvs-body').height());
    	var canvHeadHeight = Math.round($('#cvs-head').height());
    	if(x < 0 || x < pos_head_x){
    		x =Math.round(pos_head_x+ Math.round(proportion*280)) ;
    	}else if(Math.round(x + width ) < maxWidth ){
    		width = width+20;
    	}
    	var y= Math.round(pos.y)+20;
    	if(y < 0){
    		y = Math.round(pos_body.y)+20 ;
    	}
    	if(width >= canvWidth ){
    		width = canvWidth;
    	}
    	if(height >= canvHeight){
    		height= canvHeight + canvHeadHeight;
    	}
    	if(width >= maxWidth ){
    		width = maxWidth-x;
    	}
    	if(height >= maxHeight){
    		height = maxHeight;
    	}
        var url = "${pageContext.request.contextPath}/MeetingTools/cutPic";
    	//页面层
    	var index =layui.layer.open({
    		title: ['请填写抓拍图的名称和类型', 'font-size:18px;text-align:center;'],
	    	type: 1,
	    	//skin: 'layui-layer-rim', //加上边框	
	    	area: ['500px','340px'], //宽高
	    	content: $('#addScheduleDiv'),
	    	btn: ["保存", "取消"],
	    	//点击确认执行的方法
  		  	yes: function (index, layero) {
  		  	  var fileName = $('#fileName').val();
  		  	  var fileType = layui.formSelects.value('img-type-select', 'val');//取值val
  		  	  if(fileType.length >0){
  		  		fileType = fileType[0];
  		  	  }
  		  	  $.ajax({
  				 url:url,
  	             type:"post",
  	             dataType: 'json',
  	             contentType: "application/x-www-form-urlencoded;charset=UTF-8",
  	             encoding: "UTF-8",
  	             data:{ "meetingId": currentMeetingId,"x":x,"y":y,"width":width,"height":height,"fileName":fileName,"fileType":fileType},
  	             success:function(response){
  	                 if(response.state=="0" && response.upload == true){
  	                     layui.layer.msg("抓拍成功，已保存至云盘");
  	                     //上传图片
  	                    // $("#jietu").attr("src",response.path);
  	                 }else if(response.state=="0" && response.upload == false){
  	                     layui.layer.msg("抓拍抓拍成功，上传至云盘失败："+response.msg);
  	                 }else{
  	                	layui.layer.msg("抓拍失败："+response.msg);
  	                 }
  	             },
  	             error:function(e){
  	                 layui.layer.msg("抓拍出错！！");
  	             }
  	        });
  			//关闭弹出层
            layui.layer.close(index);
  		  	}

    	});
    }
    

    //十六进制颜色值的正则表达式
    var reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/;
    /*RGB颜色转换为16进制*/
    String.prototype.colorHex = function(that){
    	if(/^(rgb|RGB)/.test(that)){
    		var aColor = that.replace(/(?:\(|\)|rgb|RGB)*/g,"").split(",");
    		var strHex = "#";
    		for(var i=0; i<aColor.length; i++){
    			var hex = Number(aColor[i]).toString(16);
    			if(hex === "0"){
    				hex += hex;	
    			}
    			strHex += hex;
    		}
    		if(strHex.length !== 7){
    			strHex = that;	
    		}
    		return strHex;
    	}else if(reg.test(that)){
    		var aNum = that.replace(/#/,"").split("");
    		if(aNum.length === 6){
    			return that;	
    		}else if(aNum.length === 3){
    			var numHex = "#";
    			for(var i=0; i<aNum.length; i+=1){
    				numHex += (aNum[i]+aNum[i]);
    			}
    			return numHex;
    		}
    	}else{
    		return that;	
    	}
    };

    /**window.devicePixelRatio获取像素比**/
    function DPR() {
        if (window.devicePixelRatio && window.devicePixelRatio > 1) {
            return window.devicePixelRatio;
        }
        return 1;
    }

    function createImage(){
    	$('.tx_board_canvas_box canvas:eq(0)').attr("id","tx_board_canvas_box_id");
    	$('.tx_board_canvas_box canvas:eq(1)').attr("id","tx_board_canvas_box_id1");
    	var proportion = ChangeRatio()/1.5/100;
    	var pos=getElementPos('tx_board_canvas_box_id');
    	var pos_head = getElementPos('cvs-head');
    	var pos_head_x= Math.round(pos_head.x * proportion);
    	
    	var pos_body = getElementPos('cvs-body');
    	var pos_body_x =Math.round(pos_body.x * proportion);
    	
    	var x= Math.round(pos.x );
    	var width = Math.round($('#tx_board_canvas_box_id').width());
        var height = Math.round($('#tx_board_canvas_box_id').height());
    	var maxWidth = $(window).width();
    	var maxHeight = $(window).height();
    	var canvWidth = Math.round($('#cvs-body').width());
    	var canvHeight = Math.round($('#cvs-body').height());
    	var canvHeadHeight = Math.round($('#cvs-head').height());
        if(x < 0 || x < pos_head_x){
    		x =Math.round(pos_head_x+ Math.round(proportion*280)) ;
    	}else if(Math.round(x + width ) < maxWidth ){
    		width = width+20;
    	} 
    	var y= Math.round(pos.y)+20;
        if(y < 0){
    		y = Math.round(pos_body.y)+20 ;
    	}
    	if(width >= canvWidth ){
    		width = canvWidth;
    	}
    	if(height >= canvHeight){
    		height= canvHeight + canvHeadHeight;
    	} 
    	/* if(width >= maxWidth ){
    		width = maxWidth-x;
    	}
    	if(height >= maxHeight){
    		height = maxHeight;
    	}  */
    	var baseDiv = document.querySelector('#tx_board_canvas_box_id');
    	var baseDiv1 = document.querySelector('#tx_board_canvas_box_id1');
        var width = baseDiv.offsetWidth;//div宽
        var height = baseDiv.offsetHeight;//div高
        var canvas = document.createElement("canvas");
        var content = canvas.getContext("2d");
        var scale =  DPR()*2;//放大倍数 获取像素比
        canvas.width = width * scale;//canvas宽度
        canvas.height = height * scale;//canvas高度
        var pos=getElementPos('tx_board_canvas_box_id');
        content.scale(scale,scale);
        var rect = baseDiv.getBoundingClientRect();//获取元素相对于视察的偏移量
        content.translate(-rect.left,-rect.top);//设置context位置，值为相对于视窗的偏移量负值，让图片复位
         var url = "${pageContext.request.contextPath}/MeetingTools/cutPic";
         var color =  String.prototype.colorHex($(".tic_board_bg").css("background-color"));
         content.fillStyle = color;
         $("#tx_board_canvas_box_id1").css("background",color);
         html2canvas(baseDiv1, {
        	     dpi: window.devicePixelRatio*scale,
		  		 background: color,
		  		 useCORS: true,
		  		 canvas: canvas,
	              logging: false,//是否输出日志
	              width: width/2,
	              height: height/2
		  	 });
           var index =layui.layer.open({
    		title: ['请填写抓拍图的名称和类型', 'font-size:18px;text-align:center;'],
	    	type: 1,
	    	//skin: 'layui-layer-rim', //加上边框	
	    	area: ['500px','340px'], //宽高
	    	content: $('#addScheduleDiv'),
	    	btn: ["保存", "取消"],
	    	//点击确认执行的方法
  		  	yes: function (index, layero) {
	  		  	  var fileName = $('#fileName').val();
	  		  	  var fileType = layui.formSelects.value('img-type-select', 'val');//取值val
	  		  	  if(fileType.length >0){
	  		  		fileType = fileType[0];
	  		  	  }
	  		  	  var dataUrl = "image/jpeg";
	  		  	  if(fileType == "jpg"){
	  		  		dataUrl = "image/jpeg"
	  		  	  }else if(fileType == "png"){
	  		  		dataUrl = "image/png"
	  		  	  }else{
	  		  		dataUrl = "image/bmp"
	  		  	  }
	  		  	 
	  		  	 html2canvas(baseDiv, {
	  		  		 dpi: window.devicePixelRatio*scale,
	  		  		 background: color,
	  		  		 allowTaint :false,//表示是否允许被污染,而被污染的canvas是没法使用toDataURL()转base64流的,但是我们这需要base64,所有allowTaint需要被设置为false
	  		  		 taintTest: true,
	  		  		 useCORS: true,
	  		  		 canvas: canvas,
	                 logging: false,//是否输出日志
	                 width: width,
	                 height: height,
	                 x: x,
	                 y: y
	  		  	 }).then(canvas =>{
	  		  		//document.body.appendChild(canvas);
	  		  		var dataImg = canvas.toDataURL(dataUrl);
	                 var parts = dataImg.split(';base64,');
	                 var myImage=parts[1];
	                 $.ajax({
	                     type: "post",
	                     dataType: "json",
	                     url: url,
	                     contentType: "application/x-www-form-urlencoded;charset=UTF-8",
	                     data : {"meetingId": currentMeetingId,"data":myImage,"fileName":fileName,"fileType":fileType,"ratio":proportion},  
	                     success:function(response){
		   	                 if(response.state=="0" && response.upload == true){
		   	                     layui.layer.msg("抓拍成功，已保存至云盘");
		   	                     //上传图片
		   	                    // $("#jietu").attr("src",response.path);
		   	                 }else if(response.state=="0" && response.upload == false){
		   	                     layui.layer.msg("抓拍抓拍成功，上传至云盘失败："+response.msg);
		   	                 }else{
		   	                	layui.layer.msg("抓拍失败："+response.msg);
		   	                 }
	                     },
	      	             error:function(e){
	      	                 layui.layer.msg("抓拍出错！！");
	      	             }
	                 });
	                 
	  		  	 })
	  		  	 $("#tx_board_canvas_box_id1").css("background","");
 	  		 //关闭弹出层
 	           layui.layer.close(index);
 	 		 }
	  	});
  		  	  
    }
    
    /*
		抓拍
		capture
	*/
	$("body").on("click",".capture",function(){
		createImage();
		/* var $canvas = $('canvas');
        var $video = $('#liveVideo_html5_api');
        func7(); */
	});
    
    //提交会诊信息
    form.on('submit(save)', function(data){
   	  //console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}
   	  $.post("saveMeetingInfo",data.field,function(result){
   		  if(result.code==="0"){
   			  layer.alert(result.msg);
   		  }else{
   			  layer.alert(result.msg);
   		  }
   		isShowMeetingInfo();
         },"json");
   	  
   	  return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
   	});
    //分享会诊信息
    form.on('submit(share)', function(data){
    	if(isEditMeetingConclusion(this)){
    		return false;
    	}
    	var afterConclusion = $("#meeting-info-form").find("[name='meetingConclusion']").val();
    	if(!afterConclusion){
    		layer.alert("会诊结论为空，不能分享");
    		return false;
    	}
      $.post("shareMeetingInfo",data.field,function(result){
          if(result.code==="0"){
              layer.alert(result.msg);
          }else{
              layer.alert(result.msg);
          }
         },"json");
      
      return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

      EventUtils.onChangePresenter(function(){
          //layer.msg("对方已成为主讲人");
          window.location.reload();
      },function(){
          //layer.msg("您已成为主讲人");
          window.location.reload();
      })

  });
  
/*
    EventUtils.onQuitMeeting(function(){
        //主持人退出回调函数
        //layer.msg("主持人退出回调函数");
        location.href="toDefault?currentUser="+currentUser;

    }, function(){
        // layer.msg("其他人退出回调函数");
        layer.msg("会诊已结束")
        setTimeout(function(){
            location.href="toDefault?currentUser="+currentUser;
        }, 1500)

    })
*/

    //点击事件完成后隐藏弹框
    $("body").on("click", ".close-li>ul>li", function() {
        $(".close-li").hide();
    });

    var id="${meetingInfo['id']}";
    
    $("body").on("click",".fileuploadform-info",function(){
  		layer.open({
 	          type: 2, 
 	          content: 'fileUploadForm?id='+id,
 	          title:'文件列表',
 	          area: ['800px', '600px'],
 	          response:{
 		    	  statusName: 'status',
 		    	  statusCode: 200
 		      }
  	     });
    });
    
  findMettingName = function(){
	  $.ajax({
  		url:"${pageContext.request.contextPath}/findMeetingName",
  		type:"post",
  		data:"{meetingId:meetingId}",
  		success:function(data){
  			
  		}
  	})
  }
  
  function keepServletSession() {
	  setInterval(function(){
		  if (typeof(getMeetingInfo) != 'undefined') {
			  getMeetingInfo(function(){
				  console.log('保持servelet session')
			  })
		  }
	  }, 1000*60)
  }
  keepServletSession()
</script>
<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<div style="display: none">
<span class="font-family-HWKT">华文楷体</span>
<span class="font-family-HWXK">华文行楷</span>
<span class="font-family-SONGTI">宋体</span>
<span class="font-family-WRYHC">微软雅黑</span>
</div>
</body>
</html>