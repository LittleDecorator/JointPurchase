package com.acme.sms;

public class SMSResponseReport extends SMSResponse {

	public SMSResponseReport() {
		super();
	}

	public SMSResponseReport(SMSResponse response) {
		super(response);
	}

	private String stateMessage = null;

	private boolean smsOnTheWay = false;
	private boolean smsDelivered = false;
	private boolean smsError = false;

	public String getStateMessage() {
		return stateMessage;
	}

	public void setStateMessage(String stateMessage) {
		this.stateMessage = stateMessage;
	}

	public boolean isSmsOnTheWay() {
		return smsOnTheWay;
	}

	public void setSmsOnTheWay(boolean smsOnTheWay) {
		this.smsOnTheWay = smsOnTheWay;
	}

	public boolean isSmsDelivered() {
		return smsDelivered;
	}

	public void setSmsDelivered(boolean smsDelivered) {
		this.smsDelivered = smsDelivered;
	}

	public boolean isSmsError() {
		return smsError;
	}

	public void setSmsError(boolean smsError) {
		this.smsError = smsError;
	}

}
