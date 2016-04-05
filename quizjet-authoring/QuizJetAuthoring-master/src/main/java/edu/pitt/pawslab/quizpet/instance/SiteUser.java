package edu.pitt.pawslab.quizpet.instance;

public class SiteUser {
	private int id;
	private StringBuilder name;
	private StringBuilder role;
	private StringBuilder login;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public StringBuilder getName() {
		return name;
	}
	
	public void setName(StringBuilder name) {
		this.name = name;
	}
	
	public StringBuilder getRole() {
		return role;
	}
	
	public void setRole(StringBuilder role) {
		this.role = role;
	}
	
	public StringBuilder getLogin() {
		return login;
	}
	
	public void setLogin(StringBuilder login) {
		this.login = login;
	}
	
}
