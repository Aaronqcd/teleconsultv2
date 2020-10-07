<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>测试屏幕分享</title>
<script src="../js/jquery-7.min.js"></script>
<script src="../js/trtc/trtc.js"></script>
<script src="../js/test/screenshare.js"></script>
<style>
.main {
	display: flex;
	justify-content: space-between;
}

.col1 {
}

.section {
	margin: 5px 0px;
}

button {
	padding: 5px;
	border: solid 1px #ccc;
	background-color: white;
}

.btn-green {
	background-color: green;
	color: white;
}

.btn-red {
	background-color: red;
	color: white;
}

.player {
	width: 600px;
	height: 400px;
	background-color: #ccc;
}

.gapv {
	margin: 10px 0px;
}
</style>
</head>
<body>
	<div class="main">
		<div class="col1">
			<h3>主播端-本地流</h3>
			<div id="mainVideo" class="player"></div>
		</div>
		<div class="col1">
			<h3>观众端-远程流</h3>
			<div id="userVideo" class="player"></div>
		</div>
	</div>
	<div>
		<div class="gapv">
			<button id="btnShare" class="btn-green">开始屏幕分享</button>
			<button id="btnInfo">环境信息</button>
		</div>
		<div id="info"></div>
	</div>
</body>
</html>