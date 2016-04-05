package edu.pitt.pawslab.quizpet.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import edu.pitt.pawslab.quizpet.database.WebexDatabase;
import edu.pitt.pawslab.quizpet.instance.Quiz;
import edu.pitt.pawslab.quizpet.instance.Topic;
import edu.pitt.pawslab.quizpet.instance.SiteUser;

import edu.pitt.pawslab.quizpet.service.PasswordHashService;

@Service
public class UserService {
	
	@Autowired
	private WebexDatabase webexDatabase;
	
	/*
	 * this method checks if one username exists
	 */
	public boolean ifValidUsername(String username){
		//return true;
		return webexDatabase.checkUsername(username) > 0 ? true : false;
	}
	
	/*
	 * this method returns one user object with given user login
	 */
	public SiteUser getUserByLogin(StringBuilder login){
		SiteUser targetUser = webexDatabase.getUserByLogin(login);
		targetUser.setLogin(login);
		return targetUser;
	}
	
	/*
	 * this method checks if one user's login attempt is valid
	 */
	public boolean ifValidAuthentication(String username, String password){
		String passwordEncrypted = webexDatabase.getUserPassword(username);
		try {
			return PasswordHashService.validatePassword(password, passwordEncrypted);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public SiteUser getCurrentUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String name = auth.getName();
	    return getUserByLogin(new StringBuilder(name));
	}

	public void addAuthorInfoToQuiz(final Quiz quiz){
		SiteUser curr = getCurrentUser();
		quiz.setAuthorId(curr.getId());
		quiz.setGroupId(1);
	}
	
	public void addAuthorInfoToTopic(final Topic topic){
		SiteUser curr = getCurrentUser();
		topic.setAuthorId(curr.getId());
		topic.setGroupId(1);
	}
}
