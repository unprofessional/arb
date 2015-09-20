package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonResponse {

	@JsonProperty("event")
	private String event = "";
	
	@JsonProperty("data")
	private Object data;

	public JsonResponse(String event, Object data) {
		this.setEvent(event);
		this.setData(data);
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}