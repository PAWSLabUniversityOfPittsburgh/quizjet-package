package edu.pitt.pawslab.quizpet.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.pitt.pawslab.quizpet.database.WebexDatabase;
import edu.pitt.pawslab.quizpet.instance.JavaSourceFromString;
import edu.pitt.pawslab.quizpet.instance.Quiz;
import edu.pitt.pawslab.quizpet.instance.QuizCollection;
import edu.pitt.pawslab.quizpet.instance.ServerMessage;
import edu.pitt.pawslab.quizpet.instance.Setting;
import edu.pitt.pawslab.quizpet.instance.SiteUser;

@Service
public class QuizService {
	
	@Autowired
	private WebexDatabase webexDatabase;
	@Autowired
	private ExternalClassService externalClassService;
	@Autowired
	private UserService userService;
	
	/*
	 * this method returns if this rdfid is available
	 */
	public ServerMessage isRdfIdAvailable(StringBuilder rdfId){
		ServerMessage result = new ServerMessage();
		Integer idCount = webexDatabase.rdfIdCount(rdfId);
		if(idCount == 0){
			result.setSuccess(true);
			result.setMessage(new StringBuilder("This rdfid is usable"));
		}else{
			result.setSuccess(false);
			result.setMessage(new StringBuilder("This rdfid is already used."));
		}
		return result;
	}
	
	/*
	 * this method returns python questions search result
	 */
	public ArrayList<Quiz> titleBlurSearch(StringBuilder keyword){
		SiteUser cUser = userService.getCurrentUser();		
		//QuizCollection quizCollection = new QuizCollection(webexDatabase.blurSearch(keyword, cUser.getId()));
		QuizCollection quizCollection = new QuizCollection(webexDatabase.blurSearch(keyword, cUser.getId()));
		return new ArrayList<Quiz>(quizCollection.getUniqueCollection().values());
	}
	/*
	 * this method returns python questions search result
	 */
	public ArrayList<Quiz> quizSearchByTopic(StringBuilder keyword){
		SiteUser cUser = userService.getCurrentUser();		
		//QuizCollection quizCollection = new QuizCollection(webexDatabase.blurSearch(keyword, cUser.getId()));
		//QuizCollection quizCollection = new QuizCollection(webexDatabase.topicSearch(keyword, cUser.getId()));
		//return new ArrayList<Quiz>(quizCollection.getUniqueCollection().values());
		return new ArrayList<Quiz>(webexDatabase.topicSearch(keyword, cUser.getId()));
	}
	
	
	/*
	 * this method returns one python question according to its id
	 */
	public ServerMessage getQuizById(Integer quizId){
		ServerMessage serverMessage = new ServerMessage();
		if(webexDatabase.ifQuizIdExists(quizId) > 0){
			Quiz rs = webexDatabase.getQuizById(quizId);
			//add linked classes information
			rs.setLinkedClasses(webexDatabase.getClassListByQuizId(quizId));
			//add topic information
			rs.setTopicId(webexDatabase.getTopicIdByQuiz(quizId));
			
			serverMessage.setSuccess(true);
			serverMessage.setContent(new Object[]{rs});
		}else{
			serverMessage.setSuccess(false);
			serverMessage.setMessage(new StringBuilder("No such quiz id."));
		}
		return serverMessage;
	}
	
	/*
	 * this method adds a new quiz to the database
	 */
	public ServerMessage newQuiz(Quiz quiz){
		ServerMessage serverMessage = new ServerMessage();
		ServerMessage isRdfidAvailable = this.isRdfIdAvailable(quiz.getRdfId());
		ServerMessage isCompile = this.checkCodeSyntax(quiz);
		if(isCompile.isSuccess() && isRdfidAvailable.isSuccess()){
			userService.addAuthorInfoToQuiz(quiz);
			//the rdfid is usable
			
			Quiz quizInDb = webexDatabase.newQuiz(quiz);
			serverMessage.setSuccess(true);
			serverMessage.setMessage(new StringBuilder("Quiz Created."));
			serverMessage.setContent(new Object[]{quizInDb});
			return serverMessage;
		}
		if (!isCompile.isSuccess()) return isCompile;
		else return isRdfidAvailable;
	}
	
	/*
	 * this method adds a new quiz to the database
	 */
	public ServerMessage checkCodeSyntax(Quiz quiz){
		ServerMessage result = new ServerMessage();
		System.out.println(check(quiz));

		if(check(quiz)){
			result.setSuccess(true);
			result.setMessage(new StringBuilder("The code is compilable"));
		}else{
			result.setSuccess(false);
			result.setMessage(new StringBuilder("The code is not compilable."));
		}
		return result;
	}
	public  ServerMessage getClassFileById(Integer classId){
		ServerMessage serverMessage = new ServerMessage();
		//check if it exists
		if(webexDatabase.checkClassId(classId) > 0){
			StringBuilder filename = webexDatabase.getClassFileNameById(classId);
      String filepath = Setting.JAVACLASSFOLDER + "//" + filename.toString();
				
				serverMessage.setSuccess(true);
				serverMessage.setContent(new Object[]{filepath});;
	
		}else{
			serverMessage.setSuccess(false);
			serverMessage.setMessage(new StringBuilder("No such class."));
		}
		return serverMessage;
	}
	 public  boolean check(Quiz quiz) {
	        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	        String code = quiz.getCode().toString();
	            JavaSourceFromString jsfs = new JavaSourceFromString( "Tester", code);
	            
	            List<SimpleJavaFileObject> js = new ArrayList<SimpleJavaFileObject>();
	            js.add(jsfs);

	        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
			Set<Integer> set = quiz.getLinkedClasses();
			int setlen = set.size();
	        File[] aclass = new File[setlen];
	        int i = 0;
	        
	        System.out.println(setlen);
			for (Integer s : set) {
				 System.out.println(s);
				ServerMessage sm = getClassFileById(s);
				if(sm.isSuccess()){
					File f = new File((String) sm.getContent()[0]);
					aclass[i++] = f;
				}
			}
	        Iterable<? extends JavaFileObject> ijo = (fileManager.getJavaFileObjectsFromFiles(Arrays.asList(aclass)));
	        Iterable<? extends JavaFileObject> compilationUnits =js;
	        
	        ArrayList<JavaFileObject> list = new ArrayList<JavaFileObject>();
	        
	          for(JavaFileObject e: compilationUnits) {
	            list.add(e);
	          
	        }
	          for(JavaFileObject e: ijo) {
	              list.add(e);
	            
	          }
	        

	        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	        boolean success = compiler.getTask(null, fileManager, diagnostics, null, null, list).call();

	        List<String> messages = new ArrayList<String>();
	        Formatter formatter = new Formatter();
	        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
	            messages.add(diagnostic.getKind() + ":\t Line [" + diagnostic.getLineNumber() + "] \t Position [" + diagnostic.getPosition() + "]\t" + diagnostic.getMessage(Locale.ROOT) + "\n");
	            System.out.println(diagnostic.getKind() + ":\t Line [" + diagnostic.getLineNumber() + "] \t Position [" + diagnostic.getPosition() + "]\t" + diagnostic.getMessage(Locale.ROOT) + "\n");
	        }

	        return success;
	    }
	
	/*
	 * this method updates one quiz's related classes
	 */
	public ServerMessage updateQuizClassRel(Integer quizId, HashSet<Integer> classIds){
		ServerMessage serverMessage = new ServerMessage();
		if(webexDatabase.ifQuizHasClasses(quizId) > 0){
			//remove
			webexDatabase.removeClassesUnderQuiz(quizId);
		}
		if(classIds.size() > 0){
			if(webexDatabase.addClassesToQuiz(quizId, classIds) > 0){
				serverMessage.setSuccess(true);
				serverMessage.setMessage(new StringBuilder("External classes linked."));
			}else{
				serverMessage.setSuccess(false);
				serverMessage.setMessage(new StringBuilder("No external classes linked."));
			}
		}else{
			serverMessage.setSuccess(true);
			serverMessage.setMessage(new StringBuilder("External classes linked."));
		}
		return serverMessage;
	}
	
	/*
	 * this method updates one quiz
	 */
	public ServerMessage updateQuiz(Quiz quiz){
		ServerMessage serverMessage = new ServerMessage();
		Quiz originalQuiz = (Quiz) this.getQuizById(quiz.getQuizId()).getContent()[0];
		if(originalQuiz.needNewVersion(quiz)){
			//this quiz's core parts are changed, a new version is needed
			quiz.setVersion(originalQuiz.getVersion() + 1);
			userService.addAuthorInfoToQuiz(quiz);		
			System.out.println("creat a new version");
			ServerMessage isCompile = this.checkCodeSyntax(quiz);
			if(isCompile.isSuccess()){
			
			
			Quiz quizInDb = webexDatabase.newQuiz(quiz);
			serverMessage.setSuccess(true); 
			serverMessage.setMessage(new StringBuilder("Quiz updated with new version."));
			serverMessage.setContent(new Object[]{quizInDb});}
			else {
				return isCompile;
			}
		}else{
			//this quiz just needs to be updated
			ServerMessage isCompile = this.checkCodeSyntax(quiz);
			if(!isCompile.isSuccess()){
				return isCompile;
			}
			System.out.println("update a old version");
			if(webexDatabase.updateQuiz(quiz) > 0){
				serverMessage.setSuccess(true);
				serverMessage.setMessage(new StringBuilder("Quiz updated."));
			}else{
				serverMessage.setSuccess(false);
				serverMessage.setMessage(new StringBuilder("Quiz update failed."));
			}
		}
		return serverMessage;
	}
	
	/*
	 * this method returns all the code parts for one quiz
	 */
	public HashMap<StringBuilder, StringBuilder> getCodePartsByQuizId(Integer quizId){
		HashMap<StringBuilder, StringBuilder> codeParts = new HashMap<StringBuilder, StringBuilder>();
		Quiz quiz = webexDatabase.getQuizById(quizId);
		codeParts.put(new StringBuilder("Tester.py"), quiz.getCode());
		
		HashSet<Integer> classSet = webexDatabase.getClassListByQuizId(quizId);
		if(classSet.size() > 0){
			Iterator<Integer> iterator = classSet.iterator();
			while(iterator.hasNext()){
				Integer classId = iterator.next();
				StringBuilder codePart = (StringBuilder) externalClassService.getClassById(classId).getContent()[0];
				codeParts.put(webexDatabase.getClassFileNameById(classId), codePart);
			}
		}
		
		return codeParts;
	}
}
