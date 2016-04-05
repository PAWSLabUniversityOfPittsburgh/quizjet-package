package edu.pitt.pawslab.quizpet.instance;

public class ServerMessage {
	private boolean success;
	private StringBuilder message;
	private Object[] content;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public StringBuilder getMessage() {
		return message;
	}
	public void setMessage(StringBuilder message) {
		this.message = message;
	}
	public Object[] getContent() {
		return content;
	}
	public void setContent(Object[] content) {
		this.content = content;
	}
}
