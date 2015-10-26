package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerRequest {
	
	@JsonProperty("questionId")
	long questionId = 1L;
	
	@JsonProperty("text")
	String text = "";

	@JsonProperty("latitude")
	double latitude = 0.0;
	
	@JsonProperty("longitude")
	double longitude = 0.0;
	
	public long getQuestionId() {
		return questionId;
	}
	public String getText() {
		return text;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
}
