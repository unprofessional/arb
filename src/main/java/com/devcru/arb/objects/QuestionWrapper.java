package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionWrapper {
	
	@JsonProperty("question")
	QuestionRequest questionRequest;

	public QuestionRequest getQuestionRequest() {
		return questionRequest;
	}

	public void setQuestionRequest(QuestionRequest questionRequest) {
		this.questionRequest = questionRequest;
	}

}
