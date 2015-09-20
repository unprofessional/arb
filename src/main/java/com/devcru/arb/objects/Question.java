package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Question {

	// Class is internal use, for RequestBody, use AskRequest
	
	@JsonProperty("id")
	long id = 1L;
	
	@JsonProperty("text")
	String text = "Hello World";
	
	public Question(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

}
