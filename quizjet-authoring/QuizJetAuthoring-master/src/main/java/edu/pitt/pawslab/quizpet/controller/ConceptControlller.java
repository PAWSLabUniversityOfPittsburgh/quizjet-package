package edu.pitt.pawslab.quizpet.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import edu.pitt.pawslab.quizpet.instance.ConceptNode;
import edu.pitt.pawslab.quizpet.instance.Quiz;
import edu.pitt.pawslab.quizpet.instance.ServerMessage;
import edu.pitt.pawslab.quizpet.instance.Setting;
import edu.pitt.pawslab.quizpet.service.ConceptService;
import edu.pitt.pawslab.quizpet.service.QuizService;

@Controller
@RequestMapping(value = "/concept")
public class ConceptControlller {
	
	@Autowired
	private ConceptService conceptService;
	@Autowired
	private QuizService quizService;
	
	private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
	private static Locale locale = new Locale("en");

	@RequestMapping(value = "/get/{quizId}", method = RequestMethod.GET)
	public @ResponseBody HashMap<String, Object[]> getConceptPackgeForOneQuiz(@PathVariable Integer quizId){
		logger.info("getting concepts of quiz id " + quizId, locale);
		HashMap<String, Object[]> rs = new HashMap<String, Object[]>();
		ServerMessage quiz = quizService.getQuizById(quizId);
		if(quiz.isSuccess()){
			Quiz currQuiz = (Quiz) quiz.getContent()[0];
			return conceptService.getConceptsAndCodeOfOneQuiz(currQuiz);
		}else{
			return rs;
		}
	}
	
	
	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public @ResponseBody List<String> getAllOntology(){
		logger.info("requesting all the ontology", locale);
		return conceptService.getAllOntology();
	}
	
	@RequestMapping(value = "/update/{quizId}", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage updateConceptForOneQuiz(@PathVariable Integer quizId, @RequestBody ArrayList<ConceptNode> newConcepts){
		System.out.println("update concept.......");
		ServerMessage serverMessage = new ServerMessage();
		logger.info("updating concepts of quiz id " + quizId, locale);
		ServerMessage quiz = quizService.getQuizById(quizId);
		if(quiz.isSuccess()){
			Quiz currQuiz = (Quiz) quiz.getContent()[0];
			Integer rows = conceptService.updateConceptsOfOneQuiz(currQuiz, newConcepts);
			serverMessage.setSuccess(true);
			serverMessage.setMessage(new StringBuilder("Concepts updated."));
		}else{
			serverMessage.setSuccess(false);
			serverMessage.setMessage(new StringBuilder("No such quiz."));
		}
		
		return serverMessage;
	}
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage deleteConceptForOneQuiz(@PathVariable Integer id){
		System.out.println("delete concept.......");
		ServerMessage serverMessage = new ServerMessage();
			Integer rows = conceptService.deleteConceptsOfOneQuiz(id);
			if(rows>=1){
			serverMessage.setSuccess(true);
			serverMessage.setMessage(new StringBuilder("Concept deleted."));
			}
			else {
				serverMessage.setSuccess(false);
				serverMessage.setMessage(new StringBuilder("Concept deleted fail."));	
			}
		
		return serverMessage;
	}
	
	
	
	@RequestMapping(value = "/deletebyrdf/{rdfId}", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage deleteConceptByRdfid(@PathVariable String rdfId){
		System.out.println("delete all concept.......");
		if (!rdfId.startsWith(Setting.QJAVAPREFIX)){
			rdfId = Setting.QJAVAPREFIX+"_"+rdfId;
		}
		ServerMessage serverMessage = new ServerMessage();
			Integer rows = conceptService.deleteConceptByRdfid(rdfId);
			if(rows>=1){
			serverMessage.setSuccess(true);
			serverMessage.setMessage(new StringBuilder("All concepts deleted."));
			}
			else {
				serverMessage.setSuccess(false);
				serverMessage.setMessage(new StringBuilder("Concepts deleted fail."));	
			}
		
		return serverMessage;
	}
	
	
	@RequestMapping(value = "/add/{id}", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage addConcept(@PathVariable String id, @RequestBody ConceptNode cp){
		logger.info("adding new quiz to the database.", locale);
		ServerMessage serverMessage = new ServerMessage();
		System.out.println(cp.getTitle().toString());
		int i =  conceptService.addConcept(id,cp);
		serverMessage.setSuccess(true);
		serverMessage.setContent(new Object[]{i});
		return serverMessage;
	}
	@RequestMapping(value = "/addMul/{id}", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage addConceptMul(@PathVariable String id, @RequestBody String cps){
		logger.info("adding new quiz to the database.", locale);
		ServerMessage serverMessage = new ServerMessage();
		System.out.println(cps);
		HashMap<String,Integer> re=  conceptService.addConceptMul(id,cps);
		serverMessage.setSuccess(true);
		serverMessage.setContent(new Object[]{re});
		return serverMessage;
	}
	
	
	@RequestMapping(value = "/addline/{id}", method = RequestMethod.PUT)
	public @ResponseBody ServerMessage addConceptLine(@PathVariable String id, @RequestBody String line){
		logger.info("edit line to the database.", locale);
		ServerMessage serverMessage = new ServerMessage();
		System.out.println(line);
		System.out.println(id);
		int i =  conceptService.updateConceptLine(id,line);
		serverMessage.setSuccess(true);
		serverMessage.setContent(new Object[]{i});
		return serverMessage;
	}
}
