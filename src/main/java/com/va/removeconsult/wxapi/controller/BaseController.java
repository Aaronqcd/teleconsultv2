
package com.va.removeconsult.wxapi.controller;

import com.va.removeconsult.wxapi.utils.ResultBean;

public class BaseController {

	
	public static final Integer STATUS_SUCCESS=200;
	
	public static final Integer STATUS_ERROR=500;
	
	public static final String MSG_SUCCESS="操作成功";
	
	public static final String MSG_ERROR="操作异常";
	
    public static final String OK = "ok";

    public static final String BAD_REQUEST = "Bad Request";

    public static final String NOT_FOUND = "Not Found";

    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    public static final String WRONG_FORMAT = "WRONG_FORMAT";


    private  ResultBean handleResultBean(ResultBean resultBean) {
        resultBean.setMsg(resultBean.getMsg());
        return resultBean;
    }

    @SuppressWarnings("unused")
	private  ResultBean handleResultBean(ResultBean resultBean, Object value) {
        if (value instanceof String) {
            resultBean.setMsg(String.format(resultBean.getMsg(), value));
            return resultBean;
        } else {
            return handleResultBean(resultBean);
        }
    }
    
    /**
     * 返回失败
     * @return
     */
    protected ResultBean errorResult() {
        return handleResultBean(new ResultBean(STATUS_ERROR,MSG_ERROR));
    }

    protected ResultBean errorResult(String msg) {
        return handleResultBean(new ResultBean(STATUS_ERROR,msg));
    }

    protected ResultBean errorResult(Integer status, String msg) {
        return handleResultBean(new ResultBean(status, msg));
    }

    protected  ResultBean errorResult(Integer status, String msg, Object data) {
        return handleResultBean(new ResultBean(status, msg, data));
    }
    
    
    /**
     * 返回成功的
     * @return
     */
    protected  ResultBean successResult() {
        return handleResultBean(new ResultBean(STATUS_SUCCESS, MSG_SUCCESS));
    }

    protected ResultBean successResult(Integer status, String msg) {
        return handleResultBean(new ResultBean(status, msg));
    }

    protected  ResultBean successResult(Object data) {
        return handleResultBean(new ResultBean(STATUS_SUCCESS, MSG_SUCCESS,data));
    }
    
    protected  ResultBean successResult(Integer status, String msg, Object data) {
        return handleResultBean(new ResultBean(status, msg, data));
    }

}
