package com.acme.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Created by kobzev on 20.06.17.
 */
public class TwilioTest {

	// Find your Account Sid and Token at twilio.com/user/account
	public static final String ACCOUNT_SID = "ACcbb8d5de181b3c254024c862515dede7";
	public static final String AUTH_TOKEN = "6ba2d8dbebcaaf18ba575b6c8c298658";
	public static final String SERVICE_ID = "MG10160c7b856d4a7006a7fe01c8e0c834";

	//TODO: каждая отправка ест деньги со счета
	public static void main(String[] args) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		Message message = Message.creator( new PhoneNumber("+79263959143"), SERVICE_ID, "Check service name 123")
				.create();

		System.out.println(message.getSid());
	}

}
