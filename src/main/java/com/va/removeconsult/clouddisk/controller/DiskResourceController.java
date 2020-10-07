package com.va.removeconsult.clouddisk.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.va.removeconsult.clouddisk.service.ResourceService;


@Controller
@RequestMapping("/diskResourceController")
public class DiskResourceController {

	@Resource
	private ResourceService rs;
	
	
	@RequestMapping("/getResource.do")
	public void getResource(HttpServletRequest request, HttpServletResponse response) {
		rs.getResource(request, response);
	}
	
	
	@RequestMapping("/getWordView/{fileId}")
	public void getWordView(@PathVariable("fileId") String fileId,HttpServletRequest request,HttpServletResponse response) {
		rs.getWordView(fileId,request, response);
	}
	
	
	@RequestMapping("/getTxtView/{fileId}")
	public void getTxtView(@PathVariable("fileId") String fileId,HttpServletRequest request,HttpServletResponse response) {
		rs.getTxtView(fileId, request, response);
	}
	
	
	@RequestMapping("/getPdfView/{fileId}")
	public void getPdfView(@PathVariable("fileId") String fileId,HttpServletRequest request,HttpServletResponse response) {
		rs.getPdfView(fileId, request, response);
	}
	
	
	@RequestMapping("/getVideoTranscodeStatus.ajax")
	public @ResponseBody String getVideoTranscodeStatus(HttpServletRequest request) {
		return rs.getVideoTranscodeStatus(request);
	}

}
