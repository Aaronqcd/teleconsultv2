package com.va.removeconsult.controller.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelpController {
	@RequestMapping(value = "/help", method = RequestMethod.GET)
	public ModelAndView index() {
		ModelAndView result = new ModelAndView("/help/index");
		return result;
	}
}
