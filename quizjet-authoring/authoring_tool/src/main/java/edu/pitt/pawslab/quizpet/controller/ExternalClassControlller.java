package edu.pitt.pawslab.quizpet.controller;

import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import edu.pitt.pawslab.quizpet.instance.ServerMessage;
import edu.pitt.pawslab.quizpet.service.ExternalClassService;

@Controller
@RequestMapping(value = "/class")
public class ExternalClassControlller {
	
	@Autowired
	private ExternalClassService externalClassService;
	
	private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
	private static Locale locale = new Locale("en");
	
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody HashMap<Integer, StringBuilder> getDomainList(){
		logger.info("requesting all the external classes", locale);
		return externalClassService.getAllClasses();
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public @ResponseBody ServerMessage uploadClassFile(@RequestParam("file") MultipartFile file){
		return externalClassService.uploadClass(file);
	}
	
	@RequestMapping(value = "/get/{classId}", method = RequestMethod.GET)
	public @ResponseBody ServerMessage getClassFileById(@PathVariable Integer classId){
		return externalClassService.getClassById(classId);
	}
}
