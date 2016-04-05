package edu.pitt.pawslab.quizpet.instance;

public class Topic {
	private int topicId;
	private int authorId;
	private int groupId;
	private StringBuilder title;
	private StringBuilder decp;
	private boolean privacy;
	private StringBuilder domain;
	
	public int getTopicId() {
		return topicId;
	}
	
	public void setTopicId(int topicId) {
		this.topicId = topicId;
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
	
	public boolean isPrivacy() {
		return privacy;
	}
	
	public void setPrivacy(boolean privacy) {
		this.privacy = privacy;
	}
	
	public StringBuilder getDomain() {
		return domain;
	}
	
	public void setDomain(StringBuilder domain) {
		this.domain = domain;
	}
	
}
