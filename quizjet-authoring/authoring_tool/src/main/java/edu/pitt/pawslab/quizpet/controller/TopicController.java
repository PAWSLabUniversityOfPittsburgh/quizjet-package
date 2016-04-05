package edu.pitt.pawslab.quizpet.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.pitt.pawslab.quizpet.instance.Quiz;
import edu.pitt.pawslab.quizpet.instance.ServerMessage;
import edu.pitt.pawslab.quizpet.instance.Topic;
import edu.pitt.pawslab.quizpet.service.TopicService;

@Controller
@RequestMapping(value = "/topic")
public class TopicController {
	
	private static final Logger logger = LoggerFactory.getLogger(TopicController.class);
	private static Locale locale = new Locale("en");
	
	@Autowired
	private TopicService topicService;
	
	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public @ResponseBody HashMap<Integer, StringBuilder> getAllTopics(){
		logger.info("requesting all the topics", locale);
		return topicService.getAllTopics();
	}
	@RequestMapping(value = "/getUserAll", method = RequestMethod.GET)
	public @ResponseBody HashMap<Integer, StringBuilder> getUserAllTopics(){
		logger.info("requesting all the topics", locale);
		return topicService.getUserAllTopics();
	}
	
	
	@RequestMapping(value = "/linkQuiz", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage linkQuiz(@RequestBody Quiz quiz){
		logger.info("adding quiz id " + quiz.getQuizId() + " to topic id " + quiz.getTopicId(), locale);
		return topicService.updateQuizTopicRel(quiz.getQuizId(), quiz.getTopicId());
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage newTopic(@RequestBody Topic topic){
		logger.info("adding new topic ", locale);
		return topicService.newTopic(topic);
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage updateTopic(@RequestBody Topic topic){
		logger.info("updating topic id " + topic.getTopicId(), locale);
		return topicService.updateTopic(topic);
	}
	
	@RequestMapping(value = "/myTopics", method = RequestMethod.GET)
	public @ResponseBody ArrayList<Topic> getTopicsByAuthor(){
		logger.info("getting topics related to urser id: 60", locale);
		return topicService.getTopicsByAuthor(60);
	}
	
	@RequestMapping(value = "/id/{topicId}", method = RequestMethod.GET)
	public @ResponseBody Topic getTopicsById(@PathVariable Integer topicId){
		logger.info("getting topic id: " + topicId, locale);
		return topicService.getTopicById(topicId);
	}
}
