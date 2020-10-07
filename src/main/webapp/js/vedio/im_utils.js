;(function (window) {
    var IMUtils = {
        __data : {}
        , register : function(msgType, toUserCallback, fromUserCallback, otherUserCallback){
            if(msgType in this.__data){

                if(toUserCallback){
                    this.__data[msgType]['to'].push(toUserCallback);
                }

                if(fromUserCallback){
                    this.__data[msgType]['from'].push(fromUserCallback);
                }

                if (otherUserCallback){
                    this.__data[msgType]['other'].put(otherUserCallback)
                }

            }else{
                var callBackObj = {

                }

                if(toUserCallback){
                    callBackObj['to'] = [toUserCallback];
                }else{
                    callBackObj['to'] = [];
                }

                if(fromUserCallback){
                    callBackObj['from'] = [fromUserCallback];
                }else{
                    callBackObj['from'] = [];
                }

                if (otherUserCallback){
                    callBackObj['other'] = [otherUserCallback];
                }else{
                    callBackObj['other'] = [];
                }
                this.__data[msgType] = callBackObj;
            }
        }, fire : function(msgType, toUserID, msgData){
            if(!(msgType in this.__data)){
                console.error("自定义消息类型未注册:", msgType);
                return;
            };

            var msgContent = {
                msgType     :   msgType,
                toUserID    :   toUserID,
                fromUserID  :   meetingConfig.userID,
                msgData     :   msgData
            };

            callbackList = this.__data[msgType]['from'];

            onSendMsg(msgContent, callbackList);
        }
    }

    window.IMUtils = IMUtils;
})(window);

;(function (window) {
    var EventUtils = {
        __data : {}
        , register : function(eventType, callback){
            if (!callback){
                console.error("callback must ", callback)
                return;
            }

            if(eventType in this.__data){

                if(callback){
                    this.__data[eventType].push(callback);
                }

            }else{

                this.__data[eventType] = [callback];
            }

        },
        fire : function(eventType){
            if(!(eventType in this.__data)){
                console.error("自定义事件类型未注册:", eventType);
                console.log(this.__data)
                return;
            };

            var argsArr = []
            for(var i in arguments){
                if(i > 0){
                    argsArr.push(arguments[i])
                }
            }

            this.__data[eventType].forEach(function(callback){
                invokeFunc(callback, argsArr)
            })


        }
        //切换主讲人
        , onChangePresenter: function(orgPresenterCallback, newPresenterCallback){
            this.register("changeGroupOwnerOrgPresenterCallback", orgPresenterCallback)
            this.register("changeGroupOwnerNewPresenterCallback", newPresenterCallback)
        }

        //退出会诊
        ,onQuitMeeting : function (presenterCallback, otherCallback) {
            this.register("destroyGroupOwnerCallback", presenterCallback)
            this.register("destroyGroupOtherCallback", otherCallback)
        }


    }

    window.EventUtils = EventUtils;
})(window);
