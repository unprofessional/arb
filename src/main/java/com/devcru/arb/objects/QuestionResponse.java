package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionResponse {

	// Class is internal use, for RequestBody, use AskRequest
	
	@JsonProperty("id")
	long id = 1L;
	
	@JsonProperty("text")
	String text = "";
	
	@JsonProperty("distance")
	double distance;
	
	public QuestionResponse(long id, String text, double distance) {
		this.id = id;
		this.text = text;
		this.distance = distance;
	}
}
