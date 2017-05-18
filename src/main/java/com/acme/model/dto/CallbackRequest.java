package com.acme.model.dto;

/**
 * Created by kobzev on 18.05.17.
 */
public class CallbackRequest {

	private String phone;
	private String message;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "CallbackRequest{" +
			   "phone='" + phone + '\'' +
			   ", message='" + message + '\'' +
			   '}';
	}
}
