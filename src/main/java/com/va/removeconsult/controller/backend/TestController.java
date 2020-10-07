package com.va.removeconsult.controller.backend;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/test")
public class TestController {

	@RequestMapping("/fileupload")
	public ModelAndView upload(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/test/fileupload");
		return mv;
	}

	@RequestMapping("/screenshare")
	public ModelAndView testScreenShare(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/test/screenshare");
		return mv;
	}
}