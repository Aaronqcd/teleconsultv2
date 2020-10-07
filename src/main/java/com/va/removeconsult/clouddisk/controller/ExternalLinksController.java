package com.va.removeconsult.clouddisk.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.va.removeconsult.clouddisk.service.ExternalDownloadService;


@Controller
@RequestMapping({ "/externalLinksController" })
public class ExternalLinksController {

	@Resource
	private ExternalDownloadService eds;

	@RequestMapping("/getDownloadKey.ajax")
	public @ResponseBody String getDownloadKey(HttpServletRequest request) {
		return eds.getDownloadKey(request);
	}

	@RequestMapping("/downloadFileByKey/{fileName}")
	public void downloadFileByKey(HttpServletRequest request, HttpServletResponse response) {
		eds.downloadFileByKey(request, response);
	}

}
