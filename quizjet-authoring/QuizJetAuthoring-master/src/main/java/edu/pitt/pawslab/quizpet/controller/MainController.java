package edu.pitt.pawslab.quizpet.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	
	//for login page
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout){
		ModelAndView model = new ModelAndView();
		
		if(error != null)
			model.addObject("error", "Invalid username and password!");
		if(logout != null)
			model.addObject("msg", "You've been logged out successfully.");
		model.setViewName("login");
		return model;
	}

	//for the main portal page
	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public ModelAndView defaultPage(){
		ModelAndView model = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addObject("username", auth.getName());
		model.setViewName("home");
		return model;
	}
}
