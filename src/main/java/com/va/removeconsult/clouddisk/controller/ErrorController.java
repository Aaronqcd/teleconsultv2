package com.va.removeconsult.clouddisk.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import com.va.removeconsult.clouddisk.util.FileBlockUtil;
import com.va.removeconsult.clouddisk.util.LogUtil;
import com.va.removeconsult.clouddisk.util.Printer;

@ControllerAdvice
@RequestMapping({ "/errorController" })
public class ErrorController {
	@Resource
	private FileBlockUtil fbu;
	@Resource
	private LogUtil lu;

	@RequestMapping({ "/pageNotFound.do" })
	public String handleError(final HttpServletRequest request, final HttpServletResponse response) {
		return response.encodeURL("/prv/error.html");
	}

	@ExceptionHandler({ Exception.class })
	public void handleException(final Exception e) {
		this.lu.writeException(e);
		e.printStackTrace();
		this.fbu.checkFileBlocks();
		System.out.println("\u5904\u7406\u8bf7\u6c42\u65f6\u53d1\u751f\u9519\u8bef\uff1a\n\r------\u4fe1\u606f------\n\r"
						+ e.getMessage() + "\n\r------\u4fe1\u606f------");
	}
}
