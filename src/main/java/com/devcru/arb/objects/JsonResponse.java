package com.devcru.arb.objects;

public class JsonResponse {

	private String event = "";
	private String data = "";

	public JsonResponse(String event, String data) {
		this.setEvent(event);
		this.setData(data);
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
}