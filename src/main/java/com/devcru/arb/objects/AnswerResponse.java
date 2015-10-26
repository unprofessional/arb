package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerResponse {
	@JsonProperty("answer")
	private String answer;
	
	@JsonProperty("distance")
	private double distance;
	
	public AnswerResponse(String text, double dist) {
		answer = text;
		distance = dist;
	}
}
