package com.sell.arkaysell.bean;

/**
 * This is class is use for Single answare question and it's have just question and single answare
 * @author Arkay
 *
 */
public class Question {
	private int questionNo;
	private String question;
	private String rightans;
	
	public Question(int questionNo, String question) {
		super();
		this.questionNo = questionNo;
		this.question = question;
	}

	public int getQuestionNo() {
		return questionNo;
	}
	public void setQuestionNo(int questionNo) {
		this.questionNo = questionNo;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnsware() {
		return rightans;
	}
	public void setAnsware(String rightans) {
		this.rightans = rightans;
	}

	@Override
	public String toString() {
		return question +" "+rightans;
	}
	
	
}
