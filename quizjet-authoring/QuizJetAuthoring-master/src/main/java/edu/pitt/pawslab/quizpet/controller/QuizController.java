package edu.pitt.pawslab.quizpet.controller;

import java.util.ArrayList;
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
import edu.pitt.pawslab.quizpet.instance.Setting;
import edu.pitt.pawslab.quizpet.service.ConceptService;
import edu.pitt.pawslab.quizpet.service.QuizService;

@Controller
@RequestMapping(value = "/quiz")
public class QuizController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
	private static Locale locale = new Locale("en");
	
	@Autowired
	private QuizService quizService;
	@Autowired
	private ConceptService conceptService;
	
	@RequestMapping(value = "/isRdfIdAvailable/{rdfId}", method = RequestMethod.GET)
	public @ResponseBody ServerMessage getDomainList(@PathVariable String rdfId){
		logger.info("requesting the count for rdfid: " + rdfId, locale);
		return quizService.isRdfIdAvailable(new StringBuilder(rdfId));
	}
	
	@RequestMapping(value = "/search/{keyword}", method = RequestMethod.GET)
	public @ResponseBody ArrayList<Quiz> titleBlurSearch(@PathVariable String keyword){
		logger.info("searching for python questions related to: " + keyword, locale);
		return quizService.titleBlurSearch(new StringBuilder(keyword));
	}
	
	@RequestMapping(value = "/id/{quizId}", method = RequestMethod.GET)
	public @ResponseBody ServerMessage getQuizById(@PathVariable Integer quizId){
		logger.info("requesting quiz has id: " + quizId, locale);
		return quizService.getQuizById(quizId);
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage newQuiz(@RequestBody Quiz quiz){
		logger.info("adding new quiz to the database.", locale);
		return quizService.newQuiz(quiz);
	}
	
	@RequestMapping(value = "/searchbytopic/{keyword}", method = RequestMethod.GET)
	public @ResponseBody ArrayList<Quiz> quizSearchByTopic(@PathVariable String keyword){
		logger.info("searching for python questions by topic related to: " + keyword, locale);
		return quizService.quizSearchByTopic(new StringBuilder(keyword));
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage updateQuiz(@RequestBody Quiz quiz){
		logger.info("updating quiz, its id: " + quiz.getQuizId(), locale);
		return quizService.updateQuiz(quiz);
	}
	
	@RequestMapping(value = "/linkClasses", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage linkClasses(@RequestBody Quiz quiz){
		logger.info("linking external classes to this quiz, its id: " + quiz.getQuizId(), locale);
		return quizService.updateQuizClassRel(quiz.getQuizId(), quiz.getLinkedClasses());
	}
	
	@RequestMapping(value = "/injectConcept/{rdfId}", method = RequestMethod.GET)
	public @ResponseBody ServerMessage injectConcept(@PathVariable String rdfId) throws Exception{
		logger.info("injecting concepts for quiz id: " + rdfId, locale);
		System.out.println("update concept.......");
		if (rdfId.startsWith(Setting.QJAVAPREFIX)){
		return conceptService.injectConceptForOneQuiz(rdfId);}
		else return conceptService.injectConceptForOneQuiz(Setting.QJAVAPREFIX+"_"+rdfId);
	}
}
