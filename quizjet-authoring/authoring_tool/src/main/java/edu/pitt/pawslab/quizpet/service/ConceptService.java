package edu.pitt.pawslab.quizpet.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.pitt.pawslab.quizpet.database.WebexDatabase;
import edu.pitt.pawslab.quizpet.instance.ConceptNode;
import edu.pitt.pawslab.quizpet.instance.DB;
import edu.pitt.pawslab.quizpet.instance.Parser2;
import edu.pitt.pawslab.quizpet.instance.Quiz;
import edu.pitt.pawslab.quizpet.instance.ServerMessage;
import edu.pitt.pawslab.quizpet.instance.Setting;

@Service
public class ConceptService {
	
	@Autowired
	WebexDatabase webexDatabase;
	 @Autowired
	 ServletContext context; 
	@Autowired
	QuizService quizService;
	@Autowired
	ExternalClassService externalClassService;
	
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	private static StringBuilder parseCode(StringBuilder code) throws Exception{
		URL url = new URL(Setting.PARSERURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		
		String parameters = Setting.PARSERTYPENAME + "=" + Setting.PARSERTYPEVALUE + "&"
				+ Setting.PARSERCODENAME + "=" + UriUtils.encodeQueryParam(code.toString(), "UTF-8");
		
		connection.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(parameters);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while((inputLine = in.readLine()) != null){
			response.append(inputLine);
		}
		in.close();
		
		return response;
	}
	
	private static ArrayList<ConceptNode> parseConceptTree(StringBuilder codeTree){
		try{
			ConceptNode root = objectMapper.readValue(codeTree.toString(), ConceptNode.class);
			ArrayList<ConceptNode> conceptList = new ArrayList<ConceptNode>();
			ConceptNode.traverse(conceptList, root);
			return conceptList;
		}catch(Exception e){
			return null;
		}
	}

	
	//here is the code and concept check, for this version, just return true
	//add code check later
	public ServerMessage injectConceptForOneQuiz(String rfDfId) throws Exception{
		ServerMessage serverMessage = new ServerMessage();
		DB db = new DB();
		db.connectToWebex21(this.context);
		try {
			Parser2 parser = new Parser2();
			Blob testerCode = db.getQuestionCode(rfDfId);
			int minvar = db.getQuestionMinVar(rfDfId);
			int qtype = db.getQuestionType(rfDfId);
			
			parser.parse(
					rfDfId,
					getSource(testerCode.getBinaryStream(), minvar,
							qtype), this.context, true);
			List<String> quizClassList = db.getQuizClass(rfDfId);
			Map<String, String> externalClassSourceMap = getExternalClassSource(
					rfDfId, minvar, qtype, quizClassList);
			for (String className : externalClassSourceMap.keySet())
				parser.parse(rfDfId,
						externalClassSourceMap.get(className),
						this.context, false);
			parser = null;

			//response.sendRedirect(load);
			//response.sendRedirect("authoring.jsp?type=quiz&message=Example saved successfully!&alert=success");
		} catch (Exception e) {
			serverMessage.setSuccess(false);
			System.out.println("Concept injection failed.");
			serverMessage.setMessage(new StringBuilder("Concept injection failed."));
			e.printStackTrace();
			return serverMessage;
		
			//response.sendRedirect("authoring.jsp?message=Sorry, we could not process your question&alert=danger");
		} finally {
		}
	
		serverMessage.setSuccess(true);
		System.out.println("Concept injection success.");
		serverMessage.setMessage(new StringBuilder("Concept injection success."));
		return serverMessage;
	
		
		
//		HashMap<StringBuilder, StringBuilder> codeParts = quizService.getCodePartsByQuizId(quizId);
//		
//		HashMap<StringBuilder, ArrayList<ConceptNode>> conceptListForOneQuiz = new HashMap<StringBuilder, ArrayList<ConceptNode>>();
//		Iterator<StringBuilder> iterator = codeParts.keySet().iterator();
//		while(iterator.hasNext()){
//			StringBuilder filename = iterator.next();
//			StringBuilder code = codeParts.get(filename);
//			ArrayList<ConceptNode> nodelist = parseConceptTree(parseCode(code));
//			if(nodelist != null){
//				conceptListForOneQuiz.put(filename, nodelist);
//			}else{
//				serverMessage.setSuccess(false);
//				System.out.println("xmykk");
//				serverMessage.setMessage(new StringBuilder(filename + " has errors in its code. Concept injection failed."));
//			}
//			System.out.println("xmy");
//		}
//		System.out.println("xy");
//		Quiz quiz = webexDatabase.getQuizById(quizId);
//		String quizRdfId = quiz.getRdfIdInDb();
//		if(webexDatabase.checkConcepts(quizRdfId) > 0){
//			webexDatabase.removeConcepts(quizRdfId);
//		}
//		if(webexDatabase.addConceptsByClassFile(quizRdfId, conceptListForOneQuiz) > 0){
//			serverMessage.setSuccess(true);
//			serverMessage.setContent(new Object[]{quizRdfId, conceptListForOneQuiz});
//		}else{
//			serverMessage.setSuccess(false);
//			System.out.println("xymm");
//			serverMessage.setMessage(new StringBuilder("Concept injection failed."));
//		}
//		//will comment here later
//		serverMessage.setSuccess(true);
//		System.out.println("xymm");
//		serverMessage.setMessage(new StringBuilder("Concept injection success."));
//		return serverMessage;
	}
	
	public Map<String,String> getExternalClassSource(String question, int minvar, int qtype, List<String> quizClassList)
	{
		Map<String,String> externalClassSourceMap = new HashMap<String,String>();
		String source = "";
		for (String className : quizClassList)
		{
			try {
				System.out.println("additional source");
				source = FileUtils.readFileToString(new File(Setting.JAVACLASSFOLDER + "//"+className));
				System.out.println(source);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			externalClassSourceMap.put(className, source);
		}
		return externalClassSourceMap;	
	}

	private String getSource(InputStream in, int minvar, int qtype) {
		
		byte[] buffer = new byte[1024];

		int P = minvar;
		int position = 0;
		String codepart = "";
		try {
			while ( in.read(buffer) != -1) {
				StringBuffer text = new StringBuffer(new String(buffer));
				int linecount = 0;
				int loc = (new String(text)).indexOf('\n', position);
				while (loc >= 0) {
					loc = (new String(text)).indexOf('\n', position);
					String line = "";
					if (loc > position) {

						line = text.substring(position, loc);
						int b = line.indexOf("_Param");
						int b2 = line.lastIndexOf("_Param");
						if (b > 0 && b == b2) {
							line = line.substring(0, b) + P
									+ line.substring(b + 6);
						} else if (b > 0 && b2 > b) {
							line = line.substring(0, b) + P
									+ line.substring(b + 6, b2) + P
									+ line.substring(b2 + 6);
						}
						linecount = linecount + 1; //just for knowing the line number

						// for Question Type 3
						if (qtype != 3) {
							codepart += line; 
						}
					}
					else
					{
						line = text.substring(position, position+1);
						codepart += line; 
					}
					position = loc + 1;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return codepart;
	}
	
	
	public HashMap<String, ArrayList<ConceptNode>> getConceptsOfOneQuiz(String rdfId){
		ArrayList<ConceptNode> list = webexDatabase.getAllConceptsOfOneQuiz(rdfId);
		HashMap<String, ArrayList<ConceptNode>> rs = new HashMap<String, ArrayList<ConceptNode>>();
		
		Iterator<ConceptNode> iterator = list.iterator();
		while(iterator.hasNext()){
			ConceptNode curr = iterator.next();
			String classFile = curr.getClassFile().toString();
			if(rs.containsKey(classFile)){
				ArrayList<ConceptNode> currList = rs.get(classFile);
				currList.add(curr);
			}else{
				ArrayList<ConceptNode> currList = new ArrayList<ConceptNode>();
				currList.add(curr);
				rs.put(classFile, currList);
			}
		}
		
		return rs;
	}
	
	public HashMap<String, Object[]> getConceptsAndCodeOfOneQuiz(Quiz quiz){
		HashMap<String, Object[]> rs = new HashMap<String, Object[]>();
		System.out.println("CHECK ID");
		System.out.println(quiz.getRdfId().toString());
		System.out.println(quiz.getRdfIdInDb());
		HashMap<String, ArrayList<ConceptNode>> concepts = getConceptsOfOneQuiz(quiz.getRdfIdInDb());
		
		Iterator<String> iterator = concepts.keySet().iterator();
		while(iterator.hasNext()){
			String classFileName = iterator.next();
			ArrayList<ConceptNode> conceptsForThisClass = concepts.get(classFileName);
			
			if(classFileName.equals("Tester.java")){
				//code from this quiz
				StringBuilder code = quiz.getCode();
				Object[] value = new Object[]{code, conceptsForThisClass};
				rs.put(classFileName, value);
			}else{
				//code from external classes
				System.out.println("FILE NAME"+classFileName);
				Integer externalClassId = webexDatabase.getClassIdByFileName(classFileName);
				ServerMessage classCode = externalClassService.getClassById(externalClassId);
				if(classCode.isSuccess()){
					StringBuilder code = (StringBuilder) classCode.getContent()[0];
					Object[] value = new Object[]{code, conceptsForThisClass};
					rs.put(classFileName, value);
				}
			}
		}
		
		return rs;
	}
	
	public Integer updateConceptsOfOneQuiz(Quiz quiz, ArrayList<ConceptNode> newConcepts){
		String rdfIdInDb = quiz.getRdfIdInDb();
		webexDatabase.removeConcepts(rdfIdInDb);
		
		return webexDatabase.addConcepts(rdfIdInDb, newConcepts);
	}
	public Integer deleteConceptsOfOneQuiz(int id){

		
		return webexDatabase.removeConceptById(id);
	}
	public Integer deleteConceptByRdfid(String rdfid){

		
		return webexDatabase.removeConceptByRdfid(rdfid);
	}	
	
	public Integer addConcept(String id, ConceptNode cp){

		
		return webexDatabase.addConceptById(id, cp);
	}
	public HashMap<String,Integer> addConceptMul(String id, String cps){

		return webexDatabase.addConceptMulById(id, cps);
	}
	
	public Integer updateConceptLine(String id, String line){

		
		return webexDatabase.updateConceptLineById(id, line);
	}
	
	/*
	 * this method returns all the python topics
	 */
	public List<String> getAllOntology(){
		System.out.println("Get all ontology.....");
		System.out.println(Setting.JAVACLASSFOLDER);
		DB db = new DB();
		db.connectToUM2(this.context);
		List<String> ls = db.getOntologyConcepts(this.context);
		return ls;
	}
}
