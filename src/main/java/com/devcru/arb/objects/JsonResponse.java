package com.devcru.arb.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonResponse {

	@JsonProperty("status")
	private String status = "";
	
	@JsonProperty("data")
	private Object data;

	public JsonResponse(String status, Object data) {
		this.setStatus(status);
		this.setData(data);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}