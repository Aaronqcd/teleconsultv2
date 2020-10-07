var LiveMeeting = LiveMeeting || {
	lastClickTime : null,
	lastClickId : null,
	monitor : null,
	liveMonitor: null,
	player : null,
	recordStatus: null, // 录制状态
	recordTaskId: null, // 录制任务ID
	rtc : {
		api : null,
		status : -1, // -2=不支持 -1=待检测 0=已停止 1=已开始
		sharing : false,
		localStream: null
	},
	data : {
		id : parseInt(Utility.getUrlParam("meetingId")),
		user : parseInt(Utility.getUrlParam("userid")),
		isPresenter : $("#hdPresenter").val() == "true",
		rtcRoomId: null,
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
	$("#btnStartRecord").click(LiveMeeting.startRecord);
	$("#btnStopRecord").click(LiveMeeting.stopRecord);
	$("#btnPauseRecord").click(LiveMeeting.pauseRecord);
	$("#btnResumeRecord").click(LiveMeeting.resumeRecord);
	this.uiRecordStatus();
	this.statusRecord();
	TXWebRTCAPI.on("onAddStream", LiveMeeting.onLiveVideoAdd);
	TXWebRTCAPI.on("onRemoveStream", LiveMeeting.onLiveVideoRemove);
	// TXWebRTCAPI.on("onEvent", LiveMeeting.onLiveVideoEvent);
};

/*
 * 画布切换 ----------------------------
 */
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

/*
 * 4S 资源 ----------------------------
 */
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

/*
 * 设备直播 ----------------------------
 */
/* 检测是否有设备直播 */
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
		if(!device){
			layer.msg("设备ID无效，请刷新页面重试!");
			return;
		}
		this.data.roomName = $('#treeResource').treeview('getNode',
				node.parentId).text;
		this.data.channelName = node.text;
		this.openDevice(parseInt(node.id), device, channel);
		break;
	case 1:
	case 2:
		if (this.data.device == device && this.data.channel == channel) {
			this.closeDevice(parseInt(node.id), device, channel);
		} else {
			layer.msg("已打开其它线路, 请先停止直播!");
		}
		break;
	}
}

/* 打开视频设备 */
LiveMeeting.openDevice = function(id, device, channel) {
	var me = this;
	me.data.deviceStatus = 1;
	layer.msg("正在开始直播, 请稍候 ...");
	$.ajax({
		// url : "device/open",
		url : "device/open2",
		type : "post",
		contentType : "application/json; charset=utf-8",
		data : JSON.stringify({
			id: id,
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
			// url : "device/close",
			url : "device/close2",
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
					me.toggleLiveMonitor(false);
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
	/* -------- VideoJS 升级为 WebRTC -----------------
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
	------------------------------------------------- */
	// url = "webrtc://liveplay.szvisionapp.cn/live/OBSTest";
	TXWebRTCAPI.pullStream(url, "liveVideo");
	
	if (me.data.isPresenter) {
		$("#infoLiveRoom").text(me.data.roomName);
		$("#infoLiveChannel").text(me.data.channelName);
	}
};

LiveMeeting.hideVideo = function() {
	if (this.data.playUrl) {
		TXWebRTCAPI.stopStream(this.data.playUrl);
	}
	this.data.deviceStatus = 0;
	this.data.device = null;
	this.data.channel = 0;
	this.data.roomName = null;
	this.data.channelName = null;
	this.data.pushUrl = null;
	this.data.playUrl = null;
	/* -------------------------- 
	if (this.player) {
		this.player.pause();
	}
	this.player = null;
	----------------------------- */
	
	$("#content_live").hide();
};

LiveMeeting.changeVideo = function() {
	var player = videojs("liveVideo");
	player.src({
		type : 'application/x-mpegURL',
		src : $("#txtUrl").val()
	});
};

LiveMeeting.onLiveVideoAdd = function(e) {
	console.log("LiveMeeting.onLiveVideoAdd", e);
	var video = document.getElementById("liveVideo");
	setTimeout(function() {
		video.srcObject = e.stream;
		//video.muted = true
		video.autoplay = true;
		video.playsinline = true;
		video.play();
	}, 100);
	LiveMeeting.toggleLiveMonitor(false);
};

LiveMeeting.onLiveVideoRemove = function(e) {
	console.log("LiveMeeting.onLiveVideoRemove", e);
	if(e.code != 20013){
		LiveMeeting.toggleLiveMonitor(true);
	}
};

LiveMeeting.onLiveVideoEvent = function(e) {
};

LiveMeeting.toggleLiveMonitor = function (flag) {
	var me = this;
	if (flag) {
		if(!this.liveMonitor){
			this.liveMonitor = setInterval(function(){
				TXWebRTCAPI.pullStream(me.data.playUrl, "liveVideo");
			}, 3000);			
		}
	} else {
		if(this.liveMonitor){
			clearInterval(this.liveMonitor);
			this.liveMonitor=null;
		}
	}
};

LiveMeeting.refreshLiveVideo = function () {
	var me = LiveMeeting;
	if(me.data.playUrl) {
		TXWebRTCAPI.stopStream(me.data.playUrl);
		setTimeout(function(){
			TXWebRTCAPI.pullStream(me.data.playUrl, "liveVideo");			
		}, 300);
	}
}
/*
 * 屏幕共享 ----------------------------
 */
/* 初始化屏幕分享 */
LiveMeeting.initShare = function() {
	var me = this;
	me.data.rtcRoomId = parseInt(meetingConfig.imGroupID);
	if (me.data.isPresenter) {
		if (TRTC.isScreenShareSupported()) {
			me.getUserToken(me.initRTC);
		} else {
			me.rtc.status == -2;
			layer.msg("当前浏览器不支持屏幕分享，请使用Chrome浏览器, 且版本为72以上！");
		}
	} else {
		me.getUserToken(me.initRTC);
	}
}

LiveMeeting.initRTC = function(opt) {
	var me = LiveMeeting;
	me.rtc.api = TRTC.createClient({
		sdkAppId : opt.appId,
		userId : opt.userId,
		userSig : opt.token,
		mode : "videoCall"
	});
	me.rtc.status = 0;
	me.rtc.api.on("stream-added", me.onRemoteShare);
	me.rtc.api.on("stream-removed", me.onRemoteUnShare);
	me.rtc.api.on("stream-subscribed", me.onRemoteShareSubscribe);
	me.rtc.api.on("error", me.onShareError);

	if (!me.data.isPresenter) {
		// 进入房间
        me.rtc.api.join({roomId: parseInt(me.data.rtcRoomId)}).then(() => {
            console.log("与会人加入房间成功!");
        }).catch(error=> {
            console.error("与会人加入房间失败: " + error);
        });
	}
}

LiveMeeting.getUserToken = function(callback) {
	var me = this;
	var userId = "SHARE_SCREEN_" + me.data.user;
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
		layer.msg("当前浏览器不支持屏幕分享，请使用Chrome浏览器, 且版本为72以上！");
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
	if (!me.data.isPresenter) {
		console.log("非主持人不允许分享屏幕!");
		layer.msg("非主持人不允许分享屏幕!");
		return;
	}
	if(!me.data.rtcRoomId) {
		return;
	}

    // 不接收任何远端流, 只负责发送屏幕分享流
    me.rtc.api.setDefaultMuteRemoteStreams(true);
    me.rtc.localStream = TRTC.createStream({
        audio: false, video: false, screen: true
    });
    // me.rtc.localStream.setScreenProfile("1080p");
    // me.rtc.localStream.setScreenProfile({width:1920, height:1080,
	// frameRate:5, bitrate:1600 });
    me.rtc.localStream.on("screen-sharing-stopped", me.onShareStop);
    me.rtc.localStream.initialize().then(() => {
        console.log("屏幕共享初始化成功!");
        me.rtc.api.join({roomId: me.data.rtcRoomId}).then(() => {
            console.log("主持人加入房间成功!");
            me.rtc.api.publish(me.rtc.localStream).then(() => {
                console.log("屏幕共享成功!");
                // me.showShareVideo(me.rtc.localStream);
                // console.log("播放本地流成功!")
        		$("#btnShare").find("p").text("停止共享");
        		me.rtc.status = 1;
    			layer.msg("屏幕共享成功");
            }).catch(error=> {
                console.error("屏幕共享失败: " + error);
                alert("屏幕共享失败: " + error.getCode);
                me.rtc.api.leave();
            });
        }).catch(error=> {
            console.error("主持人加入房间失败: " + error);
    		layer.msg("主持人加入房间失败");
        });
    }).catch(error=> {
    	console.error("屏幕共享初始化失败: " + error);
        if(error.name == "NotAllowedError"){
        	layer.msg("已取消屏幕共享");
        }else{
        	layer.msg("屏幕共享初始化失败:" + error.message );
        }
    });
}

LiveMeeting.stopShare = function() {
	var me = this;
	if(me.rtc.status == 0) return;
	
	me.rtc.status = 0;
	$("#btnShare").find("p").text("屏幕共享");
	
    // 取消发布本地流
    me.rtc.api.unpublish(me.rtc.localStream).then(()=>{
    	console.log("取消发布本地流成功!");
    }).catch(error=>{
    	console.error("取消发布本地流失败: " + error);
    });
    
    // 关闭本地流
    if (me.rtc.localStream) {
        // me.rtc.localStream.stop();
        me.rtc.localStream.close();
        me.rtc.localStream = null;
    }
    
    // 退出房间
    me.rtc.api.leave().then(() => {
        console.log("主持人退出房间成功!");
    }).catch(error=> {
        console.error("主持人退出房间失败: " + error);
    });
    
	layer.msg("已停止屏幕共享!");
}

LiveMeeting.onRemoteShare = function(event) {
	var me = LiveMeeting;
    console.log("新增远程屏幕流: " + event.stream);
	if (me.isShareVideo(event.stream.getUserId()) && !me.isMyShare(event.stream.getUserId())){
	    me.rtc.api.subscribe(event.stream,{audio:false,video:true}).then(()=>{
	    	console.log("订阅远程屏幕成功!");
	    }).catch(error=>{
	        console.error("订阅远程屏幕失败: " + error);
	    });
	} else {
		me.rtc.api.unsubscribe(event.stream);
	}
}

LiveMeeting.onRemoteUnShare = function(event) {
    console.log("移除远程屏幕流: " + event.stream);
	var me = LiveMeeting;
	if (me.isShareVideo(event.stream.getUserId())){
		me.hideShareVideo(event.stream);
	}
}

LiveMeeting.onRemoteShareSubscribe = function (event) {
    console.log("订阅远程屏幕流: " + event.stream);
	var me = LiveMeeting;
    me.showShareVideo(event.stream);
},

LiveMeeting.onShareError = function (error) {
    console.error("屏幕共享错误: " + error);
},

LiveMeeting.onShareStop = function () {
    console.log("已停止屏幕共享!");
	LiveMeeting.stopShare();
}

LiveMeeting.isShareVideo = function(uid) {
	if (uid.indexOf("SHARE_SCREEN_") == 0) {
		return true;
	}
	return false;
}

LiveMeeting.isMyShare = function(uid) {
	return uid == "SHARE_SCREEN_" + LiveMeeting.data.user;
}

LiveMeeting.showShareVideo = function(stream) {
    stream.play("content_share", {objectFit: "contain"});
	this.rtc.sharing = true;
	$("#content_share").show();
}

LiveMeeting.hideShareVideo = function(stream) {
	stream.stop();
	this.rtc.sharing = false;
	$("#content_share").hide();
}

/*
 * Socket 通信 ----------------------------
 */
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

/*
 * 视频录制 ----------------------------
 */
LiveMeeting.postJson = function(url, data, fnOK, fnFail){
	$.ajax({
		url : url,
		type : "post",
		contentType : "application/json; charset=utf-8",
		data : data?JSON.stringify(data):null,
		success : fnOK,
		error : fnFail
	});
}

LiveMeeting.uiRecordStatus = function(){
	$("#btnStartRecord").hide();
	$("#btnPauseRecord").hide();
	$("#btnResumeRecord").hide();
	$("#btnStopRecord").hide();
	switch(this.recordStatus){
	case "STARTING":
	case "PREPARED":
	case "RECORDING":
	case "RESUMING":
		$("#btnPauseRecord").show();
		$("#btnStopRecord").show();
		break;
	case "PAUSING":
	case "PAUSED":
		$("#btnResumeRecord").show();
		$("#btnStopRecord").show();
		break;
	case "STOPPING":
	case "STOPPED":
	case "FINISHED":
		$("#btnStartRecord").show();
		break;
	default:
		$("#btnStartRecord").show();
		break;
	}
}

LiveMeeting.statusRecord = function(){
	var me = LiveMeeting;
	me.postJson("/tencent-video-api/record/statusLocal", me.data.id, 
	function(result){
		if(result.code==0){				
			me.recordStatus = result.data.status;
			me.recordTaskId = result.data.taskId;
			me.uiRecordStatus();
		}else{
			console.log("获取录制状态出错: ", result);
			layer.msg("获取录制状态出错: " + result.message);
		}
	}, 
	function(xrh, status, text) {
		layer.msg("获取录制状态出错: " + text);
	});
}

LiveMeeting.startRecord = function(){
	var me = LiveMeeting;
	me.postJson("/tencent-video-api/record/start", me.data.id, 
	function(result){
		if(result.code==0){
			me.recordStatus = "STARTING";
			me.recordTaskId = result.data.taskId;
			me.uiRecordStatus();
		}else{
			console.log("开始录制出错: ", result);
			layer.msg(result.message);
		}
	}, 
	function(xrh, status, text) {
		layer.msg(text);
	});	
}

LiveMeeting.pauseRecord = function(){
	var me = LiveMeeting;
	me.postJson("/tencent-video-api/record/pause", me.recordTaskId, 
	function(result){
		if(result.code==0){
			me.recordStatus = "PAUSING";
			me.uiRecordStatus();
		}else{
			console.log("暂停录制出错: ", result);
			layer.msg(result.message);
		}
	}, 
	function(xrh, status, text) {
		layer.msg(text);
	});
}

LiveMeeting.resumeRecord = function(){
	var me = LiveMeeting;
	me.postJson("/tencent-video-api/record/resume", me.recordTaskId, 
	function(result){
		if(result.code==0){
			me.recordStatus = "RESUMING";
			me.uiRecordStatus();
		}else{
			console.log("继续录制出错: ", result);
			layer.msg(result.message);
		}
	}, 
	function(xrh, status, text) {
		layer.msg(text);
	});
}

LiveMeeting.stopRecord = function(){
	var me = LiveMeeting;
	me.postJson("/tencent-video-api/record/stop", me.recordTaskId, 
	function(result){
		if(result.code==0){
			me.recordStatus = "STOPPING";
			me.recordTaskId = null;
			me.uiRecordStatus();
		}else{
			console.log("停止录制出错: ", result);
			layer.msg(result.message);
		}
	}, 
	function(xrh, status, text) {
		layer.msg(text);
	});	
}

$(function() {
	LiveMeeting.init();
});