package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerRequest {
	
	@JsonProperty("questionId")
	long questionId = 1L;
	
	@JsonProperty("text")
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
