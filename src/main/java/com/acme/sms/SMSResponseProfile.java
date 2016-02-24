package com.acme.sms;

public class SMSResponseProfile extends SMSResponse {

	public SMSResponseProfile() {
		super();
	}

	public SMSResponseProfile(SMSResponse response) {
		super(response);
	}

	private boolean active = false;
	private String balance = null;
	private String currency = null;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
