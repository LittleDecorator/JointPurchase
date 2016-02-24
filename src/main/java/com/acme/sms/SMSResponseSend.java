package com.acme.sms;

public class SMSResponseSend extends SMSResponse {

	private String smsId = null;

	public SMSResponseSend() {
		super();
	}

	public SMSResponseSend(SMSResponse response) {
		super(response);
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

}
