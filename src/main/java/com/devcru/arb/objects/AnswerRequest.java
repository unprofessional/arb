package com.devcru.arb.objects;

public class AnswerRequest {
	
	long questionId = 1L;
	String text = "";

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
