package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionWrapper {
	
	@JsonProperty("question")
	Question question;

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

}
