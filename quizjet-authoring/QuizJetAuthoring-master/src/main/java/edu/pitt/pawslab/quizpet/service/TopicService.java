package edu.pitt.pawslab.quizpet.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.pitt.pawslab.quizpet.database.WebexDatabase;
import edu.pitt.pawslab.quizpet.instance.ServerMessage;
import edu.pitt.pawslab.quizpet.instance.Setting;
import edu.pitt.pawslab.quizpet.instance.SiteUser;
import edu.pitt.pawslab.quizpet.instance.Topic;

@Service
public class TopicService {

	@Autowired
	private WebexDatabase webexDatabase;
	@Autowired
	private UserService userService;
	
	/*
	 * this method returns all the python topics
	 */
	public HashMap<Integer, StringBuilder> getAllTopics(){
		System.out.println("Get all topic.....");
		System.out.println(Setting.JAVACLASSFOLDER);
		SiteUser curr = userService.getCurrentUser();
		return webexDatabase.getAllTopics();
	}
	
	/*
	 * this method returns all the python topics
	 */
	public HashMap<Integer, StringBuilder> getUserAllTopics(){
		System.out.println("Get all topic.....");
		System.out.println(Setting.JAVACLASSFOLDER);
		SiteUser curr = userService.getCurrentUser();
		return webexDatabase.getUserAllTopics(curr.getId());
	}
	
	
	/*
	 * this method updates the relationship between one topic and one quiz
	 */
	public ServerMessage updateQuizTopicRel(Integer quizId, Integer topicId){
		ServerMessage serverMessage = new ServerMessage();
		//if there is already a relationship, delete it
		if(webexDatabase.ifQuizTopicRelExists(quizId) > 0){
			webexDatabase.removeQuizTopicRelation(quizId);
		}
		//add the new relationship
		if(webexDatabase.createQuizTopicRelation(quizId, topicId) > 0){
			serverMessage.setSuccess(true);
			serverMessage.setMessage(new StringBuilder("Quiz added to its topic."));
		}else{
			serverMessage.setSuccess(false);
			serverMessage.setMessage(new StringBuilder("Adding quiz failed."));
		}
		return serverMessage;
	}
	
	/*
	 * this method creates a new topic
	 */
	public ServerMessage newTopic(Topic newTopic){
		ServerMessage serverMessage = new ServerMessage();
		userService.addAuthorInfoToTopic(newTopic);
		if(webexDatabase.newTopic(newTopic).getTopicId() > 0){
			serverMessage.setSuccess(true);
			serverMessage.setMessage(new StringBuilder("Topic added."));
			serverMessage.setContent(new Object[]{newTopic});
		}else{
			serverMessage.setSuccess(false);
			serverMessage.setMessage(new StringBuilder("Insert topic failed."));
		}
		return serverMessage;
	}
	
	/*
	 * this method updates one topic
	 */
	public ServerMessage updateTopic(Topic newTopic){
		ServerMessage serverMessage = new ServerMessage();
		if(webexDatabase.updateTopic(newTopic) > 0){
			serverMessage.setSuccess(true);
			serverMessage.setMessage(new StringBuilder("Topic updated."));
		}else{
			serverMessage.setSuccess(false);
			serverMessage.setMessage(new StringBuilder("Update topic failed."));
		}
		return serverMessage;
	}
	
	/*
	 * this method returns a list of topics of one user
	 */
	public ArrayList<Topic> getTopicsByAuthor(int authorId){
		SiteUser curr = userService.getCurrentUser();
		return new ArrayList<Topic>(webexDatabase.getTopicsByAuthorId(curr.getId()));
	}
	
	/*
	 * this method returns one topic by its id
	 */
	public Topic getTopicById(Integer topicId){
		return webexDatabase.getTopicById(topicId);
	}
}
