package com.va.removeconsult.wxapi.utils;

import java.io.Serializable;

public class ResultBean implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer status;

    private String msg;

    private Object data;

    public ResultBean() {
    }

    public ResultBean(Integer status) {
        this.status = status;
    }

    public ResultBean(Object data) {
        this.data = data;
    }

    public  ResultBean(Integer status, Object data) {
        this.status = status;
        this.data = data;
    }

    public ResultBean(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResultBean(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}
