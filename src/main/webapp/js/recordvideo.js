/**
 * 会诊录制视频管理
 */
var RecordManager = {meetingId: 0};

RecordManager.initDataGrid = function() {
	layui.table.render({
		elem : '#layTable',
		url : 'queryRecordVideo',
		toolbar : '#toolbar',
		defaultToolbar : [],
		height : 'full-40',
		cols : [ [ {
			type : 'checkbox',
			width:35
		}, {
			field : 'meetingNo',
			title : '会诊编号',
			width : 135,
			sort : true
		}, {
			field : 'name',
			title : '视频名称',
			sort : true
		}, {
			field : 'videoSize',
			title : '大小',
			width : 80,
			templet : function(d) {
				return RecordManager.formatSize(d.videoSize);
			},
			sort : true
		}, {
			field : 'videoDuration',
			title : '时长',
			width : 80,
			templet : function(d) {
				return RecordManager.formatTime1(d.videoDuration);
			},
			sort : true
		}, {
			field : 'videoType',
			title : '类型',
			width : 80,
			templet : function(d) {
				return RecordManager.formatVideoType(d.videoType);
			},
			sort : true
		}, {
			field : 'inDate',
			title : '录制日期',
			width : 180,
			templet : function(d) {
				return RecordManager.formatTime2(d.inDate);
			},
			sort : true
		}, {
			title : '动作',
			templet : '#toolline',
			width : 200,
		} ] ],
		page : true
	});
}

RecordManager.onGridEvent = function(obj) {
	var checkStatus = layui.table.checkStatus(obj.config.id);
	if (checkStatus.data.length == 0) {
		layui.layer.msg("请先勾选数据记录!");
		return;
	}
	;
	var ids = "";
	for (var i = 0; i < checkStatus.data.length; i++) {
		if (ids.length > 0) {
			ids += ",";
		}
		ids += checkStatus.data[i].id;
	}
	switch (obj.event) {
	case 'delete':
		RecordManager.deleteVideos(ids);
		break;
	}
}

RecordManager.onGridRowEvent = function(obj) {
	var data = obj.data;
	switch (obj.event) {
	case "name":
		RecordManager.renameVideo(data.id, data.name);
		break;
	case "play":
		RecordManager.playVideo(data.videoUrl);
		break;
	case "download":
		RecordManager.downloadVideo(data.videoUrl);
		break;
	case "delete":
		RecordManager.deleteVideos(data.id);
		break;
	}
}

RecordManager.onSortEvent = function(args) {
	layui.table.reload("layTable", {
		initSort : args,
		where : {
			sort : args.field,
			direction : args.type == "desc" ? 1 : 0
		},
	});
}

RecordManager.initTree = function(){
	var me = this;
	$.get("queryRecordMeetings", function(result){
		$("#meetingTree").treeview({
			color : "#666",
			selectedBackColor : "#d4f9fb",
			showBorder : false,
			selectedColor : "#666",
			showTags : false,
			data : result.data,
			onNodeSelected : function(event, data) {
				me.meetingId = data.id;
				me.reloadGrid();
			}
		});
	});	
};

RecordManager.postJson = function(url, data, fnOK, fnFail) {
	$.ajax({
		url : url,
		type : "post",
		contentType : "application/json; charset=utf-8",
		data : data ? JSON.stringify(data) : null,
		success : fnOK,
		error : fnFail
	});
}

RecordManager.renameVideo = function(id, oldName) {
	layui.layer.prompt({
		value : oldName,
		title : "重命名",
		maxlength : 50
	}, function(value, index, elem) {
		layer.close(index);
		RecordManager.postJson("RenameRecordVideo", {
			id : id,
			name : value
		}, function(result) {
			result = JSON.parse(result);
			if (result.code == "0") {
				RecordManager.reloadGrid();
			} else {
				layui.layer.msg(result.msg);
			}
		});
	});
}

RecordManager.playVideo = function(url) {
	window.open(url);
}

RecordManager.downloadVideo = function(url) {
	window.open(url);
}

RecordManager.deleteVideos = function(ids) {
	layui.layer.confirm("确认要删除该视频吗?", function(index) {
		layui.layer.close(index);
		RecordManager.postJson("DeleteRecordVideo", ids, function(result) {
			result = JSON.parse(result);
			if (result.code == "0") {
				RecordManager.reloadGrid();
			} else {
				layui.layer.msg(result.msg);
			}
		});
	});
}

RecordManager.reloadGrid = function() {
	var me = this;
	layui.table.reload('layTable', {
		page : {
			curr : 1
		},
		where : {
			meetingId: me.meetingId,
			searchValue : $("#searchValue").val()
		}
	});
}

RecordManager.formatSize = function(size) {
	if (size < 1024) { // <1K
		return size + "B";
	}
	if (size < 1048576) { // <1M
		return (size / 1024).toFixed(1) + "K";
	}
	if (size < 1073741824) {// <1G
		return (size / 1048576).toFixed(1) + "M";
	}
	return (size / 1073741824).toFixed(1) + "G";
}

RecordManager.formatTime1 = function(value) {
	value = parseInt(value / 1000);
	if (value < 60) {
		return "00:00:" + value;
	} else if (value < 3600) {
		return "00:" + parseInt(value / 60) + ":" + (value % 60);
	} else {
		var min = parseInt((value % 3600) / 60);
		return parseInt(value / 3600) + ":" + min + ":" + (value % 3600);
	}
}

RecordManager.formatTime2 = function(value) {
	var date = new Date(value);
	var fmt = "YYYY-mm-dd HH:MM:SS";
	let ret;
	const opt = {
		"Y+" : date.getFullYear().toString(), // 年
		"m+" : (date.getMonth() + 1).toString(), // 月
		"d+" : date.getDate().toString(), // 日
		"H+" : date.getHours().toString(), // 时
		"M+" : date.getMinutes().toString(), // 分
		"S+" : date.getSeconds().toString()
	// 秒
	};
	for ( let k in opt) {
		ret = new RegExp("(" + k + ")").exec(fmt);
		if (ret) {
			fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k]
					.padStart(ret[1].length, "0")))
		}
		;
	}
	;
	return fmt;
}

RecordManager.formatVideoType = function(value) {
	switch (value) {
	case 0:
		return "专家视频";
	case 1:
		return "屏幕分享视频";
	case 2:
		return "白板视频";
	case 3:
		return "混流视频";
	case 4:
		return "纯音频";
	}
	return value;
}

$(function() {
	layui.use([ 'table', 'form', 'layer'], function() {
		RecordManager.initDataGrid();
		RecordManager.initTree();
		$('#searchBtn').on('click', RecordManager.reloadGrid);
		layui.table.on('toolbar(layTable)', RecordManager.onGridEvent);
		layui.table.on('tool(layTable)', RecordManager.onGridRowEvent);
		layui.table.on('sort(layTable)', RecordManager.onSortEvent);
	});
});