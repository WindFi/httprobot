package com.impler.httprobot.model;

public class PushMessageRequest {
	
	private String send_content;
	private String verification;
	
	public PushMessageRequest() {}
	
	public PushMessageRequest(String send_content) {
		this.send_content = send_content;
	}

	public String getSend_content() {
		return send_content;
	}
	public void setSend_content(String send_content) {
		this.send_content = send_content;
	}
	public String getVerification() {
		return verification;
	}
	public void setVerification(String verification) {
		this.verification = verification;
	}

}
