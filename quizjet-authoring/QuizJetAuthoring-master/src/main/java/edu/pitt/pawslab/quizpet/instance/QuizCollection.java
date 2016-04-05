package edu.pitt.pawslab.quizpet.instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class QuizCollection {
	private HashSet<Quiz> collection;
	
	public QuizCollection(ArrayList<Quiz> collection) {
		this.collection = new HashSet<Quiz>(collection);
	}
	
	public LinkedHashMap<String, Quiz> getUniqueCollection(){
		LinkedHashMap<String, Quiz> rs = new LinkedHashMap<String, Quiz>();

		Iterator<Quiz> iterator = this.collection.iterator();
		while(iterator.hasNext()){
			Quiz currQuiz = iterator.next();
			if(rs.containsKey(currQuiz.getRdfId().toString())){
				Quiz origin = rs.get(currQuiz.getRdfId().toString());
				if(origin.getVersion() < currQuiz.getVersion()){
					rs.remove(origin.getRdfId());
					rs.put(currQuiz.getRdfId().toString(), currQuiz);
				}
			}else{
				rs.put(currQuiz.getRdfId().toString(), currQuiz);
			}
		}

		return rs;
	}

}
