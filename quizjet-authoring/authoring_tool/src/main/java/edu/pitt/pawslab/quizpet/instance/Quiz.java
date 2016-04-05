package edu.pitt.pawslab.quizpet.instance;

import java.text.ParseException;
import java.util.HashSet;

public class Quiz {
	
	private int quizId;
	private int topicId;
	private StringBuilder rdfId;
	private int version = 0;
	
	private StringBuilder date;
	private long timestamp;
	
	private int authorId;
	private String author;
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	private int groupId;
	
	private StringBuilder title;
	private StringBuilder decp;
	private StringBuilder code;
	
	private int minVar;
	private int maxVar;
	private int awsTypeId;
	private int questionTypeId;
	
	private HashSet<Integer> linkedClasses;
	
	private boolean privacy;
	private boolean canModify;

	public boolean isCanModify() {
		return canModify;
	}

	public void setCanModify(boolean canModify) {
		this.canModify = canModify;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public StringBuilder getRdfId() {
		return rdfId;
	}

	public void setRdfId(StringBuilder rdfId) {
		this.rdfId = rdfId;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public StringBuilder getTitle() {
		return title;
	}

	public void setTitle(StringBuilder title) {
		this.title = title;
	}

	public StringBuilder getDecp() {
		return decp;
	}

	public void setDecp(StringBuilder decp) {
		this.decp = decp;
	}

	public StringBuilder getCode() {
		return code;
	}

	public void setCode(StringBuilder code) {
		this.code = code;
	}

	public int getMinVar() {
		return minVar;
	}

	public void setMinVar(int minVar) {
		this.minVar = minVar;
	}

	public int getMaxVar() {
		return maxVar;
	}

	public void setMaxVar(int maxVar) {
		this.maxVar = maxVar;
	}

	public int getAwsTypeId() {
		return awsTypeId;
	}

	public void setAwsTypeId(int awsTypeId) {
		this.awsTypeId = awsTypeId;
	}

	public int getQuestionTypeId() {
		return questionTypeId;
	}

	public void setQuestionTypeId(int questionTypeId) {
		this.questionTypeId = questionTypeId;
	}

	public boolean isPrivacy() {
		return privacy;
	}

	public void setPrivacy(boolean privacy) {
		this.privacy = privacy;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public StringBuilder getDate() {
		return date;
	}

	public void setDate(StringBuilder date) {
		this.date = date;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public HashSet<Integer> getLinkedClasses() {
		return linkedClasses;
	}

	public void setLinkedClasses(HashSet<Integer> linkedClasses) {
		this.linkedClasses = linkedClasses;
	}

	public void timestampToDate(){
		this.date = new StringBuilder(new java.text.SimpleDateFormat(Setting.TIMEFORMAT).format(new java.util.Date(this.timestamp * 1000)));
	}
	
	public void dateToTimestamp() throws ParseException{
		this.timestamp = new java.text.SimpleDateFormat(Setting.TIMEFORMAT).parse(this.date.toString()).getTime();
	}
	
	public String getRdfIdInDb(){
		//prefix
		StringBuilder rStringBuilder = Quiz.getStrWithPrefix("");
		//rdfid for display
		rStringBuilder.append(this.rdfId);
		//if version is not 0, add version
		if(version != 0)
			rStringBuilder.append("_v" + version);
		return rStringBuilder.toString();
	}
	
	public Boolean needNewVersion(Quiz newQuiz){
		//boolean sameRdfId = newQuiz.getRdfId().toString().equals(this.getRdfId().toString());
		boolean sameCode = newQuiz.getCode().toString().equals(this.code.toString());
		boolean sameMax = newQuiz.getMaxVar() == this.maxVar;
		boolean sameMin = newQuiz.getMinVar() == this.minVar;
		boolean sameAnsTypeId = newQuiz.getAwsTypeId() == this.awsTypeId;
		boolean sameLinkedClasses = newQuiz.getLinkedClasses().equals(this.linkedClasses);
		
		//return ! (sameCode	&& sameMax && sameMin && sameAnsTypeId && sameLinkedClasses && sameRdfId);
		return ! (sameCode	&& sameMax && sameMin && sameAnsTypeId && sameLinkedClasses);
	}
	
	public static StringBuilder getStrWithoutPrefix(String title){
		return new StringBuilder(title.replaceAll(Setting.PYTHONPREFFIX.toString(), ""));
	}
	
	public static StringBuilder getStrWithPrefix(String title){
		return new StringBuilder(Setting.QJAVAPREFIX + "_" + title.toString());
	}

}
