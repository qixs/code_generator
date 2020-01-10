package com.qxs.generator.web.model;

public class GenerateResult {

	private Status status;

	private String message;

	
	public GenerateResult() {
		super();
	}
	
	public GenerateResult(Status status) {
		super();
		this.status = status;
	}
	
	public GenerateResult(Status status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static enum Status {
		SUCCESS, FAIL
	}
}
