var LiveMeeting = LiveMeeting || {
	lastClickTime : null,
	lastClickId : null,
	monitor : null,
	player : null,
	rtc : {
		api : null,
		status : -1, // -2=不支持 -1=待检测 0=已停止 1=已开始
		sharing : false
	},
	data : {
		id : parseInt(Utility.getUrlParam("meetingId")),
		user : parseInt(Utility.getUrlParam("userid")),
		isPresenter : $("#hdPresenter").val() == "true",
		device : null,
		channel : null,
		roomName : null,
		channelName : null,
		deviceStatus : 0, // 设备状态 0=关闭 1=打开中 2=已打开 3=关闭中
		pushUrl : null,
		playUrl : null,
		resId : null, // 资源ID
		resType : null, // 资源类型
	}
};

LiveMeeting.init = function() {
	var me = this;
	$("#btnShare").click(LiveMeeting.toggleShare);
	this.loadResourceTree();
	// 暂停x秒,等电子白板初始完毕后再继续
	var fnDelay = function() {
		if (window.BoardInited) {
			me.initCanvasTab();
			me.initShare();
			me.checkLive();
			if (!me.data.isPresenter) {
				me.monitor = setInterval(me.checkLive, 3000, me);
				setTimeout(me.viewAsk, 3000);
			}
		} else {
			setTimeout(fnDelay, 2000);
		}
	};
	setTimeout(fnDelay, 2000);
};

/* 画布切换 */
LiveMeeting.initCanvasTab = function() {
	var me = this;
	if (me.data.isPresenter) {
		$("#tabView > li").click(function() {
			me.goTabView($(this).attr("data-tab"));
		});
		$("#tabView > li:eq(0)").click();
	}
}

LiveMeeting.goTabView = function(to) {
	// 隐藏已购选视图
	var check = $("#tabView").attr("data-tab-check");
	if (check) {
		this.toggleTabView(check);
	}
	var from = $("#tabView").attr("data-tab");
	if (from == to)
		return;
	if (from) {
		$("#cvs-head div[data-tab='" + from + "']").hide();
		$("#cvs-body div[data-tab='" + from + "']").hide();
	}
	$("#cvs-head div[data-tab='" + to + "']").show();
	$("#cvs-body div[data-tab='" + to + "']").show();
	$("#tabView").attr("data-tab", to);
	if (to != "info") {
		var boradDiv = $("#content_borad").css("display");
		if(to == "board" || boradDiv != "none"){
        	$(".carousel-control-prev").show();
        	$(".carousel-control-next").show();
		} else {
        	$(".carousel-control-prev").hide();
        	$(".carousel-control-next").hide();
		}
		
		if (to == "board") {        	
			$("#boardTools").show();
			$("#content_borad").show();
			layui.element.tabChange("layTabView", "tabTool");
		} else {        	
			$("#boardTools").hide();
			$("#content_borad").hide();
			layui.element.tabChange("layTabView", "tabMix");
		}       
		
	}
	this.viewNotify();
}

LiveMeeting.goTabView2 = function(to) {
	this.goTabView("mix");
	var from = $("#content_mix").attr("data-tab2");
	if (from) {
		$("#cvs-head div[data-tab='" + from + "']").hide();
		$("#content_mix div[data-tab='" + from + "']").hide();
	}
	$("#cvs-head div[data-tab='" + to + "']").show();
	$("#content_mix div[data-tab='" + to + "']").show();
	$("#content_mix").attr("data-tab2", to);
	this.viewNotify();
}

LiveMeeting.toggleTabView = function(to, force) {
	var tab1 = $("#tabView").attr("data-tab");
	var tab2 = $("#tabView").attr("data-tab-check");
	if (tab2 == to || force == false) {
		if (tab1) {
			$("#cvs-head div[data-tab='" + tab1 + "']").show();
			$("#cvs-body div[data-tab='" + tab1 + "']").show();
		}
		$("#cvs-head div[data-tab='" + to + "']").hide();
		$("#cvs-body div[data-tab='" + to + "']").hide();
		$("#tabView").attr("data-tab-check", "");
		if (to == "info") {
			$(".meeting-info").removeClass("select_button");
		}
	} else {
		if (tab1) {
			$("#cvs-head div[data-tab='" + tab1 + "']").hide();
			$("#cvs-body div[data-tab='" + tab1 + "']").hide();
		}
		$("#cvs-head div[data-tab='" + to + "']").show();
		$("#cvs-body div[data-tab='" + to + "']").show();
		$("#tabView").attr("data-tab-check", to);
		if (to == "info") {
			$(".meeting-info").attr("isShow", "false");
			$(".meeting-info").removeClass("select_button").addClass(
					"select_button");
		}
	}
}

/* 加载资源树 */
LiveMeeting.loadResourceTree = function() {
	var me = this;
	$.get("/teleconsult/meetingrestree?currentId=" + $("#currentOrg").val(),
			function(result) {
				$('#treeResource').treeview({
					color : "#666",
					expandIcon : 'glyphicon glyphicon-chevron-right',
					collapseIcon : 'glyphicon glyphicon-chevron-down',
					nodeIcon : "glyphicon glyphicon-bookmark",
					selectedBackColor : "#d4f9fb",
					showBorder : false,
					selectedColor : "#666",
					showTags : false,
					data : result,
					onNodeSelected : function(event, data) {
						me.onTreeNodeClick(event, data);
						me.checkTreeDoubleClick(event, data);
					},
					onNodeUnselected : function(event, data) {
						me.checkTreeDoubleClick(event, data);
					}
				});
			});
}

/* 查询病例窗口 */
LiveMeeting.showPatientDialog = function(resType, resId) {
	layer.open({
		type : 2,
		content : 'patient/select?restype=' + resType + '&resid=' + resId,
		title : '选择病人',
		area : [ '800px', '600px' ],
		btn : [ '确定', '取消' ],
		yes : function(index, layero) {
			var dialog = window["layui-layer-iframe" + index];
			var status = dialog.layui.table.checkStatus("mainTable");
			var data = status.data;
			if (data.length == 0) {
				layer.msg("请选择一条记录!");
				return;
			}
			layer.close(index);
			LiveMeeting.load4S(resType, resId, data[0].patient_id, Utility
					.formatTS(data[0].indate), data[0].no_id);
		}
	});
}

/* 加载4S数据 */
LiveMeeting.load4S = function(resType, resId, patientId, indate, noId) {
	var me = this;
	$.get(
			"patient/data4s?restype=" + resType + "&resid=" + resId
					+ "&patientid=" + patientId + "&indate=" + indate
					+ "&noid=" + noId, function(result) {
				$("#content_4s").html(result);
				me.goTabView2("4s");
			});
}

LiveMeeting.onTreeNodeClick = function(event, data) {
	var me = this;
	if (data.device = me.data.device && data.channel == me.data.channel) {
		me.showVideo();
	}
}

LiveMeeting.checkTreeDoubleClick = function(event, data) {
	if (this.lastClickTime != null) {
		var ts = new Date().getTime() - this.lastClickTime;
		if (this.lastClickId == data.nodeId && ts < 300) {
			this.onTreeDoubleClick(event, data);
		}
	}
	this.lastClickTime = new Date().getTime();
	this.lastClickId = data.nodeId;
}

LiveMeeting.onTreeDoubleClick = function(event, data) {
	if (data.flag) {
		this.data.resType = data.flag;
		this.data.resId = data.id;
		if (data.flag == "LIVE") {
			this.toggleDevice(data, data.device, data.channel);
		} else {
			this.showPatientDialog(data.flag, data.id);
		}
	}
}

LiveMeeting.checkLive = function(sender) {
	var me = sender || this;
	$.get("meeting/live?id=" + me.data.id, function(result) {
		if (!result) {
			return;
		}
		var js = JSON.parse(result);
		if (js.code == 0) {
			if (js.data) {
				if (me.data.deviceStatus == 0 || me.data.deviceStatus == 3) {
					me.data.device = js.data.deviceId;
					me.data.channel = js.data.channel;
					me.data.deviceStatus = js.data.status;
					me.data.playUrl = js.data.playUrl;
					me.data.pushUrl = js.data.pushUrl;
					me.data.roomName = js.data.roomName;
					me.data.channelName = js.data.channelName;
					me.showVideo(js.data.playUrl);
					// $("#btnToggleLive p").text("停止直播");
				}
			} else {
				if (me.data.deviceStatus == 1 || me.data.deviceStatus == 2) {
					me.hideVideo();
					layer.msg("直播已结束!");
				}
			}
		}
	});
}

LiveMeeting.toggleDevice = function(node, device, channel) {
	switch (this.data.deviceStatus) {
	case 0:
	case 3:
		this.data.roomName = $('#treeResource').treeview('getNode',
				node.parentId).text;
		this.data.channelName = node.text;
		this.openDevice(device, channel);
		break;
	case 1:
	case 2:
		if (this.data.device == device && this.data.channel == channel) {
			this.closeDevice(device, channel);
		} else {
			layer.msg("已打开其它线路, 请先停止直播!");
		}
		break;
	}
}

/* 打开视频设备 */
LiveMeeting.openDevice = function(device, channel) {
	var me = this;
	me.data.deviceStatus = 1;
	layer.msg("正在开始直播, 请稍候 ...");
	$.ajax({
		url : "device/open",
		type : "post",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify({
			device : device,
			channel : parseInt(channel),
			user : me.data.user,
			meeting : me.data.id
		}),
		success : function(result) {
			if (!result)
				return;
			var js = JSON.parse(result);
			if (js.code == 0 && js.data) {
				me.data.device = js.data.deviceId;
				me.data.channel = js.data.channel;
				// me.data.deviceStatus = js.data.status;
				me.data.deviceStatus = 2;
				me.data.playUrl = js.data.playUrl;
				me.data.pushUrl = js.data.pushUrl;
				me.showVideo(js.data.playUrl);
				// $("#btnToggleLive p").text("停止直播");
			} else {
				me.data.deviceStatus = 0;
				layer.msg(js.msg);
			}
		},
		error : function(xrh, status, text) {
			me.data.deviceStatus = 0;
			layer.msg("出错啦: " + text);
		}
	});
}

/* 关闭视频设备 */
LiveMeeting.closeDevice = function(device, channel) {
	var me = this;
	layer.confirm("确定要停止直播吗?", function(index) {
		layer.close(index);
		layer.msg("正在停止直播, 请稍候 ...");
		me.data.deviceStatus = 3;
		$.ajax({
			url : "device/close",
			type : "post",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify({
				device : device || me.data.device,
				channel : parseInt(channel || me.data.channel),
				user : me.data.user,
				meeting : me.data.id
			}),
			success : function(result) {
				var js = JSON.parse(result);
				if (js.code == 0) {
					me.hideVideo();
					me.data.deviceStatus = 0;
					// $("#btnToggleLive p").text("开始直播");
				} else {
					layer.msg(js.msg);
				}
			},
			error : function(xrh, status, text) {
				layer.msg("出错啦: " + text);
			}
		});
	})
}

LiveMeeting.showVideo = function(url) {
	var me = this;
	if (!url)
		url = me.data.playUrl;
	this.goTabView2("live");
	this.player = videojs("liveVideo", {
		controls : true,
		autoplay : true,
		muted : true
	});
	this.player.src({
		type : 'application/x-mpegURL',
		src : url
	});
	this.player.ready(function() {
		me.player.play();
	});
	if (me.data.isPresenter) {
		$("#infoLiveRoom").text(me.data.roomName);
		$("#infoLiveChannel").text(me.data.channelName);
	}
};

LiveMeeting.hideVideo = function() {
	this.data.deviceStatus = 0;
	this.data.device = null;
	this.data.channel = 0;
	this.data.roomName = null;
	this.data.channelName = null;
	this.data.pushUrl = null;
	this.data.playUrl = null;
	if (this.player) {
		this.player.pause();
	}
	this.player = null;
	$("#content_live").hide();
};

LiveMeeting.changeVideo = function() {
	var player = videojs("liveVideo");
	player.src({
		type : 'application/x-mpegURL',
		src : $("#txtUrl").val()
	});
};

LiveMeeting.initShare = function() {
	var me = this;
	if (me.data.isPresenter) {
		WebRTCAPI.detectRTC(function(info) {
			if (!info.screenshare) {
				me.rtc.status == -2;
				layer.msg("当前浏览器不支持屏幕分享，请使用Chrome浏览器并安装屏幕分享插件！");
			} else {
				me.getUserToken(me.initRTC);
			}
		});
	} else {
		me.getUserToken(me.initRTC);
	}
}

LiveMeeting.initRTC = function(opt) {
	var me = LiveMeeting;

	// opt.appId = window.test_accont.sdkappid;
	// opt.userId = window.test_accont.users[3].userId;
	// opt.token = window.test_accont.users[3].userToken;
	// if (!me.data.isPresenter) {
	// opt.userId = window.test_accont.users[4].userId;
	// opt.token = window.test_accont.users[4].userToken;
	// }

	me.rtc.api = new WebRTCAPI({
		sdkAppId : opt.appId,
		userId : opt.userId,
		userSig : opt.token,
		closeLocalMedia : true
	});
	me.rtc.status = 0;
	me.rtc.api.on("onLocalStreamAdd", me.onLocalShare);
	me.rtc.api.on("onRemoteStreamUpdate", me.onRemoteShare);
	me.rtc.api.on("onRemoteStreamRemove", me.onRemoteUnShare);
	me.rtc.api.on("onWebSocketClose", me.onShareSocketClose);
	me.rtc.api.on("onRelayTimeout", me.onShareTimeout);
	me.rtc.api.on("onKickout", me.onShareKickout);

	// 进入房间
	me.rtc.api.enterRoom({
		roomid : parseInt(me.data.id),
	// role : me.data.isPresenter ? "anchor" : "audience"
	}, function() {
	}, function(error) {
		alert("进入房间失败:" + error.errorMsg);
	});
}

LiveMeeting.getUserToken = function(callback) {
	var me = this;
	var userId = "share" + me.data.user;
	$.ajax({
		url : "/tencent-video-api/project/genSignUpgrade.ajax",
		data : {
			userID : userId
		},
		dataType : "jsonp",
		success : function(result) {
			if (result.code == 0) {
				callback({
					userId : userId,
					appId : result.data.skdAppid,
					token : result.data.token
				});
			} else {
				alert(result.message || "获取Token失败");
			}
		}
	});
}

LiveMeeting.toggleShare = function() {
	var me = LiveMeeting;
	switch (me.rtc.status) {
	case -2:
		layer.msg("当前浏览器不支持屏幕分享，请使用 Chrome浏览器并安装屏幕分享插件！");
		break;
	case -1:
		layer.msg("正在初始化，请稍候 ...");
		break;
	case 0:
		me.startShare();
		break;
	case 1:
		me.stopShare();
		break;
	}
}

LiveMeeting.startShare = function() {
	var me = this;
	// 采集流
	me.rtc.api.getLocalStream({
		screen : true,
		video : false,
		audio : false,
		screenSources : "screen",
		attributes : {
			frameRate : 10
		}
	}, function(info) {
		// me.showShareVideo(info.stream);
		$("#btnShare").find("p").text("停止共享");
		me.rtc.status = 1;

		me.rtc.api.startRTC({
			role : "screen_share_role",
			stream : info.stream
		}, function() {
			layer.msg("推流成功");
		}, function(error) {
			alert("推流失败:" + error.errorMsg);
		});
	}, function(error) {
		if (error.errorCode = 10008) {
			if (error.errorMsg.indexOf("get user media failed") != -1) {
				layer.msg("您已取消屏幕共享，请刷新页面重试!");
			}
		} else {
			layer.msg(error.errorMsg);
		}
	});
}

LiveMeeting.stopShare = function() {
	var me = this;
	me.rtc.api.stopRTC({}, function() {
		layer.msg("已停止屏幕共享!");
	}, function(error) {
		layer.msg(error);
	});
	me.hideShareVideo();
	$("#btnShare").find("p").text("屏幕共享");
	me.rtc.status = 0;
}

LiveMeeting.onLocalShare = function(data) {
}

LiveMeeting.onRemoteShare = function(data) {
	var me = LiveMeeting;
	if (!me.isShareVideo(data.userId))
		return;
	if (data.stream != null) {
		me.showShareVideo(data);
	}
}

LiveMeeting.onRemoteUnShare = function(data) {
	var me = LiveMeeting;
	if (!me.isShareVideo(data.userId))
		return;
	me.hideShareVideo();
}

LiveMeeting.onShareSocketClose = function(data) {
	console.log("视频连接已断开");
}

LiveMeeting.onShareTimeout = function(data) {
	console.log("视频服务器连接超时");
}

LiveMeeting.onShareKickout = function() {
	console.log("重复登录, 被踢出!");
}

LiveMeeting.isShareVideo = function(uid) {
	if (uid.indexOf("share") == 0) {
		return true;
	}
	return false;
}

LiveMeeting.showShareVideo = function(data) {
	$("#shareVideo").attr("data-uid", data.userId).attr("data-vid",
			data.videoId);
	$("#shareVideo")[0].srcObject = data.stream;
	this.rtc.sharing = true;
	$("#content_share").show();
}

LiveMeeting.hideShareVideo = function() {
	$("#shareVideo")[0].srcObject = null;
	this.rtc.sharing = false;
	$("#content_share").hide();
}

LiveMeeting.onSocketMessage = function(msg) {
	var me = LiveMeeting;
	switch (msg.type) {
	case "view.notify":
		me.onViewNotify(msg);
		return true;
	case "view.ask":
		me.onViewAsk(msg);
		return true;
	case "view.tell":
		me.onViewTell(msg);
		return true;
	}
	return false;
}

LiveMeeting.viewNotify = function() {
	var me = LiveMeeting;
	if (!me.data.isPresenter)
		return;
	var content = null;
	var page = $("#tabView").attr("data-tab");
	if (page == "mix") {
		var tab2 = $("#content_mix").attr("data-tab2")
		page = page + "," + tab2;
		switch (tab2) {
		case "4s":
			content = $("#content_4s").html();
			break;
		}
	}
	$.ajax({
		url : "viewNotify",
		type : "post",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify({
			meeting : me.data.id,
			page : page,
			content : content
		}),
		success : function(result) {
			if (result.code != 0) {
				layer.msg(result.msg);
			}
		},
		error : function(xrh, status, text) {
			layer.msg("通知画布切换出错: " + text);
		}
	});
}

LiveMeeting.viewAsk = function() {
	var me = LiveMeeting;
	$.ajax({
		url : "viewAsk",
		type : "post",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify({
			meeting : me.data.id,
		}),
		success : function(result) {
			if (result.code != 0) {
				layer.msg(result.msg);
			}
		},
		error : function(xrh, status, text) {
			layer.msg("获取画布状态出错: " + text);
		}
	});
}

LiveMeeting.onViewAsk = function(msg) {
	var me = LiveMeeting;
	if (!me.data.isPresenter)
		return;
	var content = null;
	var page = $("#tabView").attr("data-tab");
	if (page == "mix") {
		var tab2 = $("#content_mix").attr("data-tab2")
		page = page + "," + tab2;
		switch (tab2) {
		case "4s":
			content = $("#content_4s").html();
			break;
		}
	}
	$.ajax({
		url : "viewTell",
		type : "post",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify({
			meeting : me.data.id,
			page : page,
			content : content,
			toUser : msg.fromUser
		}),
		success : function(result) {
			if (result.code != 0) {
				layer.msg(result.msg);
			}
		},
		error : function(xrh, status, text) {
			layer.msg("答复画布状态出错: " + text);
		}
	});
}

LiveMeeting.onViewNotify = function(msg) {
	this.onViewTell(msg);
}

LiveMeeting.onViewTell = function(msg) {
	var me = LiveMeeting;
	if (me.data.isPresenter)
		return;
	if (me.rtc.sharing)
		return;
	var pages = msg.page;
	var aryPage = pages.split(",");
	if (aryPage.length == 2) {
		switch (aryPage[1]) {
		case "4s":
			$("#content_4s").html(msg.content);
			break;
		}
		me.goTabView2(aryPage[1]);
	} else {
		me.goTabView(aryPage[0]);
	}
}

$(function() {
	LiveMeeting.init();
});