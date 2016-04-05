package edu.pitt.pawslab.quizpet.instance;

import java.util.ArrayList;

public class ConceptNode {
	private Integer id;
	private StringBuilder classFile;
	private StringBuilder name;
	private Integer startLine;
	private Integer endLine;
	private ArrayList<ConceptNode> children;
	private StringBuilder title;
	public StringBuilder getTitle() {
		return title;
	}

	public void setTitle(StringBuilder title) {
		this.title = title;
	}

	private String weight;
	private String direction;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StringBuilder getClassFile() {
		return classFile;
	}

	public void setClassFile(StringBuilder classFile) {
		this.classFile = classFile;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public StringBuilder getName() {
		return name;
	}
	
	public void setName(StringBuilder name) {
		this.name = name;
	}
	
	public Integer getStartLine() {
		return startLine;
	}
	
	public void setStartLine(Integer startLine) {
		this.startLine = startLine;
	}
	
	public Integer getEndLine() {
		return endLine;
	}
	
	public void setEndLine(Integer endLine) {
		this.endLine = endLine;
	}
	
	public ArrayList<ConceptNode> getChildren() {
		return children;
	}
	
	public void setChildren(ArrayList<ConceptNode> children) {
		this.children = children;
	}
	
	public static void traverse(final ArrayList<ConceptNode> collection, ConceptNode root){
		if(root.getWeight() == null)
			root.setWeight("1.0");
		if(root.getDirection() == null)
			root.setDirection("outcome");
		collection.add(root);
		if(root.getChildren().size() < 1){
			return;
		}else{
			for(int i = 0; i < root.getChildren().size(); i++){
				ConceptNode.traverse(collection, root.getChildren().get(i));
			}
		}
	}
}
