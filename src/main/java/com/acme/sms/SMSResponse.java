package com.acme.sms;

public class SMSResponse {

	private boolean ok = false;
	private String raw = null;
	private String message = null;

	public SMSResponse() {
		super();
	}

	public SMSResponse(SMSResponse response) {
		super();
		this.ok = response.isOk();
		this.raw = response.getRaw();
		this.message = response.message;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	@Override
	public String toString() {
		return "SMSResponse [ok=" + ok + ", message=" + message + ", raw="
				+ raw + "]";
	}

}
