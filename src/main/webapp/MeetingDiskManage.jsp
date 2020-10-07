<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name=renderer content=webkit>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title>远程会诊管理系统</title>
  <link rel="stylesheet" type="text/css" href="layui-v2.4.5/css/layui.css"><!-- layui.css-->
  <link rel="stylesheet" type="text/css" href="css/common.css"><!-- 自定义css-->
  <link href="css/bootstrap.css" rel="stylesheet"><!-- bootstrap.css-->
	<!-- 全局样式 -->
	<link rel="stylesheet" href="css/overrall.min.css">
	<!-- 图片查看器插件 -->
	<link rel="stylesheet" href="css/viewer.min.css">
	<!-- 音乐播放器插件 -->
	<link rel="stylesheet" href="css/APlayer.min.css">
	<!-- 对旧浏览器的支持部分... -->
	<!--[if lt IE 9]>
	      <script src="js/html5shiv.min.js"></script>
	      <script src="js/respond.min.js"></script>
	<![endif]-->
	<style type="text/css">
	
		div.ui-body{
			position: absolute;
		    left: 240px;
		    right: 0;
		    top: 0;
		    bottom: 0;
		    /* z-index: 998; */
		    width: auto;
		    overflow: hidden;
		    overflow-y: auto;
		    box-sizing: border-box;
		}
		
		h4.modal-title{
			margin:10px 0 0 0;
		}
		
		#itemAll{
		    width: 15px;
   			height: 16px;
   			border: 1px solid #d2d2d2;
		    font-size: 11px;
		    border-radius: 2px;
		    background-color: #fff;
		    margin: 8px 0 0 6px;
		}
		.items{
			width: 14px;
   			height: 16px;
   			border: 1px solid #d2d2d2;
		    font-size: 11px;
		    border-radius: 2px;
		    background-color: #fff;
		    margin: 8px 0 0 6px;
		}
		
		.btn-xs{
			font-size: 14px;
			padding: 1px 0px;
			color: #666;
			
		}
		
		.glyphicon-folder-close{
			color: #06b4bf;
		}
		
		h4{
			font-size: 14px;
		}
		
		.modal-content {

		    border: 0px;
		    /* border: 1px solid rgba(0, 0, 0, 0.2); */
		    border-radius: 6px;
		}
		
		body{
			color: #666;
		}
		
		.navbar-btn{
    		/* margin-bottom: 8px; */
    		margin: 10px 8px 0 0;
		}
		
		.navbar-btn li a{
			padding: 0px;
			line-height: 10px;
		}
		
		#firstth{
			color: #fff;
			background-color: #06b4bf;
		}
		
		.layui-btn-sm {
		    height: 22px;
		    line-height: 22px;
		    padding: 0 5px;
		    font-size: 12px;
		}
		
		.alert-danger {
		    color: #666;
		    background-color: #fff;
		    border-color: #666;
		}
	</style>
</head>
<body>
<!-- 左侧 -->
<div class="layui-side layui-bg-black" style="width: 240px;border-right: 1px solid #ddd;">
  <div class="layui-side-scroll" style="background: #fff;width: 260px">
    <div id="testTree" class="tree-hospital" style="width: 240px;margin-top: 15px;"></div>
  </div>
</div>
<!-- 右侧 -->
<div class="ui-body"  style="background:#f5f5f5;">
  <div class="user-right-content" id="user-right-content">
    <!-- 显示主体 -->
	<div class="container" style="width: 100%; padding-right: 0px; padding-left: 0px; height: calc(100vh);">
		<!-- 信息栏、操作栏与文件列表 -->
		<div class="row">
			<div class="col-md-12">
				<div id="filetable" class="panel panel-default" unselectable="on"
					onselectstart="return false;" style="-moz-user-select: none;">
					<!-- 文件列表头部，也就是操作栏 -->
					<div class="panel-heading" style="background-color: #fff; padding: 10px 0px; ">
						<div class="heading">
							<div class="navbar-header">
								<a id="filetableheadera" href="javascript:void(0);"
									class="navbar-text" data-toggle="modal"
									data-target="#folderInfoModal" style="color: #666;"><span id="folderIconSpan"
									class="glyphicon glyphicon-folder-close" style="color:#06b4bf;"></span>&nbsp;<span
									id="currentFolderName"></span> <span id="mdropdownicon"></span></a>
							</div>
							<div class="collapse navbar-collapse" id="filetableoptmenu">
								<!-- <ul class="nav navbar-nav">
									<li class="dropdown"><a href="javascript:void(0);"
										class="dropdown-toggle" data-toggle="dropdown" role="button"
										aria-haspopup="true" aria-expanded="false" style="color:#fff;">上一级 <span
											class="caret"></span></a>
										<ul class="dropdown-menu" id="parentFolderList"></ul></li>
								</ul> -->
								<form id="filetableoptmenusreach"
									class="navbar-form navbar-left">
									<div class="form-group">
										<input id="sreachKeyWordIn" type="text" class="form-control"
											placeholder="请输入文件名...">
									</div>
									<button type="button" class='navbar-btn layui-btn layui-btn-sm layui-btn-radius tc-bg-main' style='margin: 0px 8px 0px 0px; height: 30px;line-height: 30px;padding: 0 10px;font-size: 12px;'
										onclick="doSearchFile()">目录搜索</button>
									<button type="button" class='navbar-btn layui-btn layui-btn-sm layui-btn-radius tc-bg-main' style='margin: 0px 8px 0px 0px; height: 30px;line-height: 30px;padding: 0 10px;font-size: 12px;'
										onclick="doSearchAllFile()">全盘搜索</button>
								</form>
								<ul class="nav navbar-nav navbar-right">
									<li id="packageDownloadBox"></li>
									<li id="createFolderButtonLi">
										<a class='navbar-btn layui-btn layui-btn-sm layui-btn-radius tc-bg-main' style='color:#fff; display: inline-block;line-height: 0px'>
										新建文件夹 </a></li>
									</li>
									<li id="uploadFileButtonLi">
										<a class='navbar-btn layui-btn layui-btn-sm layui-btn-radius tc-bg-main' style='color:#fff; display: inline-block;line-height: 0px'>
										上传文件 </a>
									</li>
									<li id="cutFileButtonLi">
										<a class='navbar-btn layui-btn layui-btn-sm layui-btn-radius tc-bg-main' style='color:#fff; display: inline-block;line-height: 0px'>
										<span id='cutSignTx'>剪切</span></a>
									</li>
									<li id="deleteSeelectFileButtonLi">
										<a class='navbar-btn layui-btn layui-btn-sm layui-btn-radius tc-bg-main' style='color:#fff; display: inline-block;line-height: 0px'>
										删除 </a>
									</li>
									<!-- <li class="dropdown" id="operationMenuBox"
										data-toggle="popover"><a href="javascript:void(0)" class="dropdown-toggle"
										data-toggle="dropdown" role="button" aria-haspopup="true"
										aria-expanded="false" style="color:#fff;"><span
											class="glyphicon glyphicon-cog" style="color:#fff;"></span> 操作 <span
											class="caret"></span></a>
										<ul class="dropdown-menu" id="fileListDropDown">
											<li id="createFolderButtonLi"><a>新建文件夹 <span
													class="pull-right"><span
														class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>+N</span></a></li>
											<li role="separator" class="divider"></li>
											<li id="uploadFileButtonLi"><a>上传文件 <span
													class="pull-right"><span
														class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>+U</span></a></li>
											<li role="separator" class="divider"></li>
											<li id="cutFileButtonLi"><a><span id='cutSignTx'>剪切
														<span class="pull-right"><span
															class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>+C</span>
												</span></a></li>
											<li id="deleteSeelectFileButtonLi"><a>删除 <span
													class="pull-right"><span
														class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>+D</span></a></li>
										</ul></li> -->
								</ul>
							</div>
						</div>
					</div>
					<div class="panel-heading" id="navHeading" style="background-color: #fff;">
						<div class="heading">
							<div class="collapse navbar-collapse" id="filetablenav" style="padding-left: 0px;">
								
							</div>
						</div>
					</div> 
					<table class="table table-hover" id="u" style="border: 2px;">
						<thead>
							<tr id="firstth">
								<th style="padding: 0px 0px 2px 8px;"><input id="itemAll" type="checkbox" /></th>
								<th >文件名<span id="sortByFN"
									aria-hidden="true" style="float: right"></span></th>
								<th class="hiddenColumn" >创建日期<span
									id="sortByCD" aria-hidden="true" style="float: right"></span></th>
								<th >大小<span id="sortByFS"
									aria-hidden="true" style="float: right"></span></th>
								<th class="hiddenColumn" >创建者<span
									id="sortByCN" aria-hidden="true" style="float: right"></span></th>
								<th onclick="showOriginFolderView()">操作</th>
							</tr>
						</thead>
						<tbody id="foldertable"></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<!-- end 显示主体 -->

	<!-- end 注销 -->
	<!-- 新建文件夹框 -->
	<div class="modal " id="newFolderModal" tabindex="-1" role="dialog"
		aria-labelledby="newFolderlMolderTitle">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close" >
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="newFolderlMolderTitle">
						<span class="glyphicon glyphicon-folder-open"></span> 新建文件夹
					</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal">
						<div class="form-group" id="foldernamebox">
							<label for="folderid" id="foldernametitle"
								class="col-sm-3 control-label">新建文件夹：</label>
							<div class="col-sm-9">
								<div class="input-group">
									<input type="text" class="form-control" id="foldername"
										placeholder="请输入新文件夹的名称……" folderConstraintLevel="0">
									<!-- <div class="input-group-btn">
										<button type="button" class="btn btn-default dropdown-toggle"
											data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false">
											&nbsp;<span id="newfoldertype">公开的</span>&nbsp;<span
												class="caret"></span>
										</button>
										<ul id="foldertypelist"
											class="dropdown-menu dropdown-menu-right">
										</ul>
									</div> -->
								</div>
							</div>
						</div>
						<div id="folderalert" role="alert"></div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<button type="button" class="btn btn-primary"
						onclick="createfolder()" style="border-color: #06b4bf!important;background-color: #06b4bf!important;">创建</button>
				</div>
			</div>
		</div>
	</div>
	<!-- end 创建文件夹 -->
	<!-- 删除文件夹提示框 -->
	<div class="modal  bs-example-modal-sm" id="deleteFolderModal"
		tabindex="-1" role="dialog" aria-labelledby="deleteFolderModelTitle">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="deleteFolderModelTitle">
						<span class="glyphicon glyphicon-comment"></span> 删除文件夹
					</h4>
				</div>
				<div class="modal-body">
					<h5 id="deleteFolderMessage"></h5>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<span id="deleteFolderBox" ></span>
				</div>
			</div>
		</div>
	</div>
	<!-- end 删除文件夹 -->
	<!-- 重命名文件夹框 -->
	<div class="modal " id="renameFolderModal" tabindex="-1"
		role="dialog" aria-labelledby="renameFolderMolderTitle">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="renameFolderMolderTitle">
						<span class="glyphicon glyphicon-wrench"></span> 重命名文件夹
					</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal">
						<div class="form-group" id="folderrenamebox">
							<label for="folderid" id="foldernametitle"
								class="col-sm-3 control-label">重命名文件夹：</label>
							<div class="col-sm-9">
								<div class="input-group">
									<input type="text" class="form-control" id="newfoldername"
										placeholder="请输入文件夹的名称……" folderConstraintLevel="0">
									<!-- <div class="input-group-btn">
										<button type="button" class="btn btn-default dropdown-toggle"
											data-toggle="dropdown" aria-haspopup="true"
											aria-expanded="false">
											&nbsp;<span id="editfoldertype">公开的</span>&nbsp;<span
												class="caret"></span>
										</button>
										<ul id="editfoldertypelist"
											class="dropdown-menu dropdown-menu-right">
										</ul>
									</div> -->
								</div>
							</div>
						</div>
						<div id="newfolderalert" role="alert"></div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<span id="renameFolderBox" ></span>
				</div>
			</div>
		</div>
	</div>
	<!-- end 重命名文件夹 -->
	<!-- 上传文件框 -->
	<div class="modal " id="uploadFileModal" tabindex="-1"
		role="dialog" aria-labelledby="uploadFileMolderTitle">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="uploadFileMolderTitle">
						<span class="glyphicon glyphicon-cloud-upload"></span> 上传文件
					</h4>
				</div>
				<div class="modal-body">
					<h5>
						选择文件：<span id="selectcount"></span>
					</h5>
					<input type="text" id="filepath" class="form-control"
						onclick="checkpath()" onfocus="this.blur()"
						placeholder="请点击选择要上传的文件……"> <input type="file"
						id="uploadfile" style="display: none;" onchange="getInputUpload()"
						multiple="multiple"> <br />
					<h5>
						上传进度：<span id="filecount"></span>
					</h5>
					<div class="progress">
						<div id="pros" class="progress-bar" role="progressbar"
							aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"
							style="width: 0%;">
							<span class="sr-only"></span>
						</div>
					</div>
					<h5>上传状态：</h5>
					<div class="panel panel-default">
						<div class="panel-body">
							<div id="uploadstatus" class="uploadstatusbox"></div>
						</div>
					</div>
					<div id="uploadFileAlert" role="alert"></div>
					<div id="selectFileUpLoadModelAlert" class="alert alert-danger"
						role="alert" style="background-color: #fff; color: #666;">
						<h4>提示：存在同名文件！</h4>
						<p>
							您要上传的文件“<span id="repeFileName"></span>”已存在于该路径下，您希望：
						</p>
						<p>
							<input id="selectFileUpLoadModelAsAll" type="checkbox">
							全部应用
						</p>
						<p>
							<button id="uploadcoverbtn" type="button"
								class="btn btn-danger btn-sm" style="border-color: #06b4bf!important;background-color: #06b4bf!important;"
								onclick="selectFileUpLoadModelEnd('cover')">覆盖</button>
							<button type="button" class="btn btn-default btn-sm"
								onclick="selectFileUpLoadModelEnd('skip')">跳过</button>
							<button type="button" class="btn btn-default btn-sm"
								onclick="selectFileUpLoadModelEnd('both')">保留两者</button>
						</p>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"
						onclick='abortUpload()'>取消</button>
					<button id="umbutton" type='button' class='btn btn-primary'
						onclick='checkUploadFile()' style="border-color: #06b4bf!important;background-color: #06b4bf!important;">开始上传</button>
				</div>
			</div>
		</div>
	</div>
	<!-- end 上传文件 -->
	<!-- 下载提示框 -->
	<div class="modal " id="downloadModal" tabindex="-1" role="dialog"
		aria-labelledby="downloadModelTitle">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="downloadModelTitle">
						<span class="glyphicon glyphicon-cloud-download"></span> 下载
					</h4>
				</div>
				<div class="modal-body">
					<h5 id="downloadFileName" class="wordbreak">提示：您确认要下载文件：[]么？</h5>
					<a href="javascript:void(0);"
						onclick="$('#downloadURLCollapse').collapse('toggle')">下载链接+</a>
					<div class="collapse" id="downloadURLCollapse">
						<div id="downloadHrefBox" class="well well-sm wordbreak"></div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<span id="downloadFileBox"></span>
				</div>
			</div>
		</div>
	</div>
	<!-- end 下载 -->
	<!-- 删除提示框 -->
	<div class="modal  bs-example-modal-sm" id="deleteFileModal"
		tabindex="-1" role="dialog" aria-labelledby="deleteFileModelTitle">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="deleteFileModelTitle">
						<span class="glyphicon glyphicon-comment"></span> 删除文件
					</h4>
				</div>
				<div class="modal-body">
					<h5 id="deleteFileMessage" class="wordbreak"></h5>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<span id="deleteFileBox" ></span>
				</div>
			</div>
		</div>
	</div>
	<!-- end 删除提示框 -->
	<!-- 重命名框 -->
	<div class="modal  bs-example-modal-sm" id="renameFileModal"
		tabindex="-1" role="dialog" aria-labelledby="renameFileMolderTitle">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="renameFileMolderTitle">
						<span class="glyphicon glyphicon-wrench"></span> 重命名文件
					</h4>
				</div>
				<div class="modal-body">
					<form class="form-horizontal">
						<div class="form-group" id="filerenamebox">
							<label for="folderid" id="filenametitle"
								class="col-sm-2 control-label">名称：</label>
							<div class="col-sm-9">
								<input type="text" class="form-control" id="newfilename"
									placeholder="请输入文件的名称……">
							</div>
						</div>
						<div id="newFileNamealert" role="alert"></div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<span id="renameFileBox"></span>
				</div>
			</div>
		</div>
	</div>
	<!-- end 重命名 -->
	<!-- 打包下载提示框 -->
	<div class="modal  bs-example-modal-sm"
		id="downloadAllCheckedModal" tabindex="-1" role="dialog"
		aria-labelledby="downloadAllCheckedModalTitle">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="downloadAllCheckedModalTitle">
						<span class="glyphicon glyphicon-cloud-download" ></span> 打包下载
					</h4>
				</div>
				<div class="modal-body">
					<h5>
						<span id="downloadAllCheckedName"></span><span
							id="downloadAllCheckedLoad" style="text-align: center;"></span>
					</h5>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<span id="downloadAllCheckedBox"></span>
				</div>
			</div>
		</div>
	</div>
	<!-- end 打包下载 -->
	<!--音乐播放模态框-->
	<div class="modal " id="audioPlayerModal" tabindex="-1"
		role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-body">
					<div id="aplayer" class="aplayer"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"
						onclick="audio_vulome_down()">
						<span class="glyphicon glyphicon-volume-down" aria-hidden="true"></span>
					</button>
					<button type="button" class="btn btn-default"
						onclick="audio_vulome_up()">
						<span class="glyphicon glyphicon-volume-up" aria-hidden="true"></span>
					</button>
					<button type="button" class="btn btn-default" onclick="audio_bw()">
						<span class="glyphicon glyphicon-backward" aria-hidden="true"></span>
					</button>
					<button type="button" class="btn btn-primary" id="playOrPause"
						onclick="audio_playOrPause()">
						<span class='glyphicon glyphicon-play' aria-hidden='true'></span>
					</button>
					<button type="button" class="btn btn-default" onclick="audio_fw()">
						<span class="glyphicon glyphicon-forward" aria-hidden="true"></span>
					</button>
					<button type="button" class="btn btn-default"
						onclick="closeAudioPlayer()">
						<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
					</button>
				</div>
			</div>
		</div>
	</div>
	<!-- end 音乐播放 -->
	<!-- 加载提示框 -->
	<div id="loadingModal" class="modal  bs-example-modal-sm"
		tabindex="-1" role="dialog" aria-labelledby="page is loading">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content loading">加载中...</div>
		</div>
	</div>
	<!-- end 加载提示框 -->
	<!-- 移动文件提示框 -->
	<div class="modal  bs-example-modal-sm" id="moveFilesModal"
		tabindex="-1" role="dialog" aria-labelledby="moveFolderModalTitle">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="moveFolderModalTitle">
						<span class="glyphicon glyphicon-import"></span> 移动文件
					</h4>
				</div>
				<div class="modal-body">
					<h5 id="moveFilesMessage"></h5>
					<div id="selectFileMoveModelAlert" class="alert alert-danger"
						role="alert">
						<h4>提示：存在同名文件！</h4>
						<p>
							您要移动的文件“<span id="mrepeFileName"></span>”已存在于该路径下，您希望：
						</p>
						<p>
							<input id="selectFileMoveModelAsAll" type="checkbox">
							全部应用
						</p>
						<p>
							<button id="movecoverbtn" type="button"
								class="btn btn-danger btn-sm"
								onclick="selectFileMoveModel('cover')" style="border-color: #06b4bf!important;background-color: #06b4bf!important;">覆盖</button>
							<button type="button" class="btn btn-default btn-sm"
								onclick="selectFileMoveModel('skip')">跳过</button>
							<button type="button" class="btn btn-default btn-sm"
								onclick="selectFileMoveModel('both')">保留两者</button>
						</p>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
					<span id="moveFilesBox"></span>
				</div>
			</div>
		</div>
	</div>
	<!-- end 移动文件提示框 -->
	<!-- 文件夹详情模态框 -->
	<div class="modal " id="folderInfoModal" tabindex="-1"
		role="dialog" aria-labelledby="Folder Informaction Modal">
		<div class="modal-dialog modal-sm" role="document">
			<div class="modal-content">
				<div class="modal-header layui-layer-title">
					<button type="button" class="close" style="margin: 10px 0px 0px 0px;width: 16px;height: 16px;"  data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="myModalLabel">详细信息</h4>
				</div>
				<div class="modal-body">
					<dl>
						<dt>文件夹名称：</dt>
						<dd id="fim_name"></dd>
						<dt>创建者：</dt>
						<dd id="fim_creator"></dd>
						<dt>创建时间：</dt>
						<dd id="fim_folderCreationDate"></dd>
						<dt>文件统计：</dt>
						<dd id="fim_statistics"></dd>
					</dl>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal" style="border-color: #06b4bf!important;background-color: #06b4bf!important;">确定</button>
				</div>
			</div>
		</div>
	</div>
	<!-- end 文件夹详情模态框 -->
	<!-- 返回顶部按钮（隐藏式） -->
	<div id="gobacktotopbox" class="gobacktopbox text-center hidden">
		<button type="button" onclick="goBackToTop()" class="gobacktopbutton">
			返回顶部 <span class="glyphicon glyphicon-eject" aria-hidden="true"></span>
		</button>
	</div>
	<!-- end 返回顶部按钮 -->
	</div>

</div>


<!-- 让IE8/9支持媒体查询，从而兼容栅格 -->
<!--[if lt IE 9]>
  <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
  <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
<![endif]-->
<!-- jquery基本框架 -->
<script type="text/javascript" src="js/jquery-1.min.js"></script>
<!-- bootstrap基本框架 -->
<script type="text/javascript" src="js/bootstrap.js"></script>
<!-- 加密插件 -->
<script type="text/javascript" src="js/jsencrypt.min.js"></script>
<!-- 图片查看器 -->
<script type="text/javascript" src="js/viewer.min.js"></script>
<script type="text/javascript" src="js/jquery-viewer.min.js"></script>
<!-- 音乐播放器 -->
<script type="text/javascript" src="js/APlayer.min.js"></script>
<script src="layui-v2.4.5/layui.js"></script><!-- layui.js -->
<script src="js/bootstrap-treeview.js"></script><!-- 树形插件 -->
<!-- 页面操作定义 -->
<script type="text/javascript" src="js/home.js?v=1"></script>

<script type="text/javascript">

var userId = '${user.id}';
var userAccount = '${user.user}';
var userName = '${user.name}';
var rootFolderId = '${rootFolder.folderId}';
//单位字节
var maxUploadSize = parseInt('${maxUploadSize}');
//单位M
var maxSizeStr = bytesToSize(maxUploadSize);
//云盘大小 单位字节
var diskSize = parseInt('${diskSize}');

var diskSizeStr = bytesToSize(diskSize);

function reloadTree(){
	$.get("/teleconsult/homeController/getFolderTreeJson.ajax",
		function(result) {
			$('#testTree').treeview({
				color : "#666",
				expandIcon : 'glyphicon glyphicon-chevron-right',
				collapseIcon : 'glyphicon glyphicon-chevron-down',
				nodeIcon : "glyphicon glyphicon-bookmark",
				selectedBackColor : "#d4f9fb",
				showBorder : false,
				selectedColor : "#666",
				showTags : true,
				data : result,
				onNodeSelected : function(event, data) {
					console.log(data);
					showFolderView(data.id);
					/* me.checkTreeDoubleClick(event, data); */
				},
				onNodeUnselected : function(event, data) {
					/* me.checkTreeDoubleClick(event, data); */
				}
			});
		});
}

/* var interval;
interval = window.setInterval(function(){
	reloadTree();
},10000); */

$(function(){
	showFolderView(rootFolderId);
	reloadTree();
	$("#itemAll").change(function() { 
		var chk = $(this).is(':checked');
		console.log(chk);
		$('.items').each(function(){
		    $(this).prop({checked:chk});
		    if(chk){
		    	$(this).parent().parent().addClass('info');
		    }else{
		    	$(this).parent().parent().removeClass('info');
		    }
		    console.log($(this).parent().parent());
		}); 
	});
}) 

</script>
</body>
</html>