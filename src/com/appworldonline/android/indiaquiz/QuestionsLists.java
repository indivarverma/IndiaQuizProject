package com.appworldonline.android.indiaquiz;

import java.util.Vector;
import com.appworldonline.android.indiaquiz.lib.QuestionDetails;

public class QuestionsLists {
	private Vector<QuestionDetails> attemptedQuestions;
	private Vector<Integer> attemptedAnswer;
	private static QuestionsLists instance;
	private QuestionsLists(){		
		attemptedQuestions = new Vector<QuestionDetails>();
		attemptedAnswer = new Vector<Integer>();
	}
	public static QuestionsLists getInstance(){
		if(instance == null){
			instance = new QuestionsLists();
		}
		return instance;
	}
	public synchronized void addAttemptedQuestion(QuestionDetails _question, int _attemptedAnswer){
		attemptedQuestions.add(_question);
		attemptedAnswer.add(_attemptedAnswer);
	}
	public synchronized void clearAttemptedQuestions(){
		attemptedQuestions.clear();
		attemptedAnswer.clear();
	}
	public synchronized int getAttemptedQuestionsCount(){
		if(attemptedQuestions==null) return 0;
		return attemptedQuestions.size();
	}
	public synchronized QuestionDetails getAttemptedQuestionsDetails(int index){
		if(attemptedQuestions==null || attemptedQuestions.size() <= index){
			return null;
		}
		return attemptedQuestions.elementAt(index);
	}
	public synchronized Integer getAttemptedQuestionsAnswer(int index){
		if(attemptedAnswer==null || attemptedAnswer.size() <= index){
			return null;
		}
		return attemptedAnswer.elementAt(index);
	}
}
