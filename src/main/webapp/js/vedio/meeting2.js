;
!function () {
    // "use strict"
    //回调函数 的调用方法  invokeFunc("funcName"[, arg1, arg2]) 或   invokeFunc(func[, arg1, arg2])
    function invokeFunc(func) {
        var argsArr = []
        for (var i in arguments) {
            if (i > 0) {
                argsArr.push(arguments[i])
            }
        }
        switch (typeof func) {
            case "function" :
                func.apply(window, argsArr);
                break;
            case "string"    :
                window[func].apply(window, argsArr); 
                break;
        }
    }

    window.invokeFunc = invokeFunc;
}();

!function () {

    //获取url参数的值
    function getQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    }

    window.getQueryString = getQueryString;
}();

!function () {

    function ajax(url, data, success, fail) {
        $.ajax({
            url: url,
            xhrFields: {
                withCredentials: false
            },
            data: data,
            success: function (resp) {
                if (resp.code === 0) {
                    success(resp)
                } else {
                    alert(resp.message ? resp.message : "服务器异常")
                }
            },
            error: fail
        })
    }

    function jsonp(url, data, success, fail) {
        $.ajax({
            url: url,
            xhrFields: {
                withCredentials: false
            },
            data: data,
            dataType: "jsonp",
            success: function (resp) {
                if (resp.code === 0) {
                    success(resp)
                } else {
                    alert(resp.message ? resp.message : "服务器异常")
                }
            },
            error: fail
        })
    }

    window.ajax = ajax;
    window.jsonp = jsonp;
}();

(function () {

    var callbackMap = {}
    var meetingConfig = {};
    var loginInfo = {};
    var selType = webim.SESSION_TYPE.GROUP;

    meetingConfig.defaultUserAvatar = "images/meeting/tree_cloud_icon.png"
    // meetingConfig['vedioApiHost'] = document.domain.match(/(localhost)|(tencent.vedio.yf)/) ? "https://i.27wy.cn" : "/tencent-video-api"
    meetingConfig['vedioApiHost'] = "/tencent-video-api";
    //meetingConfig['vedioApiHost'] = document.domain.match(/(localhost)|(tencent.vedio.yf)/) ? "https://telemed.szvisionapp.cn/tencent-video-api" : "/tencent-video-api"
    // meetingConfig['vedioApiHost'] = document.domain.match(/(localhost)|(tencent.vedio.yf)/) ? "http://192.168.0.122:8090/tencent-video-api" : "/tencent-video-api"
    meetingConfig['mainApiHost'] = ""

    //从主框架获取会诊信息、登录用户信息、参会列表
    function getMeetingInfo(callback) {
        var userid = getQueryString("userid")
        var url = meetingConfig.mainApiHost + "currentMeetingByUserID"

        var meetingID = $("#currentMeetingId").length == 1 ? $("#currentMeetingId").val() : 1;
        meetingConfig.meetingID = meetingID

        $.post(url, {meetingId: meetingID, userid: userid}, function (resp) {

            var userID = resp['currentUser']['user'];
            var userName = resp['currentUser']['name'];
            var userAvatar = resp['currentUser']['avatar'] ? "webapp/images/headimg/" + resp['currentUser']['avatar'] : meetingConfig.defaultUserAvatar;
            var videoRole = resp['currentUser']['videoRole']
            var meetingMemberIDs = resp['attendsLoginIds'].split(",")
            var meetingMemberNames = resp['attendNames'].split(",")
            var meetingMemberAvatars = resp['attendAvatars'].split(",")
            var isteudborad = $('#isteudborad').val() == 1 ? true : false;

            var meetingMembers = []
            var userInfoMap = {}

            var userIsInMembers = false;
            if (resp['isPresenter']) {
                userIsInMembers = true;
            }

            meetingMembers.push({
                userID: resp['loginId'],
                userName: resp['userName'],
                headImg: resp['userAvatar'] ? "webapp/images/headimg/" + resp['userAvatar'] : meetingConfig.defaultUserAvatar
            })      //主持人信息
            userInfoMap[resp['loginId']] = {
                userID: resp['loginId'],
                userName: resp['userName'],
                headImg: resp['userAvatar'] ? "webapp/images/headimg/" + resp['userAvatar'] : meetingConfig.defaultUserAvatar
            }
            meetingMemberIDs.forEach(function (userID, i) {
                meetingMembers.push({
                    userID: userID,
                    userName: meetingMemberNames[i],
                    headImg: meetingMemberAvatars[i] ? "webapp/images/headimg/" + meetingMemberAvatars[i] : meetingConfig.defaultUserAvatar
                })
                userInfoMap[userID] = {
                    userID: userID,
                    userName: meetingMemberNames[i],
                    headImg: meetingMemberAvatars[i] ? "webapp/images/headimg/" + meetingMemberAvatars[i] : meetingConfig.defaultUserAvatar
                }
                if (userID == resp['currentUser']['user']) {
                    userIsInMembers = true
                }
            })

            if (!userIsInMembers) {
                meetingMembers.push({userID: userID, userName: userName, headImg: userAvatar})
                userInfoMap[userID] = ({userID: userID, userName: userName, headImg: userAvatar})
            }

            meetingConfig.presenterID = resp["loginId"];
            meetingConfig['userID'] = userID
            meetingConfig['userName'] = userName
            meetingConfig['meetingID'] = meetingID
            meetingConfig['meetingMembers'] = meetingMembers
            meetingConfig['isPresenter'] = resp['isPresenter']
            meetingConfig['userAvatar'] = userAvatar
            meetingConfig['videoRole'] = videoRole
            meetingConfig['isteudborad'] = isteudborad

            loginInfo['identifier'] = userID;
            loginInfo['identifierNick'] = userName;
            loginInfo['headurl'] = userAvatar

            window.userInfoMap = userInfoMap

            invokeFunc(callback);
        })
    }
    window.getMeetingInfo = getMeetingInfo;

    //从服务端获取用户token
    function getUserToken(callback) {

        var url = meetingConfig.vedioApiHost + "/project/genSignUpgrade.ajax"

        jsonp(url, {userID: meetingConfig.userID}, function (resp) {
            var userToken = resp.data.token;
            var appID = resp.data.skdAppid;
            var accountType = resp.data.accountType;

            meetingConfig['appID'] = appID;
            meetingConfig['ImAccountType'] = accountType;
            meetingConfig['userToken'] = userToken;

            loginInfo['sdkAppID'] = appID;
            loginInfo['accountType'] = accountType;
            loginInfo['userSig'] = userToken;

            invokeFunc(callback, userToken)
        })

    }

    //获取roomID
    function genRoomID(callback) {

        var url = meetingConfig.vedioApiHost + "/project/genRoomIDUpgrade.ajax"

        jsonp(url, {userID: meetingConfig.userID, meetingID: meetingConfig.meetingID}, function (resp) {
            console.log("genRoomIDUpgrade data: ", resp);
            // 使用TIC后只用到房间号
            var roomID = resp.data.roomID;
            if (!!resp.data.groupID) {
                var imGroupID = resp.data.groupID
                meetingConfig['existImGroupID'] = true
            } else {
                //var imGroupID = "@WSAP#" + new Date().getTime()
                var imGroupID = Date.parse(new Date()) / 1000;
                meetingConfig['existImGroupID'] = false
            }
            // 使用TIC后只用到房间号
            meetingConfig.roomID = roomID;
            meetingConfig['imGroupID'] = imGroupID
            meetingConfig['selToID'] = imGroupID
            invokeFunc(callback, roomID)
        })
    }

    // 渲染会诊成员的视频界面
    function showMeetingMemberVedio() {
        var $container = $(".meeting-content-right-content")
        $container.children().remove()

        meetingConfig.meetingMembers.forEach(function (user) {
            createMeetingMemberVedioElement(user.userID, user.userName)
        })
        // resetVedioSize();
    }

    function createMeetingMemberVedioElement(userID, userName) {
        var isPresenter = userID == meetingConfig.presenterID;
        
        var $container = $(".meeting-content-right-content");
        var $item = $("<div>").addClass("meeting-content-right-item").attr("user-id", userID);
        if(isPresenter){
            $item.addClass("presenter-mark");
        }
        $item.appendTo($container);
        
        var $itemPortrait = $("<div>").addClass("vedio portrait").append("<div class='div-show user-vedio'>待加入</div>");
        $itemPortrait.append($("<span>").text(userName).addClass("user-name"));
        $itemPortrait.appendTo($item);
        
        if(isPresenter){
            $('<i class="arrow-mark"></i>').appendTo($item);
        }
        var $vedioIcon = $("<img>").addClass("vedio-button active div-noshow").attr("src", "images/meeting/vedio.png");
        var $speakIcon = $("<img>").addClass("speak active div-noshow").attr("src", "images/meeting/speak.png");
        $itemPortrait.append($vedioIcon);
        $itemPortrait.append($speakIcon);
    }

    /** 音视频相关接口、事件函数 -- begin **/
    function onKickout() {
        alert("on kick out!");
    }

    function onRelayTimeout(msg) {
        alert("onRelayTimeout!" + (msg ? JSON.stringify(msg) : ""));
    }
    
    function isShareVideo(uid){
		if (uid.indexOf("SHARE_SCREEN_") == 0) {
			return true;
		}
		return false;
    }

    // 渲染视频画面
    function createVideoElement(id, userID, userName, vedioStream) {
    	if(isShareVideo(userID)) return;
        var $userItem = $(".meeting-content-right-content .meeting-content-right-item[user-id=" + userID + "]")
        if ($userItem.length == 0) {
            createMeetingMemberVedioElement(userID, userName)
            $userItem = $(".meeting-content-right-content .meeting-content-right-item[user-id=" + userID + "]")
        }

        var $videoDiv = $userItem.children(".vedio.portrait")
        $videoDiv.children(".div-show.user-vedio").hide()
        $videoDiv.children("video.user-vedio").remove()

        //$video = $("<video>").attr("id", id).attr("autoplay", "").attr("playsinline", "").addClass("portrait user-vedio").css("background-color", "#fff")
        $video = $("<video id='"+id+"' class='portrait user-vedio' style='background-color:#fff'></video>");

        if (userID == meetingConfig.userID) {
            // $video.attr("muted",true)
        }
        $videoDiv.prepend($video)

        var video = $video[0]
        video.srcObject = vedioStream;
        if (userID == meetingConfig.userID) {
            video.muted = true
            $userItem.find(".vedio-button").addClass("actable")
            $userItem.find(".speak").addClass("actable")
        }
        video.autoplay = true
        video.playsinline = true

        if (meetingConfig.isPresenter) {
            $userItem.find(".speak").addClass("actable")
            $userItem.find(".vedio-button").addClass("actable")
        }
        $userItem.find(".vedio-button").addClass("active div-show").removeClass("div-noshow")
        $userItem.find(".speak").addClass("active div-show").removeClass("div-noshow")

        $userItem.find("span").addClass("joined")
        // setTimeout(fuckReduce, 50)

        console.log("$video", $video.css("width"), $video.css("height"))
    }

    function onLocalStreamAdd(info) {
        console.log("local info", info)
        if (info.stream && info.stream.active === true) {
            createVideoElement(meetingConfig.userID, meetingConfig.userID, meetingConfig.userName, info.stream);         
        }
    }

    function onRemoteStreamUpdate(info) {    	
        console.info("远端流更新:", info);
        if(isShareVideo(info.userId)) return;
        if (info.stream && info.stream.active === true) {
            var id = info.videoId;
            var video = document.getElementById(id);
            if (!video) {
                createVideoElement(id, info.userId, info.userId, info.stream);
            }
        } else {
            console.log('欢迎用户' + info.userId + '加入房间');
        }
    }

    function onRemoteStreamRemove(info) {
        console.log(info.userId + ' 断开了连接');
        var $userItem = $(".meeting-content-right-content .meeting-content-right-item[user-id=" + info.userId + "]")
        $userItem.find("video").remove();
        $userItem.find(".user-vedio").show();
        if (meetingConfig.isPresenter) {
            $userItem.find("img.speak").removeClass("actable");
            $userItem.find("img.vedio-button").removeClass("actable");
        }
        $userItem.find("img.vedio-button").removeClass("div-show").addClass("div-noshow");
        $userItem.find("img.speak").removeClass("div-show").addClass("div-noshow");
        $userItem.find("span").removeClass("joined");
    }
    /** 音视频相关接口、事件函数 -- end **/

    /********************************************初始化TIC begin********************************************/
    function initTIC(opts) {
        var boardOption = {}, webrtcOption = {}, loginConfig = {};
        /*var users = test_accont.users;

        if(opts.userID == "web_tic_230"){
            var user = users[0];
            boardOption.userId = user.userId;
            boardOption.userSig = user.userToken;
        } else if (opts.userID == "web_tic_231"){
            var user = users[1];
            boardOption.userId = user.userId;
            boardOption.userSig = user.userToken;
        } else if (opts.userID == "web_tic_232"){
            var user = users[2];
            boardOption.userId = user.userId;
            boardOption.userSig = user.userToken;
        } else if (opts.userID == "web_tic_233"){
            var user = users[3];
            boardOption.userId = user.userId;
            boardOption.userSig = user.userToken;
        } else if (opts.userID == "web_tic_234"){
            var user = users[4];
            boardOption.userId = user.userId;
            boardOption.userSig = user.userToken;
        }*/
        // 初始化电子白板参数

        if (meetingConfig.isteudborad) {
            boardOption["id"] = "content_borad";
        }else{
            boardOption["id"] = "content_borad1";
        }
        boardOption["classId"] = opts.imGroupID;
        boardOption["sdkAppId"] = meetingConfig.appID;
        // boardOption["sdkAppId"] = test_accont.sdkappid;
        boardOption["ratio"] = "3:4";// 10:7
        boardOption["brushThin"] = 50;
        boardOption["toolType"] = 0;


        // loginConfig["userId"] = boardOption.userId;
        // loginConfig["userSig"] = boardOption.userSig;
        // 初始化登录TIC参数
        loginConfig["userId"] = meetingConfig.userID;
        loginConfig["userSig"] = meetingConfig.userToken;
        // 初始化RTC参数
        webrtcOption['role'] = meetingConfig.videoRole;
        this.tic = null;
        this.tic = new TIC({});
        // 初始化TIC
        this.tic.init(boardOption.sdkAppId, function (res) {
            if (res.code) {
                layer.msg('初始化失败，code:' + res.code + ' msg:' + res.desc);
            } else {
                login();
            }
        });
        // 登录
        function login() {
            this.tic.login(loginConfig, function (res) {
                console.log("login======================>", res)
                if (res.code) {
                    layer.msg('登录失败');
                    console.error(res);
                } else {
                    layer.msg('登录成功');
                    // 增加事件监听
                    addTICMessageListeners();
                    addTICEventListeners();
                    addTICStatusListeners();
                    if (!meetingConfig.existImGroupID){
                        console.log("未创建课堂，需要创建课堂");
                        if (meetingConfig.isPresenter) {
                            // 老师就创建课堂
                            createClassroom();
                        }
                    }else { // 如果是学生
                        console.log("已创建课堂，直接加入");
                        joinClassroom();
                    }
                }
            });
        }

        /**
         * 增加IM消息监听回调
         */
        function addTICMessageListeners() {
            /**
             * 收到C2C文本消息
             * @param fromUserId        发送此消息的用户id
             * @param text                收到消息的内容
             * @param textLen            收到消息的长度
             */
            function onTICRecvTextMessage(fromUserId, text, textLen) {
                console.log("收到C2C文本消息");
            }

            /**
             * 收到C2C自定义消息
             * @param fromUserId        发送此消息的用户id
             * @param data                收到消息的内容
             * @param dataLen            收到消息的长度
             */
            function onTICRecvCustomMessage(fromUserId, data, textLen) {
                console.log("收到C2C自定义消息");
            }

            /**
             * 收到群文本消息
             * @param fromUserId        发送此消息的用户id
             * @param text                收到消息的内容
             * @param textLen            收到消息的长度
             */
            function onTICRecvGroupTextMessage(fromUserId, text, textLen) {
                console.log("收到群文本消息", text);
            }

            /**
             * 收到群自定义消息
             * @param fromUserId        发送此消息的用户id
             * @param data                收到消息的内容
             * @param dataLen            收到消息的长度
             */
            function onTICRecvGroupCustomMessage(fromUserId, data, textLen) {
                console.log("收到群自定义消息")
            }

            /**
             * 所有消息
             * @param msg    IM消息体
             * @note 所有收到的消息都会在此回调进行通知，包括前面已经封装的文本和自定义消息（白板信令消息除外）
             */
            function onTICRecvMessage(msg) {
                console.log("所有消息======================>", msg);
                addMsg(msg);
            }

            var listener = {
                'onTICRecvTextMessage': onTICRecvTextMessage, // 收到C2C文本消息
                'onTICRecvCustomMessage': onTICRecvCustomMessage, // 收到C2C自定义消息
                'onTICRecvGroupTextMessage': onTICRecvGroupTextMessage, // 收到群文本消息
                'onTICRecvGroupCustomMessage': onTICRecvGroupCustomMessage, // 收到群自定义消息
                'onTICRecvMessage': onTICRecvMessage, // 所有消息
                //"onMsgNotify": onMsgNotify //监听新消息(私聊，普通群(非直播聊天室)消息，全员推送消息)事件，必填
            }

            this.tic.addTICMessageListener(listener);
        }

        // 事件监听回调
        function addTICEventListeners() {
            /**
             * 用户进入房间
             * @param {Array} members     进入房间的用户id
             */
            function onTICMemberJoin(members) {
                console.log('onTICMemberJoin == ' + members)
            }

            /**
             * 用户退出房间
             * @param {Array} members     退出房间的用户id
             */
            function onTICMemberQuit(members) {
                console.log('onTICMemberQuit == ' + members)
            }

            /**
             * 课堂被销毁
             */
            function onTICClassroomDestroy() {
                console.log('onTICClassroomDestroy ')
            }

            var lisenter = {
                'onTICMemberJoin': onTICMemberJoin, // 用户进入房间
                'onTICMemberQuit': onTICMemberQuit, // 用户退出房间
                'onTICClassroomDestroy': onTICClassroomDestroy // 课堂被销毁
            }
            this.tic.addTICEventListener(lisenter);
        }

        // IM状态监听回调
        function addTICStatusListeners() {
            this.tic.addTICStatusListener({
                'onTICForceOffline': function () {
                    layer.alert('其他地方登录，被T了');
                }
            });
        }

        function createClassroom() {
            this.tic.createClassroom(boardOption.classId, function (res) {
                console.log("createClassroom=============================>", res)
                if (res.code == 0) {
                    console.log('创建课堂成功');
                    meetingConfig['imGroupID'] = boardOption.classId;
                    //TODO 异步更新音视频房间对应的IM groupID
                    var url = meetingConfig.vedioApiHost + "/project/createRoomSuccessCallBackUpgrade.ajax";
                    jsonp(url, {meetingID: meetingConfig.meetingID, groupID: boardOption.classId}, function (resp) {
                        console.log("保存groupID成功")
                    });
                    if (meetingConfig.isPresenter) {
                        console.log("老师创建课堂成功，准备加入课堂");
                    }
                } else if (res.code == 10025){
                    console.log("您已经创建该群聊");
                }else if(res.code == 10021){
                    console.log("群组 ID 已被使用，请选择其他的群组 ID");
                }else{
                    console.log("创建群聊失败, 老师就创建课堂:", res)
                }
                // 老师就创建课堂
                joinClassroom();
            });
        }

        // 进入房间
        function joinClassroom() {
            this.tic.joinClassroom(boardOption.classId, webrtcOption, boardOption, function (res) {
                if (res.code == 0) {
                    console.log('加入课堂成功');
                } else if(res.code == 10013 ){
                    console.log("您已经是该组成员");
                }
                window.webim = this.tic.getImInstance();
                initTRTCEvent(webrtcOption);

                initTEudborad();
                initWebIM();
                showHistoryMsg();
            });
        }

        //销毁会诊
        function destroyClassroom() {
            tic.destroyClassroom(boardOption.classId, function (res) {
                vedioFlagController.clean();
                audioFlagController.clean();
                if (res.code) {
                    console.error('销毁会诊失败');
                } else {
                    console.log('销毁会诊成功');

                }
            });
        }

        function quitMeeting(){
            //非主持人仅退出  不解散群组
            if(!meetingConfig.isPresenter){
                fireUserQuitMetting()

            }else{
                //解散聊天群
                console.log("准备调用腾讯接口清理房间数据")
                destroyClassroom();
                EventUtils.fire("destroyGroupOwnerCallback");
                var toUserID = meetingConfig.meetingMembers.length > 0 ? meetingConfig.meetingMembers[0].userID : meetingConfig.userID
                IMUtils.fire("userQuitMeeting", toUserID, {msgContent:"主持人退出", time:Date.parse(new Date())/1000})
            }
        }

        function fireUserQuitMetting(){
            var msgContent = meetingConfig['userName'] +"已退出了会诊"
            var toUserID = meetingConfig.meetingMembers.length > 0 ? meetingConfig.meetingMembers[0].userID : meetingConfig.userID
            IMUtils.fire("userQuitMeeting", toUserID, {msgContent:msgContent, time:Date.parse(new Date())/1000 , isExit:true})
        }

        window.quitMeeting = quitMeeting;
    }




    /********************************************初始化TIC end**********************************************/


    /********************************************初始化TIC begin********************************************/
    function initTRTCEvent(webrtcOption) {
        
        
        window.RTC = this.tic.getWebRTCInstance();
        var attributes = {};
        if (webrtcOption.role == 'user43') {
            attributes = {width: 640, height: 480, frameRate: 20}
        }
        if (webrtcOption.role == 'user169') {
            attributes = {width: 1280, height: 720, frameRate: 15}
        }
        RTC.getLocalStream({
            video: true,
            audio: true,
            attributes: attributes
        }, function (info) {
            console.log("local info", info)
            if (info.stream && info.stream.active === true) {
                RTC.startRTC({
                    stream: info.stream,
                    role: meetingConfig.videoRole
                }, function (data) {
                    // 成功
                    console.log('更新流成功');
                    createVideoElement(meetingConfig.userID, meetingConfig.userID, meetingConfig.userName, info.stream);

                    if (vedioFlagController.isDisable(meetingConfig.userID)) {
                        //视频被禁用
                        IMUtils.fire("unableUserVedio", meetingConfig.userID, {toUserID: meetingConfig.userID})
                    };
                    //渲染不可用
                    // vedioFlagController.disableList.forEach(function(item){
                    //     setVedioUnable(item);
                    // })

                    if (audioFlagController.isDisable(meetingConfig.userID)) {
                        //音频被禁用
                        IMUtils.fire("unableUserAudio", meetingConfig.userID, {toUserID: meetingConfig.userID})
                    };

                    (function(){
                        var increate = 0;
                        var timer = setInterval(function(){
                            if(increate > 1000){
                                clearInterval(timer);
                            }
                            console.log("开始查询vedio flag")
                            if ($(".vedio-button.div-show").parents(".meeting-content-right-item[user-id!="+meetingConfig.userID+"]").length > 0){
                                var toUserID = $(".vedio-button.div-show").parents(".meeting-content-right-item[user-id!="+meetingConfig.userID+"]:first").attr("user-id");
                                IMUtils.fire("queryVedioDisableList", toUserID, {fromUserID:meetingConfig.userID});
                                console.log("查询vedio flag ,发送消息")
                                clearInterval(timer);
                            }
                            increate++;
                        },10)
                        
                    })();

                }, function (error) {
                    console.error('更新流失败', error);
                });
            }
        }, function (error) {
            console.error(error)
        });
        // 本地流新增
        RTC.on("onLocalStreamAdd", onLocalStreamAdd);
        // 远端流新增/更新
        RTC.on("onRemoteStreamUpdate", onRemoteStreamUpdate);
        // 远端流断开
        RTC.on("onRemoteStreamRemove", onRemoteStreamRemove);
        // 重复登录被T
        //RTC.on("onKickout", onKickout);
        // 服务器超时
        //RTC.on("onRelayTimeout", onRelayTimeout);

        RTC.on("onErrorNotify", function (info) {
            console.error(info)
            if (info.errorCode === RTC.getErrorCode().GET_LOCAL_CANDIDATE_FAILED) {
                // alert( info.errorMsg )
            }
        });

        RTC.on("onStreamNotify", function (info) {
            // console.warn('onStreamNotify', info)
        });
        RTC.on("onWebSocketNotify", function (info) {
            // console.warn('onWebSocketNotify', info)
        });
        RTC.on("onUserDefinedWebRTCEventNotice", function (info) {
            // console.error( 'onUserDefinedWebRTCEventNotice',info )
        });
    }
    /********************************************初始化TIC end*********************************************/
    /********************************************初始化WebIM begin*****************************************/
    function initWebIM() {

        IMUtils.register("yefei自定义事件", function(d){console.log("收到自定义消息类型 yefei自定义事件",d)},function(d){console.log("发送自定义消息类型 yefei自定义事件",d)});

        function fireAddRoom(){
            //userAddMeeting
            var user = {
                userID : meetingConfig.userID,
                userName : meetingConfig.userName,
                headImg: meetingConfig.userAvatar
            }

            var toUserID = meetingConfig.meetingMembers.length > 0 ? meetingConfig.meetingMembers[0].userID : meetingConfig.userID
            IMUtils.fire("userAddMeeting", toUserID, {user:user})
        }

        //转让群主
        function changeGroupOwner(toUserID){

            var argsArr = []
            for(var i in arguments){
                if(i > 0){
                    argsArr.push(arguments[i])
                }
            }

            var options = {
                'GroupId': meetingConfig.imGroupID,
                "NewOwner_Account": toUserID
            };
            webim.changeGroupOwner(
                options,
                function (resp) {
                    console.log("转让群主成功", resp)
                    changeToNotGroupOwner()
                    EventUtils.fire("changeGroupOwnerOrgPresenterCallback", argsArr)
                },
                function (err) {
                    console.log("转让群主失败", err);
                }
            );
        }

        //变更为群主
        function changeToGroupOwner(){
            meetingConfig.isPresenter = true;
            $("video.portrait").parent().nextAll("img.vedio-button").addClass("actable")
            $("video.portrait").parent().nextAll("img.speak").addClass("actable")
        }
        //变更为非群主
        function changeToNotGroupOwner(){
            meetingConfig.isPresenter = false;
            $(".meeting-content-right-item[user-id!=" + meetingConfig.userID + "] .vedio-button").removeClass("actable")
            // $("video.portrait").parent().nextAll("img.speak").removeClass("actable")
            $(".meeting-content-right-item[user-id!=" + meetingConfig.userID + "] .speak").removeClass("actable")
        }

        window.getRoleInGroup = function () {
            var options = {
                GroupId: meetingConfig.imGroupID,
                User_Account: [meetingConfig.userID]

            }
            webim.getRoleInGroup(options, function (resp) {
                    console.log("查询成员角色成功", resp)
                },
                function (err) {
                    console.log("查询成员角色败", err);

                })
        }
        fireAddRoom();
        initEmotionUL();

        window.changeGroupOwner = changeGroupOwner;
        window.changePresenter = changeGroupOwner;
        window.changeToGroupOwner = changeToGroupOwner;
        window.changeToNotGroupOwner = changeToNotGroupOwner;
    }
    /********************************************初始化WebIM end******************************************/
    /********************************************TEudborad begin******************************************/
    function initTEudborad() {
        if (!meetingConfig.isteudborad){
        }

        window.teduBoard = this.tic.getBoardInstance();
        window.currentFile = null;// 当前文件
        window.boardFileGroup = [];
        window.boardData = {
            data: {
                current: null,
                list: []
            },
            page: {
                current: 0,
                total: 0
            }
        }


        // 监听白板事件（按需监听）
        teduBoard.on('TEB_FILEUPLOADPROGRESS', function (data) {
            var percent = parseInt(data.percent * 100);
            console.log('======================:  ', 'TEB_FILEUPLOADPROGRESS', '====================:', JSON.stringify(data));
            if (percent <= 100 && data.speed != 0) {
                layer.msg('上传进度:' + percent + '%');
            }
        });
        teduBoard.on('TEB_INIT', function () {
            console.log('======================:  ', 'TEB_INIT');
        });
        teduBoard.on('TEB_SYNCDATA', function (data) {
            // teduBoard.addSyncData(data);
            console.log('======================:  ', 'TEB_SYNCDATA');
        });

        // 新增白板
        teduBoard.on('TEB_ADDBOARD', function (boardId, fid) {
            console.log('======================:  ', 'TEB_ADDBOARD');
            proBoardData(teduBoard);
        });
        teduBoard.on('TEB_GOTOBOARD', function () {
            console.log('======================:  ', 'TEB_GOTOBOARD');
            proBoardData(teduBoard);
        });
        teduBoard.on('TEB_ADDH5PPTFILE', function () {
            console.log('======================:  ', 'TEB_ADDH5PPTFILE');
            proBoardData(teduBoard);
        });
        teduBoard.on('TEB_ADDFILE', function (fId) {
            console.log('======================:  ', 'TEB_ADDFILE');
            proBoardData(teduBoard);
            var fid = teduBoard.getCurrentFile();
            if(fId != "#DEFAULT" && fid == fId){
            	$("#rack-select").find("option[value = '"+ fid +"']").attr("selected","selected");
            }else{
            	$("#rack-select").find("option[value = '"+ fId +"']").attr("selected","selected");
            }
        });
        teduBoard.on('TEB_DELETEFILE', function () {
            console.log('======================:  ', 'TEB_DELETEFILE');
            proBoardData(teduBoard);
        });

        teduBoard.on('TEB_SWITCHFILE', function () {
            console.log('======================:  ', 'TEB_SWITCHFILE');
            proBoardData(teduBoard);
        });

        function proBoardData(teduBoard) {
            this.currentFile = teduBoard.getCurrentFile();
            this.boardFileGroup = teduBoard.getFileInfoList();
            console.log("this.boardFileGroup==>",this.boardFileGroup)
            var fid = $('#rack-select').val();
            $(".select-file").hide();
            $('#rack-select').empty();
            if(this.boardFileGroup && null != this.boardFileGroup && this.boardFileGroup != ""){
                $.each(this.boardFileGroup, function (index, item) {
                    var title = item.title;
                    if(title == '#DEFAULT'){
                    	title = '电子白板页面';
                    }
                	var option = new Option(title, item.fid);
                    if(fid && item.fid == fid){
                        option.setAttribute("selected",'true');
                    }
                    $('#rack-select').append(option);//往下拉菜单里添加元素
                });
                if(this.boardFileGroup.length >1){
                	$(".select-file").show();
                }
            }

            var currentBoard = teduBoard.getCurrentBoard();

            var boards = teduBoard.getFileBoardList(this.currentFile);

            this.boardData.page = {
                current: boards.indexOf(currentBoard) + 1,
                total: boards.length
            }
            $(".boardPage").html("<p>" + boardData.page.current + "&nbsp;/&nbsp;" + boardData.page.total + "&nbsp;页</p >");
        }

        //rgba转十六进制
        function hexify(color) {
            var values = color
                .replace(/rgba?\(/, '')
                .replace(/\)/, '')
                .replace(/[\s+]/g, '')
                .split(',');
            var a = parseFloat(values[3] || 1),
                r = Math.floor(a * parseInt(values[0]) + (1 - a) * 255),
                g = Math.floor(a * parseInt(values[1]) + (1 - a) * 255),
                b = Math.floor(a * parseInt(values[2]) + (1 - a) * 255);
            return "#" +
                ("0" + r.toString(16)).slice(-2) +
                ("0" + g.toString(16)).slice(-2) +
                ("0" + b.toString(16)).slice(-2);
        }


        $("body").on("click", ".set-color", function () {
            var bgColor = hexify(teduBoard.getBackgroundColor());
            $("#color_a").val(bgColor);
            $("#color_a").css("background-color", bgColor);
            var gbgcolor = hexify(teduBoard.getGlobalBackgroundColor());
            $("#color_b").val(gbgcolor);
            $("#color_b").css("background-color", gbgcolor);
            var brushColor = hexify(teduBoard.getBrushColor());
            $("#color_c").val(brushColor);
            $("#color_c").css("background-color", brushColor);
            var textColor = hexify(teduBoard.getTextColor());
            $("#color_d").val(textColor);
            $("#color_d").css("background-color", textColor);

        })


        // 设置电子白板比例
        $("body").on("click", ".video-collapse", function () {
            //if (meetingConfig.isPresenter) {
                $(".meeting-content-right").hide();
                $(".meeting-content-middle").removeClass("layui-col-md5").addClass("layui-col-md9");
                $(".open-video-right").show();
                // teduBoard.setBoardRatio("10:7");
                teduBoard.setBoardRatio("9:12");
                teduBoard.resize();
            //}
        });
        $("body").on("click", ".open-video-collapse", function () {
            //if (meetingConfig.isPresenter) {
                $(".meeting-content-middle").removeClass("layui-col-md9").addClass("layui-col-md5");
                $(".meeting-content-right").show();
                $(".open-video-right").hide();
                $("#content_borad").css("margin-top","-2px");
                teduBoard.setBoardRatio("3:4");
                teduBoard.resize();
            //}
        });

        //页面操作
        $("body").on("click", "#addBoard", function () {
            teduBoard.addBoard();
			$("#rack-select").find("option[value = '#DEFAULT']").attr("selected","selected");
        });
        $("body").on("click", "#deleteBoard", function () {
            teduBoard.deleteBoard();
        });

        $("body").on("click", "#prevBoard", function () {
            teduBoard.prevBoard();
        });
        $("body").on("click", "#nextBoard", function () {
            teduBoard.nextBoard();
        });
        
        //设置
        $("body").on("change", "#color_a", function () {
            var color = $("#color_a").val();
            teduBoard.setBackgroundColor(color);
        });
        $("body").on("change", "#color_b", function () {
            var color = $("#color_b").val();
            teduBoard.setGlobalBackgroundColor(color);
        });

        $("body").on("change", "#color_c", function () {
            var color = $(this).val();
            teduBoard.setBrushColor(color);
        });

        $("input[name='lines']").click(function () {
            var size = $(this).val();
            var thin = 50;
            switch (size) {
                case '1' :
                    thin = 50;
                    break;
                case '2' :
                    thin = 100;
                    break;
                case '3' :
                    thin = 150;
                    break;
            }
            teduBoard.setBrushThin(thin);
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_PEN);
            $(this).parents("div:first").hide();
        });


        //撤销
        $("body").on("click", "#previous", function () {
            teduBoard.undo();
        });
        $("body").on("click", "#nextStep", function () {
            teduBoard.redo();
        });

        //清除
        $("body").on("click", "#c-graffiti", function () {
            teduBoard.clear();
        });
        $("body").on("click", "#c-graffiti-bg", function () {
            teduBoard.clear();
            teduBoard.setBackgroundColor("#ffffff");
        });
        $("body").on("click", "#c-bg", function () {
            teduBoard.setGlobalBackgroundColor("#ffffff");
        });
        $("body").on("click", "#c-graffiti-img", function () {
            teduBoard.clear(true);
        });

        //文件操作
        $("body").on("click", ".fileOperation", function () {
            document.getElementById("fileSelector").click();
        });
        //选择
        $("body").on("click", "#click-s", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_POINT_SELECT);
        });
        $("body").on("click", "#frame-s", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_RECT_SELECT);
        });

        //橡皮擦
        $("body").on("click", "#eraser", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_ERASER);
        });

        //图形
        $("body").on("click", "#brush", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_PEN);
        });
        $("body").on("click", "#line", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_LINE);
        });

        $("body").on("click", "#h-rectangle", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_RECT);
        });
        $("body").on("click", "#s-rectangle", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_RECT_SOLID);
        });
        //空心椭圆
        $("body").on("click", "#h-oval", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_OVAL);
        });
        //实心椭圆
        $("body").on("click", "#s-oval", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_OVAL_SOLID);
        });

        //文本设置
        $("body").on("click", "#textIn", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_TEXT);
            $(this).parent().parent(".son").hide();
        });
        //文字颜色
        $("body").on("change", "#color_d", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_TEXT);
            var color = $(this).val();
            teduBoard.setTextColor(color);
        });
        // 文字字体
        $("body").on("change", "#fontval", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_TEXT);
            var fontval = $(this).val();
            teduBoard.setTextFamily(fontval);
            $(this).parent().parent().parent().parent(".son").hide();
        });
        //设置文本大小
        $("body").on("change", "#font_size", function () {
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_TEXT);
            var fontSize = $(this).val();
            var size = fontSize * 26.5;
            teduBoard.setTextSize(size);
            $(this).parent().parent().parent().parent(".son").hide();
        });
        //放大缩小
        $("body").on("click", "#zoomIn", function () {
            var scale = teduBoard.getBoardScale();
            teduBoard.setBoardScale(scale + 50);
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_ZOOM_DRAG);
        });
        $("body").on("click", "#zoomOut", function () {
            var scale = teduBoard.getBoardScale();
            teduBoard.setBoardScale(scale - 50);
            teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_ZOOM_DRAG);
        });

        var boardcanvas = document.getElementById("tx_board_canvas_box");
        /*if (boardcanvas.addEventListener && meetingConfig.isPresenter) {
            // IE9, Chrome, Safari, Opera
            boardcanvas.addEventListener("mousewheel", MouseWheelHandler, false);
            // Firefox
            boardcanvas.addEventListener("DOMMouseScroll", MouseWheelHandler, false);
        }*/

        // 鼠标移入某区域屏蔽鼠标滚轮 滚动滚动条
        function MouseWheelHandler(e) {
            // 对电子白板鼠标滚动放大缩小，阻止视窗滚动
            var e = window.event || e;
            if (e.stopPropagation) {
                e.stopPropagation();
            } else {
                e.cancelBubble = true;
            }
            if (e.preventDefault) {
                e.preventDefault();
            } else {
                e.returnValue = false;
            }
            var delta = Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail)));
            if (delta > 0) {
                // 放大
                var scale = teduBoard.getBoardScale();
                teduBoard.setBoardScale(scale + 50);
                teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_ZOOM_DRAG);
            } else {
                // 缩小
                var scale = teduBoard.getBoardScale();
                teduBoard.setBoardScale(scale - 50);
                teduBoard.setToolType(TEduBoard.TOOL_TYPE.TEDU_BOARD_TOOL_TYPE_ZOOM_DRAG);
            }
            return false;

        }

        $("body").on("change", ".input_cxcolor", function () {
            $(this).parents("div:first").hide();
        });

        // 文件操作
        $("body").on("click", "#addFile", function () {
            $('#file_input').click();
        });
        // 选择切换文件
        $('#rack-select').change(function () {
            var fid  = $(this).val();
            teduBoard.switchFile(fid);

        });
        // 删除文件：要删除的文件ID， 文件ID为空字符串时表示当前文件，默认文件无法删除
        $("body").on("click", "#delFile", function () {
            // 获取白板中上传的所有文件的文件信息列表
            var fileList = teduBoard.getFileInfoList();
            var size = fileList.length - 1;
            console.log("fileList=", JSON.stringify(fileList));
            // 获取当前文件ID
            var fid = teduBoard.getCurrentFile();
            /*if(fid == "#DEFAULT"){
                layer.alert("无法删除默认文件，请切换文件。", {icon: 2});
                return;
            }*/
            teduBoard.deleteFile(fid);
            for (var i = 0; i <= size; i++) {
                var nextFid = fileList[i].fid;
                if (fid == nextFid && size == i) {
                    teduBoard.switchFile(fileList[0].fid);
                    break;
                } else if (fid == nextFid) {
                    teduBoard.switchFile(fileList[i + 1].fid);
                    break;
                }
            }
        });
        //文件切换
        $("body").on("click", "#switchFile", function () {
            console.log("要切换到的文件ID， 文件ID为必填项，为空字符串将导致文件切换失败");
            // 获取当前文件ID
            var fid = teduBoard.getCurrentFile();
            var fileList = teduBoard.getFileInfoList();
            console.log("fileList=", JSON.stringify(fileList));
            var size = fileList.length - 1;
            if(size == 0){
                //layer.alert("当前无文件可切换，请上传文件。", {icon: 2});
                //return;
            }
            $(".select-file").show();
            for (var i = 0; i <= size; i++) {
                var fid1 = fileList[i].fid;
                console.log("fid=" + fid + ", fid1=" + fid1 + ", i=" + i);
                if (fid == fid1 && size == i) {
                    teduBoard.switchFile(fileList[0].fid);
                    $("#rack-select").find("option[value = '"+ fileList[0].fid +"']").attr("selected","selected");
                    break;
                } else if (fid == fid1) {
                    var value = fileList[i + 1].fid;
                    teduBoard.switchFile(value);
                    $("#rack-select").find("option[value = '"+ value +"']").attr("selected","selected");
                    break;
                }
            }
        });

        /**
         * 上传文件
         *
         * 当前fileObj为String类型，表示只接收腾讯云COS文件url地址。适用于已有COS文件的url地址，且COS开通了转码功能。
         */
        function uploadFile() {
            var file = document.getElementById('file_input').files[0];
            var i = file.name.lastIndexOf(".") + 1;
            var s = file.name.substring(i).toUpperCase();
            // COS文件url地址，只支持PPT、PDF、Word、Excel
            if ((file.type).indexOf("image/") == -1 && s != "PPT" && s != "PPTX" && s != "PDF" && s != "XLS" && s != "XLSX" && s != "DOCX" && s != "DOC") {
                layer.msg("上传失败：只支持图片、PPT、PDF、Word、Excel", {icon: 2});
                return;
            }
            layer.msg('文件正在上传，请等待');
            // 业务侧逻辑
            teduBoard.addFile({
                data: file
            }, function (succ) {
                console.log(succ);
            }, function (err) {
                layer.alert("文件上传失败，请重试", {icon: 2});
                console.log(err);
            });
            document.getElementById('file_input').value = '';
        }

        window.uploadFile = uploadFile;
        window.currentFile = currentFile;
        window.boardData = boardData;
    }
    /********************************************TEudborad end********************************************/

    // 计算视频窗口上用户姓名和静音按钮的位置
    function fuckReduce() {
        $("#divCamera video").each(function (i, item) {
            var width = parseInt($(item).css("width").replace("px", ""))
            var height = parseInt($(item).css("height").replace("px", ""))
            var maxWidth = height * 16 / 9;
            var margin = (maxWidth - width) / 2 + 5

            var prev = $(item).data("fuck-reduce-margin");
            // if(prev != margin){
            $(item).data("fuck-reduce-margin", margin);
            margin = margin + (Math.random()) / 100
            $(item).nextAll("span").css("left", margin + "px")
            $(item).nextAll("img.speak").css("right", margin + "px")
            $(item).nextAll("img.vedio-button").css("right", (margin+62) + "px")
            // }
        })
        
        // 表情位置
        $(".im-face-set").css("bottom", $(".meeting-chat.layui-col-md12").get(0).clientHeight - $(".meeting-chat-content").get(0).clientHeight - $(".meeting-chat-header").get(0).clientHeight + "px")
       
        resetVedioSize();
    }
    
    
    $(window).resize(function () {
    	refreshWin();
    });

    setTimeout(function () {
    	refreshWin();
    }, 500)
    
    
    //刷新比例
    function refreshWin(){
    	//IM内容高度
    	var chatContentHeight = $(window).height() - 340 - 100 - 45 - 100;
        $(".meeting-chat-content").css("height",chatContentHeight+"px");
        $(".meeting-content-right-content").css("height",$(".meeting-nav").height()-60+"px");
        $(".meeting-chat").css("height", $(".meeting-chat-send").height()+chatContentHeight+60+"px");
        
        //聊天窗口滚动到下方
        $('.meeting-chat-content').scrollTop( $('.meeting-chat-content')[0].scrollHeight);
        //画布高度
        var contentMiddleHeight = $(window).height() - 60;
        $('.content-middle').css("height",contentMiddleHeight+"px");
    }
    

    function quitRTC() {
        if (typeof RTC == "undefined") {
            console.log("RTC没有初始化，不需要退出！")
        } else {
            RTC.quit(
                function () {
                    //退出成功
                    console.log("RTC退出成功！")
                }, function () {
                    console.log("RTC退出失败！")
                });
        }

    }

    function resetVedioSize() {
        var count = $(".meeting-content-right-item").length;
        if(count == 0)return;
        var w1, h1, w2, h2, r1, r2, col=1, row=1, gap=8;
        w1 = $(".meeting-content-right-content").width();
        var isMax = w1>600;
        if(isMax){
            col = count>4?3:2;
            w1 = $("#divCamera").width();
            h1 = $("#divCamera").height();
        }else{
            col = count>4?2:1;
            w1 = $(".meeting-content-right-content").width();
            h1 = $(document).height();
        }
        h1 -= $("#divCamera .content-right-header").height();
        row = parseInt(count / col + (count % col == 0 ? 0 : 1));
        r1 = (w1 - (col+1) * gap) / (16 * col);
        r2 = (h1 - (row+1) * gap) / (9 * row);
        if(r1 > r2) r1 = r2;
        w2 = parseInt(16 * r1) ;
        h2 = parseInt(9 * r1);
        var left = parseInt((w1 - (w2+ gap)*col-gap) /2);
        var top  = - h2;
        if(isMax){
        	top = parseInt((h1 - (h2+ gap)*row-gap) /2 - h2);
        }
        $(".meeting-content-right-item").css("width", w2-10);
        $(".meeting-content-right-item").css("height", h2-10);
        $(".meeting-content-right-item").each(function(index){        	
            if(index%col==0){
                top +=h2+gap;
                r1 = left + gap;
            }else{
                r1+=w2+gap;
            }
            $(this).css("left",r1).css("top",top-5);
        });
    }

    //选中表情
    function selectEmotionImg(img) {
        var imgEle = replace_em(img.id)
        var $edit = $(window.frames['LAY_layedit_1'].document).find("body")
        $edit.html($edit.html() + imgEle)
    }

    function initEmotionUL() {
        for (var index in webim.Emotions) {
            var emotions = $('<img>').attr({
                "id": webim.Emotions[index][0],
                "src": webim.Emotions[index][1],
                "style": "cursor:pointer;"
            }).click(function () {
                // selectEmotionImg(this) ;
            })
            $('<li>').append(emotions).appendTo($('#emotionUL'));
        }


        var fontFamily = localStorage.getItem("userFontFamily-" + meetingConfig.userID);
        var fontSize = localStorage.getItem("userFontSize-" + meetingConfig.userID);
        if (fontFamily) {
            $(".font-name-select option[font-class=" + fontFamily + "]").prop("selected", true);

        }
        if (fontSize) {
            $(".font-size-select").val(fontSize)
        }

    }

    //发送普通消息
    function sendMsg() {
//      var ele = document.getElementById("msg-input");//获取富文本编辑器的值
//      console.log(ele.value)
        var $edit = $(window.frames['LAY_layedit_1'].document).find("body");
        var msgContent = $edit.html();
//        console.log("获取的语音数据")
//            console.log(msgContent);//保存下这个打开
        if (!msgContent.trim()) {
            return;
        }

        if (msgContent.search(/^<p>(<br>)+?<\/p>$/) == 0) {
            return;
        }

        var fontFamily = localStorage.getItem("userFontFamily-" + meetingConfig.userID);
        var fontSize = localStorage.getItem("userFontSize-" + meetingConfig.userID);

        if (!fontFamily) {
            fontFamily = "Microsoft YaHei"
        }

        if (!fontSize) {
            // fontSize = "12px"
            fontSize = "12"
        } else {
            // fontSize += "px"
        }

        $span = $("<div>")
        if (fontFamily) {
            // $span.css("font-family", fontFamily);
            $span.addClass("font-family-" + fontFamily);
        }
        if (fontSize) {
            // $span.css("font-size", fontSize)
            $span.addClass("font-size-" + fontSize)
        }

        $container = $("<div>").append($span)
        $span.text(msgContent);
        $container.append($span);


        var msg = $container.html()

        // 这种方式可以接收到信息但是没有历史
        /*this.tic.sendGroupTextMessage(msg, function (res) {
            console.log('===sendTextMessage:', res);
        });*/
        // 这种方式发送后没有监听，但刷新页面可以看到历史信息
        onSendMsg(msg);

        var $edit = $(window.frames['LAY_layedit_1'].document).find("body")
        $edit.html("")
        //发送完成后清空文本域中获取的内容
         var ele = document.getElementById("msg-input");//获取文本域的id
         ele.value = "";//给它赋值为空
    }

    var QUICK_ENTER = 1;
    var QUICK_CTRL_ENTER = 2;

    function getQuickKey() {
        return localStorage.getItem("userSendMsgQuickKey-" + meetingConfig.userID);
    }

    function setQuickKey(key) {
        localStorage.setItem("userSendMsgQuickKey-" + meetingConfig.userID, key);
    }

    function showQuickKeySelect(quickKey) {
        if (quickKey == QUICK_ENTER) {
            $(".quick-send-item-icon.enter").addClass("active")
            $(".quick-send-item-icon.ctrl-enter").removeClass("active")
        } else {
            $(".quick-send-item-icon.enter").removeClass("active")
            $(".quick-send-item-icon.ctrl-enter").addClass("active")
        }
    }

    function initQuickKey() {
        var key = getQuickKey();
        if (!key) {
            key = QUICK_CTRL_ENTER;
            setQuickKey(key)
        }

        showQuickKeySelect(key);

    }

    function setAudioEnable(userID) {
        audioFlagController.remove(userID);
        $(".meeting-content-right-item[user-id=" + userID + "] .speak").attr("src", "images/meeting/speak.png").addClass("active")
    }
    function setAudioUnable(userID) {
        audioFlagController.add(userID);
        $(".meeting-content-right-item[user-id=" + userID + "] .speak").attr("src", "images/meeting/nospeak.png").removeClass("active")
    }


    function setVedioEnable(userID) {
        vedioFlagController.remove(userID);
        $(".meeting-content-right-item[user-id=" + userID + "] .vedio-button").attr("src", "images/meeting/vedio.png").addClass("active")
    }
    function setVedioUnable(userID) {
        vedioFlagController.add(userID);
        $(".meeting-content-right-item[user-id=" + userID + "] .vedio-button").attr("src", "images/meeting/novedio.png").removeClass("active")
    }
    function handleUserAudio() {
        //绑定禁用语音事件
        IMUtils.register("unableUserAudio", function (data) {
            //被禁用方
            RTC.closeAudio()
            setAudioUnable(data.toUserID)
        }, function (data) {
            //发起方
            setAudioUnable(data.toUserID)
        }, function (data) {
            //其他人
            setAudioUnable(data.toUserID)
        })

        //绑定启用语音事件
        IMUtils.register("enableUserAudio", function (data) {
            //被启用方
            RTC.openAudio()
            setAudioEnable(data.toUserID)

        }, function (data) {
            //发起方
            setAudioEnable(data.toUserID)
        }, function (data) {
            //其他人
            setAudioEnable(data.toUserID)
        })



        //绑定禁用video事件
        IMUtils.register("unableUserVedio", function (data) {
            //被禁用方
            RTC.closeVideo()
            setVedioUnable(data.toUserID)
        }, function (data) {
            //发起方
            setVedioUnable(data.toUserID)
        }, function (data) {
            //其他人
            setVedioUnable(data.toUserID)
        })

        //绑定启用video事件
        IMUtils.register("enableUserVedio", function (data) {
            //被启用方
            RTC.openVideo()
            setVedioEnable(data.toUserID)

        }, function (data) {
            //发起方
            setVedioEnable(data.toUserID)
        }, function (data) {
            //其他人
            setVedioEnable(data.toUserID)
        })


        // 从本地获取视频被禁用列表，并发送给查询人
        function getVedioDisableListAndSend(fromUserID) {
            console.log("收到 " + fromUserID + " 查询视频禁用列表的请求")
            var timer = setInterval(function(){
                if (vedioFlagController && audioFlagController) {
                    console.log("发送给 " + fromUserID + " 视频禁用列表："+ vedioFlagController.disableList)
                    console.log("发送给 " + fromUserID + " 音频禁用列表："+ audioFlagController.disableList)
                    IMUtils.fire("sendVedioDisableList", fromUserID, {
                        "vedioDisableList": vedioFlagController.disableList
                        ,"audioDisableList": audioFlagController.disableList
                    })
                    clearInterval(timer);
                } else {
                    console.log("vedioFlagController is null")
                }

            },10)
        }

        //查询视频禁用的用户
        IMUtils.register("queryVedioDisableList", function (data) {
            //接收方
            getVedioDisableListAndSend(data.fromUserID);

        }, function (data) {
            //发起方
        }, function (data) {
            //其他人
        })


        // 添加视频被禁用列表
        function batchAddVedioDisableList(vedioDisableList){
            
             var timer = setInterval(function(){
                if (vedioFlagController) {
                    vedioDisableList.forEach(function(item){
                        setVedioUnable(item);
                    })
                } else {
                    console.log("vedioFlagController is null")
                }

            },10)
        }


        // 添加音频频被禁用列表
        function batchAddAudioDisableList(audioDisableList){

             var timer = setInterval(function(){
                if (audioFlagController) {
                    audioDisableList.forEach(function(item){
                        setAudioUnable(item);
                    })
                } else {
                    console.log("audioFlagController is null")
                }

            },10)
        }

        //发送视频禁用的用户
        IMUtils.register("sendVedioDisableList", function (data) {
            console.log("收到视频禁用列表："+ JSON.stringify(data))
            //接收方
            vedioFlagController.disableList = data.vedioDisableList;
            batchAddVedioDisableList(data.vedioDisableList);

            audioFlagController.disableList = data.audioDisableList;
            batchAddAudioDisableList(data.audioDisableList);
        }, function (data) {
            //发起方
            
        }, function (data) {
            //其他人
            
        })

        
        
        $(document).on("click", ".meeting-content-right-content img.speak.actable", function (event) {
            if (meetingConfig.isPresenter || meetingConfig.userID == $(this).parents(".meeting-content-right-item:first").attr("user-id")) {
                //操作人是主讲人
                if ($(this).prevAll("video.portrait").length == 1) {
                    //被操作人已经有视频
                    var toUserID = $(this).parents(".meeting-content-right-item[user-id]").attr("user-id")
                    if ($(this).hasClass("active")) {
                        //当前是可用状态,切换至禁用状态
                        IMUtils.fire("unableUserAudio", toUserID, {toUserID: toUserID})
                    } else {
                        IMUtils.fire("enableUserAudio", toUserID, {toUserID: toUserID})
                    }
                }
            }
        })
        
        
        $(document).on("click", ".meeting-content-right-content img.vedio-button.actable", function (event) {
            if (meetingConfig.isPresenter || meetingConfig.userID == $(this).parents(".meeting-content-right-item:first").attr("user-id")) {
                //操作人是主讲人
                if ($(this).prevAll("video.portrait").length == 1) {
                    
                }
              //被操作人已经有视频
                var toUserID = $(this).parents(".meeting-content-right-item[user-id]").attr("user-id")
                if ($(this).hasClass("active")) {
                    //当前是可用状态,切换至禁用状态
                    IMUtils.fire("unableUserVedio", toUserID, {toUserID: toUserID})
                } else {
                    IMUtils.fire("enableUserVedio", toUserID, {toUserID: toUserID})
                }
            }
        })
    }

// 设置电子白板比例
/*
    function   videoCollapse(){
        $(".meeting-content-right").hide();
        $(".meeting-content-middle").removeClass("layui-col-md5").addClass("layui-col-md9");
        $(".open-video-right").show();
    };
    function  openVideoCollapse () {
        $(".meeting-content-middle").removeClass("layui-col-md9").addClass("layui-col-md5");
        $(".meeting-content-right").show();
        $(".open-video-right").hide();
    };

    function handleVideoPage() {
        //绑定专家列表收起事件
        VIDEOCOLLAPSEUtils.register("openVideoCollapse", function () {
            openVideoCollapse()
        })

        //绑定专家列表打开事件
        VIDEOCOLLAPSEUtils.register("closeVideoCollapse", function () {
            videoCollapse()
        })


        $(document).on("click", ".video-collapse", function (event) {
            //操作人是主讲人
            if (meetingConfig.isPresenter) {
                //当前是打开状态,切换至收起状态
                if ($(this).hasClass("video-collapse")) {
                    VIDEOCOLLAPSEUtils.fire("closeVideoCollapse")
                }
            }
        })
        $(document).on("click", ".open-video-collapse", function (event) {
            //操作人是主讲人
            if (meetingConfig.isPresenter) {
                if ($(this).hasClass("open-video-collapse")) {
                    //当前是收起状态,切换至打开状态
                    VIDEOCOLLAPSEUtils.fire("openVideoCollapse")
                }
            }
        })

    }
*/


    function addUserTolist(user) {
        var userIsAlreadyIn = false
        meetingConfig.meetingMembers.forEach(function (userItem) {
            if (userItem.userID == user.userID) {
                userIsAlreadyIn = true;
            }
        })

        if (!userIsAlreadyIn) {
            meetingConfig.meetingMembers.push(user)
            userInfoMap[user.userID] = user
        }

    }

    function handleUserAddMeeting() {
        //绑定用户进入房间事件
        IMUtils.register("userAddMeeting", function (data) {
            //被禁用方
            addUserTolist(data.user)
        }, function (data) {
            //发起方
        }, function (data) {
            //其他人
            addUserTolist(data.user)
        })
    }

    function showUserQuit(data) {
        //TODO
        console.log("用户退出：", data.msgContent)
        showSystemInfo(data.msgContent, data.time, false)
    }

    function handleUserQuitMeeting() {
        //绑定用户退出房间事件
        IMUtils.register("userQuitMeeting", function (data) {
            showUserQuit(data)
        }, function (data) {
            //发起方
            quitRTC()
            EventUtils.fire("destroyGroupOwnerCallback")
        }, function (data) {
            //其他人
            EventUtils.fire("destroyGroupOtherCallback")
        })
    }

    function addHistoryMsgTime() {
        var $msgItems = $(".meeting-chat-info");

        $msgItems.each(function (i, item) {
            var msgTime = parseInt($(item).attr("msg-time"))

            if (i == 0) {
                showMsgTime(msgTime, $(item), true)

            } else {
                var preMsgTime = parseInt($($msgItems.get(i - 1)).attr("msg-time"))

                if (msgTime - preMsgTime > 120) {
                    showMsgTime(msgTime, $(item), true)
                }

            }
        })

    }

    function HTMLDecode(text) {
        var temp = document.createElement("div");
        temp.innerHTML = text;
        var output = temp.innerText || temp.textContent;
        temp = null;
        return output;
    }

    //获取最新的群历史消息,用于切换群组聊天时，重新拉取群组的聊天消息
    function showHistoryMsg() {
        var options = {
            'GroupId': meetingConfig.imGroupID,
            'ReqMsgNumber': 20
        };

        if (getPrePageGroupHistroyMsgInfoMap[selToID] && getPrePageGroupHistroyMsgInfoMap[selToID]['ReqMsgSeq'] > 0) {
            options.ReqMsgSeq = getPrePageGroupHistroyMsgInfoMap[selToID]['ReqMsgSeq']
        }
        //拉取最新的群历史消息
        webim.syncGroupMsgs(
            options,
            function (msgList) {
                if (msgList.length == 0) {
                    webim.Log.warn("该群没有历史消息了:options=" + JSON.stringify(options));
                    return;
                }
                console.log("获取历史消息：", msgList)

                var msgSeq = msgList[0].seq - 1;
                getPrePageGroupHistroyMsgInfoMap[selToID] = {
                    "ReqMsgSeq": msgSeq
                };

                for (var j in msgList) { //遍历新消息
                    var newMsg = msgList[j];

                    if(newMsg.elems[0].content.data){
                        console.log("历史消息 data：", newMsg.elems[0].content.data)
                    }else if(newMsg.elems[0].content.text){
                        console.log("历史消息：text", newMsg.elems[0].content.text)
                       /* var msgObj = JSON.parse(HTMLDecode(newMsg.elems[0].content.text))
                        if(msgObj['toUserID'] && msgObj['msgType']){
                            convertMsgtoHtml(newMsg);
                            if( msgObj['toUserID'] == meetingConfig.userID ){

                                console.log("自定义消息类型--->"+msgObj['toUserID'], msgObj['msgType'], msgObj, "接收方执行")
                            }else if (msgObj['fromUserID'] != meetingConfig.userID){

                                console.log("自定义消息类型--->"+msgObj['toUserID'], msgObj['msgType'], msgObj, "其他人员执行")
                            }

                        }else{
                            console.log("自定义消息类型--->"+msgObj['toUserID'], msgObj['msgType'], msgObj, "其他人员执行")
                        }*/
                    }else if(newMsg.elems[0].content.opType){
                        console.log("历史消息：opType", newMsg.elems[0].content)
                    }else{
                        console.log("历史消息：other", newMsg.elems[0].content)
                    }
                }





                onMsgNotify(msgList.reverse(), true)

                if (msgSeq > 0) {
                    showHistoryMsg()
                } else {
                    //添加消息时间
                    addHistoryMsgTime()
                }
            },
            function (err) {
                console.log("拉取群历史消息失败：", err);
            }
        );
    }

    function onEditorKeydown() {
        //快捷发送事件
        $(window.frames['LAY_layedit_1'].document).find("body").keydown(function (event) {
            if (event.key == "Enter") {
                var quick = getQuickKey()

                if (!quick) {
                    return;
                }

                if (quick == QUICK_ENTER) {
                    //enter发送
                    if (!event.ctrlKey && !event.shiftKey) {
                        event.preventDefault();
                        sendMsg();
                    }

                } else if (quick == QUICK_CTRL_ENTER) {
                    // ctrl + enter
                    if (event.ctrlKey && !event.shiftKey) {
                        event.preventDefault();
                        sendMsg();
                    }
                }
            }
        })
    }

    window.quitRTC = quitRTC
    window.meetingConfig = meetingConfig
    window.loginInfo = loginInfo
    window.selType = selType
    window.callbackMap = callbackMap
    window.getPrePageGroupHistroyMsgInfoMap = {}
    window.onRemoteStreamUpdate = onRemoteStreamUpdate
    window.onRemoteStreamRemove = onRemoteStreamRemove
    window.fuckReduce = fuckReduce
    window.onEditorKeydown = onEditorKeydown
    window.resizeBoard = function (){
        teduBoard.resize();
    }

    // 字体名称选中事件
    $(".font-name-select").change(function () {
        var fontFamilyName = $(this).val();
        $(this).children().each(function (i, option) {
            if ($(option).text() == fontFamilyName) {
                var fontFamily = $(option).attr("font-class");
                localStorage.setItem("userFontFamily-" + meetingConfig.userID, fontFamily)
            }
        })
    })

    //字体大小选中事件
    $(".font-size-select").change(function () {
        var fontSize = $(this).val();
        localStorage.setItem("userFontSize-" + meetingConfig.userID, fontSize)
    })

    // 表情和字体相关事件
    $(".im-tools .iconfont").click(function (event) {
        event.preventDefault()
        event.stopPropagation()
        if ($(this).hasClass("face")) {
            //表情
            //alert("im-face-set")
            $(".im-face-set").toggleClass("active")

        } else if ($(this).hasClass("font")) {
            //字体
            $(".im-font-set").toggleClass("active")
        }

        $(this).toggleClass("active")

    })

    //表情选择事件
    $(document).on("click", "#emotionUL li img", function () {
        selectEmotionImg(this)
        $(".iconfont.face").click()
        // $("#msg-input").focus();
        var $edit = $(window.frames['LAY_layedit_1'].document).find("body")
        // $edit.focus()
    })

    //表情和字体隐藏事件
    $(document).click(function (event) {
        var $target = $(event.target);
        if ($target.parents(".im-font-set").length == 0 && $target.parents(".im-face-set").length == 0 && $target.parents(".im-tools").length == 0) {

            if ($(".im-face-set").hasClass("active")) {
                console.log(event)
                $(".im-face-set").removeClass("active")
                $(".iconfont.face.active").removeClass("active")
            }
            //
            // if ($(".im-font-set").hasClass("active")){
            //     console.log(event)
            //     $(".im-font-set").removeClass("active")
            //     $(".iconfont.font.active").removeClass("active")
            // }
        }

    })

    //快捷发送事件
    $("#msg-input").keydown(function (event) {
        if (event.key == "Enter") {
            var quick = getQuickKey()

            if (!quick) {
                return;
            }

            if (quick == QUICK_ENTER) {
                //enter发送
                if (!event.ctrlKey && !event.shiftKey) {
                    event.preventDefault();
                    sendMsg();
                }

            } else if (quick == QUICK_CTRL_ENTER) {
                // ctrl + enter
                if (event.ctrlKey && !event.shiftKey) {
                    event.preventDefault();
                    sendMsg();
                }
            }
        }
    })

    /*$(document).on('mousewheel DOMMouseScroll', onMouseScroll);
    function onMouseScroll(e){
        e.preventDefault();
        var wheel = e.originalEvent.wheelDelta || -e.originalEvent.detail;
        var delta = Math.max(-1, Math.min(1, wheel) );
        if(delta<0){//向下滚动
            alert("向下滚动")
        }else{//向上滚动
            alert("向下滚动")
        }
    }*/

    // 选择快捷发送的方式
    $(".quick-send-item").click(function (event) {
        if ($(this).hasClass("enter")) {
            var key = QUICK_ENTER
        } else {
            var key = QUICK_CTRL_ENTER

        }
        setQuickKey(key);
        showQuickKeySelect(key);

        setTimeout(function () {
            $(".quick-send-set").toggleClass("active");
        }, 800)

    })

    // 发消息按钮事件
    $(".btn-send-msg").click(function (event) {
        event.preventDefault()
        sendMsg()
    })

    // 弹出快捷发送方式选项
    $(".btn-select-quick-key").click(function (event) {
        event.preventDefault();
        event.stopPropagation();

        $(".quick-send-set").toggleClass("active");

    })

    // 窗口变化时，重新计算视频窗口的宽度和高度
    // resetVedioSize();
    $(window).resize(function () {
        // resetVedioSize();
    })

    //定时计算视频窗口上用户姓名和静音按钮的位置
    setInterval(fuckReduce, 500)

    //滚动条事件
    $("div").scroll(function () {
        fuckReduce()
    });

    //窗口关闭
    $(window).unload(function () {
        console.log("窗口关闭");
        quitRTC();
    });

    function initCameraView(){
        $("#btnCamMin").on("click", minCameraView);
        $("#btnCamMax").on("click",maxCameraView);
        $("#btnCamRestore2").on("click",restore2CameraView);
        $("#btnCamRestore").on("click",restoreCameraView);
    }
    
    function offCameraView(){
        $("#btnCamMin").off("click");
        $("#btnCamMax").off("click");
        $("#btnCamRestore2").off("click");
        $("#btnCamRestore").off("click");
    }
    
    function minCameraView(){
        $(".meeting-content-right").hide();
        $(".meeting-content-middle").removeClass("layui-col-md5").addClass("layui-col-md9");
        $("#divMain").addClass("cameraon");
        $("#cameraBar").show();     
    }
    function maxCameraView(){
        $("body").append($("#divCamera").removeClass().addClass("cameramax").remove());
        $("#btnCamMax").hide();
        $("#btnCamMin").hide();
        $("#btnCamRestore2").show();
        resetVedioSize();
        offCameraView();
        initCameraView();
    }
    
    function restore2CameraView(){
        $("#divMainRow").append($("#divCamera").removeClass().addClass("meeting-content-right layui-col-md4").remove());
        $("#btnCamMax").show();
        $("#btnCamMin").show();
        $("#btnCamRestore2").hide();
        resetVedioSize();
        offCameraView();
        initCameraView();   
    }
    
    function restoreCameraView(){
        $(".meeting-content-middle").removeClass("layui-col-md9").addClass("layui-col-md5");
        $(".meeting-content-right").show();
        $("#divMain").removeClass("cameraon");
        $("#cameraBar").hide();
        resetVedioSize();
    }
    

    var VedioFlagController = function(roomID) {
        this.disableList = [];

        this._init = function(roomID){
            if (!roomID) {
                throw "roomID 不能为空";
            }
            
            this.storageKey = "vedio_disable_flag_" + roomID;
            this.disableList = this._load();
            console.log('vedio flag init success, key: ' + this.storageKey);
        }

        this._load = function () {
            try {
                var disableList = localStorage.getItem(this.storageKey)
                if (!disableList || disableList == "[]") {
                    return [];
                } else {
                    return JSON.parse(disableList);
                }
            } catch (error){
                console.log(error)
                return [];
            }
            
        }

        this._save = function(){
            localStorage.setItem(this.storageKey, JSON.stringify(this.disableList));
        }

        this.add = function(userID){
            if (this.disableList.indexOf(userID) == -1) {
                this.disableList.push(userID);
            }
            this._save();
            return true;
        }

        this.remove = function(userID){
            if (this.disableList.indexOf(userID) > -1) {
                this.disableList.splice(this.disableList.indexOf(userID),1);
                this._save();
            }
            return true;
        }

        this.isDisable = function(userID) {
            return this.disableList.indexOf(userID) > -1;
        }

        this.clean = function(){
            localStorage.removeItem(this.storageKey)
            return true;
        }

        this._init(roomID);
    }


    var AudioFlagController = function(roomID) {
        this.disableList = [];

        this._init = function(roomID){
            if (!roomID) {
                throw "roomID 不能为空";
            }
            
            this.storageKey = "vudio_disable_flag_" + roomID;
            this.disableList = this._load();
            console.log('vedio flag init success, key: ' + this.storageKey);
        }

        this._load = function () {
            try {
                var disableList = localStorage.getItem(this.storageKey)
                if (!disableList || disableList == "[]") {
                    return [];
                } else {
                    return JSON.parse(disableList);
                }
            } catch (error){
                console.log(error)
                return [];
            }
            
        }

        this._save = function(){
            localStorage.setItem(this.storageKey, JSON.stringify(this.disableList));
        }

        this.add = function(userID){
            if (this.disableList.indexOf(userID) == -1) {
                this.disableList.push(userID);
            }
            this._save();
            return true;
        }

        this.remove = function(userID){
            if (this.disableList.indexOf(userID) > -1) {
                this.disableList.splice(this.disableList.indexOf(userID),1);
                this._save();
            }
            return true;
        }

        this.isDisable = function(userID) {
            return this.disableList.indexOf(userID) > -1;
        }

        this.clean = function(){
            localStorage.removeItem(this.storageKey)
            return true;
        }

        this._init(roomID);
    }



    $(function () {
        initCameraView();
        WebRTCAPI.fn.detectRTC({
            screenshare: true // 是否进行屏幕分享检测，默认true
        }, function (info) {
            if (!info.support) {
                layui.msg('不支持WebRTC');
            } else {
                //从主框架接口获取当前会诊信息
                getMeetingInfo(function () {
                    //初始化会诊信息、用户信息
                    showMeetingMemberVedio();
                    //绑定语音控制事件
                    handleUserAudio();
                    //绑定用户参会事件
                    handleUserAddMeeting();
                    //绑定用户退出房间事件
                    handleUserQuitMeeting();
                    //绑定专家列表事件
                    //handleVideoPage();
                    //初始化发送快捷键选项
                    initQuickKey();
                    //获取用户签名
                    getUserToken(function (userToken) {
                        //获取会诊对应房间号
                        genRoomID(function (roomID) {
                            console.log("existImGroupID==================>", meetingConfig.existImGroupID);
                            if (!meetingConfig.existImGroupID) {
                                if (!meetingConfig.isPresenter) {
                                    if (layer) {
                                        layer.msg("非主持人不可创建群组");
                                    } else {
                                        console.log("非主持人不可创建群组");
                                    }
                                    window.BoardInited=true;
                                    return;
                                }
                            }

                            var vedioFlagController = new VedioFlagController(meetingConfig.imGroupID);
                            window.vedioFlagController = vedioFlagController;

                            var audioFlagController = new AudioFlagController(meetingConfig.imGroupID);
                            window.audioFlagController = audioFlagController;

                            //初始化TIC接口
                            try
                            {                               
                                initTIC(meetingConfig);
                            } catch (ex){                               
                            }
                            window.BoardInited=true;
                        })
                    })
                })
            }
        })

    })
})();
