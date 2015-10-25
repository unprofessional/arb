package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerResponse {
	@JsonProperty("answer")
	private String answer;
	
	public AnswerResponse(String text) {
		answer = text;
	}
}
