/**
 * 测试屏幕分享
 */

var LiveMeeting = LiveMeeting || {
    roomId: 123456,
    masterId:"share101",
    userId: "share102",

    isSharing: false,
    masterClient: null,
    masterStream: null,
    userClient: null,
    init: function () {
        const me = this;
        me.getUserToken(me.masterId, function (config) {
            me.masterClient = TRTC.createClient({
                sdkAppId: config.appId,
                userId: config.userId,
                userSig: config.token,
                mode: "videoCall"
            });
            me.masterClient.on("error", me.onShareError);
        });
        me.getUserToken(me.userId, function (config) {
            me.userClient = TRTC.createClient({
                sdkAppId: config.appId,
                userId: config.userId,
                userSig: config.token,
                mode: "videoCall"
            });
            me.userClient.on("stream-added", me.onRemoteShareAdd);
            me.userClient.on("stream-removed", me.onRemoteShareRemove);
            me.userClient.on("stream-subscribed", me.onRemoteShareSubscribe);
            me.userClient.on("error", me.onShareError);
        });
    },
    getUserToken: function (userId, callback) {
        $.ajax({
            url: "/tencent-video-api/project/genSignUpgrade.ajax",
            data: {
                userID: userId
            },
            dataType: "jsonp",
            success: function (result) {
                if (result.code == 0) {
                    callback({
                        userId: userId,
                        appId: result.data.skdAppid,
                        token: result.data.token
                    });
                } else {
                    alert(result.message || "获取Token失败");
                }
            }
        });
    },
    startShare: function () {
        const me = this;
        if(!me.masterClient) return;
        if (me.isSharing) return;
        me.isSharing = true;

        me.masterClient.join({roomId: me.roomId}).then(() => {
            console.log("主播加入房间成功!");

            // 默认不接收任何远端流, 只负责发送屏幕分享流
            // me.masterClient.setDefaultMuteRemoteStreams(true);
            me.masterStream = TRTC.createStream({
                audio: false, video: false, screen: true
            });
            me.masterStream.setScreenProfile("1080p");
            // me.masterStream.setScreenProfile({ width: 1920, height: 1080, frameRate: 5, bitrate: 1600 });
            me.masterStream.on("screen-sharing-stopped", me.onShareStop);
            me.masterStream.initialize().then(() => {
                console.log("初始化本地流成功!");
                me.masterClient.publish(me.masterStream).then(() => {
                    console.log("发布本地流成功!");
                    me.masterStream.play("mainVideo",{objectFit: "contain"}).then(() => {
                        console.log("播放本地流成功!");
                    }).catch(error=> {
                        console.error("播放本地流失败!");
                    });
                }).catch(error=> {
                    console.error("发布本地流失败: " + error);
                });
            }).catch(error=> {
                console.error("初始化本地流失败: " + error);
            });

        }).catch(error=> {
            console.error("主播加入房间失败: " + error);
        });
        
        me.userClient.join({roomId: me.roomId}).then(() => {
            console.log("观众加入房间成功!");
        }).catch(error=> {
            console.error("观众加入房间失败: " + error);
        });
    },
    stopShare: function () {
    	const me = this;
        if (!me.isSharing) return;
        me.isSharing = false;

        // 取消订阅远端流        
        
        // 取消发布本地流
        me.masterClient.unpublish(me.masterStream).then(()=>{
        	console.log("取消发布本地流成功!");
        }).catch(error=>{
        	console.error("取消发布本地流失败: " + error);
        });
        
        // 关闭本地流
        if (me.masterStream) {
            // me.masterStream.stop();
            me.masterStream.close();
            me.masterStream = null;
        }
        
        // 退出房间
        me.masterClient.leave().then(() => {
            console.log("主播退出房间成功!");
        }).catch(error=> {
            console.error("主播退出房间失败: " + error);
        });
        me.userClient.leave().then(() => {
            console.log("观众退出房间成功!");
        }).catch(error=> {
            console.error("观众退出房间失败: " + error);
        });
    },
    onRemoteShareAdd: function (event) {
        const stream = event.stream;
        console.log("新增远程流: " + event.stream);
        LiveMeeting.userClient.subscribe(event.stream,{audio:false,video:true}).then(()=>{
        	console.log("订阅远程流成功!");
        }).catch(error=>{
            console.error("订阅远程流失败: " + error);
        });
    },
    onRemoteShareRemove: function (event) {
        const stream = event.stream;
        console.log("移除远程流: " + event.stream);
        event.stream.stop();
    },
    onRemoteShareSubscribe: function (event) {
        const stream = event.stream;
        console.log("订阅远程流: " + event.stream);
        event.stream.play("userVideo",{objectFit: "contain"});
    },
    onShareError: function (error) {
        console.error("屏幕分享错误: " + error);
    },
    onShareStop: function () {
        console.log("已停止屏幕分享!");
    }
};

$(function () {
    LiveMeeting.init();
    $("#btnInfo").click(debug);
    $("#btnShare").click(function () {
        if (LiveMeeting.isSharing) {
            LiveMeeting.stopShare();
            $("#btnShare").text("开始屏幕分享").removeClass().addClass("btn-green");
        } else {
            LiveMeeting.startShare();
            $("#btnShare").text("停止屏幕分享").removeClass().addClass("btn-red");
        }
    });
});

function debug() {
    info("TRTC 版本: " + TRTC.VERSION);
    TRTC.checkSystemRequirements().then((result) => {
        info("浏览器兼容 TRTC: " + (result ? "是" : "否"), true);
    });
    info("支持屏幕分享: " + (TRTC.isScreenShareSupported() ? "是" : "否"), true);
    TRTC.getDevices().then((result) => {
        info("设备信息-------------------", true);
        if (result) {
            for (var k1 in result) {
                for (var k2 in result[k1]) {
                    info(k2 + " : " + result[k1][k2], true);
                }
                info("", true);
            }
        }
    });
}

function info(text, append) {
    if (append) {
        $("#info").html($("#info").html() + "<br/>" + text);

    } else {
        $("#info").text(text);
    }
}