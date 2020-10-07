package com.va.removeconsult.websocket.remote;

import org.springframework.web.socket.TextMessage;

/**
 * @author yefei
 * @date: 2020/7/5
 */

public class RemoteMessageRequest {
    private String toUserCode;
    String message;

    public String getToUserCode() {
        return toUserCode;
    }

    public void setToUserCode(String toUserCode) {
        this.toUserCode = toUserCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
