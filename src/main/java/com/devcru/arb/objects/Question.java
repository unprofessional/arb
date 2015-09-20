package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Question {

	//@JsonProperty("id")
	long id = 1L;
	
	@JsonProperty("text")
	String text = "Hello World";
	
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
