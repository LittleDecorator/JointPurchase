package com.acme.sms;

import java.util.Map;

public interface SMSAudit {

	String login(SMSAccount account, String message, String response);

	String send(SMSAccount account, String from, String to, String text,
				String message, String response);

	String getProfile(SMSAccount account, String message, String response);

	String getSmsReport(SMSAccount account, String smsId, String message,
						String response);

	String put(SMSAccount account, Map<String, Object> logRecord);

}
