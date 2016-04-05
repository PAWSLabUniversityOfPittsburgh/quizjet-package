package edu.pitt.pawslab.quizpet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.pitt.pawslab.quizpet.instance.SiteUser;
import edu.pitt.pawslab.quizpet.service.UserService;

@Controller
@RequestMapping(value = "/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value= "/me", method = RequestMethod.GET)
	public @ResponseBody SiteUser getDomainList(){
		logger.info("requesting user info for current user");
		return userService.getCurrentUser();
	}
	
}
