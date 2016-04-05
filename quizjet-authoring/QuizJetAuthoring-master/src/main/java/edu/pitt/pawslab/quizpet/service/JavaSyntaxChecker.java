package edu.pitt.pawslab.quizpet.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
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
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import edu.pitt.pawslab.quizpet.database.WebexDatabase;
import edu.pitt.pawslab.quizpet.instance.JavaSourceFromString;
import edu.pitt.pawslab.quizpet.instance.Quiz;
import edu.pitt.pawslab.quizpet.instance.ServerMessage;
import edu.pitt.pawslab.quizpet.instance.Setting;

@Service

public class JavaSyntaxChecker {

	
	@Autowired
	private static WebexDatabase webexDatabase;
	
	
    public static void main(String[] args) {
    	String code = "int i = 0;";
        
        //System.out.println(JavaSyntaxChecker.check(code));
    }
	public static ServerMessage getClassFileById(Integer classId){
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
    public static boolean check(Quiz quiz) {
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
}