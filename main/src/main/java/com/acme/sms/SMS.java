//package com.acme.sms;
//
//import java.io.IOException;
//
//public interface SMS {
//
//	SMSAccount getAccount();
//
//	/**
//	 * Login on service
//	 *
//	 * @param account
//	 *            Account
//	 * @return Server response body
//	 * @throws IOException
//	 *             if something bad happened with internet connection
//	 */
//	SMSResponse login(SMSAccount account) throws IOException;
//
//	/**
//	 * Logout from sms24x7 service
//	 *
//	 * @return Server response body
//	 * @throws IOException
//	 *             if something bad happened with internet connection
//	 */
//	SMSResponse logout() throws IOException;
//
//	/**
//	 * Single method to send
//	 *
//	 * @param account
//	 *            Account
//	 * @param from
//	 *            Sender name. Less or equal to 11 latin symbols (GSM_0338
//	 *            exactly)
//	 * @param to
//	 *            Recipient phone number in international format. E.g.
//	 *            79991234567
//	 * @param text
//	 *            Text of the message
//	 * @return Server response body
//	 * @throws IOException
//	 *             if something bad happened with internet connection
//	 */
//	SMSResponseSend send(SMSAccount account, String from, String to, String text)
//			throws IOException;
//
//	/**
//	 * Send sms using previously logged in session
//	 *
//	 * @param from
//	 *            Sender name. Less or equal to 11 latin symbols (GSM_0338
//	 *            exactly)
//	 * @param to
//	 *            Recipient phone number in international format. E.g.
//	 *            79991234567
//	 * @param text
//	 *            Text of the message
//	 * @return Server response body
//	 * @throws IOException
//	 *             if something bad happened with internet connection
//	 */
//	SMSResponseSend send(String from, String to, String text)
//			throws IOException;
//
//	SMSResponseProfile getProfile() throws IOException;
//
//	SMSResponseReport getSmsReport(String smsId) throws IOException;
//
//}
