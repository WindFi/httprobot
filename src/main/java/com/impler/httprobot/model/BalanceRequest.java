package com.impler.httprobot.model;

public class BalanceRequest {
	
	private String phone;
	private String verification;
	
	public BalanceRequest() {}
	
	public BalanceRequest(String phone) {
		this.phone = phone;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getVerification() {
		return verification;
	}
	public void setVerification(String verification) {
		this.verification = verification;
	}

}
