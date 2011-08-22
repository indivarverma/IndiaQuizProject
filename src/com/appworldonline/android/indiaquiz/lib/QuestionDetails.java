package com.appworldonline.android.indiaquiz.lib;

import java.util.List;
import java.util.Vector;

public class QuestionDetails {
	private int questionID;
	private int categoryID;
	private int totalPresentations;
	private int correctAttempts;
	private String bunchFileName;
	
	public String getBunchFileName() {
		return bunchFileName;
	}
	public void setBunchFileName(String bunchFileName) {
		this.bunchFileName = bunchFileName;
	}
	public int getQuestionID() {
		return questionID;
	}
	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}


	public int getCategoryID() {
		return categoryID;
	}
	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}


	private String question;
	private int rightAnswer;
	private List<String> answers;
	private String link;
	
	public QuestionDetails() {
		// TODO Auto-generated constructor stub
		
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public List<String> getAnswers() {
		return answers;
	}
	public void addAnswer(String answer) {
		if(this.answers == null){
			this.answers = new Vector<String>();
		}
		this.answers.add(answer);
	}
	
	public void setRightAnswer(int rightAnswer) {
		this.rightAnswer = rightAnswer;
	}
	public int getRightAnswer() {
		return rightAnswer;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	
	public String getLink() {
		return link;
	}
	public int getTotalPresentations() {
		return totalPresentations;
	}
	public void setTotalPresentations(int totalPresentations) {
		this.totalPresentations = totalPresentations;
	}
	public int getCorrectAttempts() {
		return correctAttempts;
	}
	public void setCorrectAttempts(int correctAttempts) {
		this.correctAttempts = correctAttempts;
	}
}
