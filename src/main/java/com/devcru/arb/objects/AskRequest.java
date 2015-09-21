package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AskRequest {
	
	@JsonProperty("text")
	String text;;
	
	@JsonProperty("latitude")
	double latitude;
	
	@JsonProperty("longitude")
	double longitude;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	

}
