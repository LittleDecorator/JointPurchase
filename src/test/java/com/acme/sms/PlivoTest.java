//package com.acme.sms;
//
//import java.util.LinkedHashMap;
//import com.plivo.helper.api.client.*;
//import com.plivo.helper.api.response.message.MessageResponse;
//import com.plivo.helper.exception.PlivoException;
//
///**
// * Created by kobzev on 20.06.17.
// */
//public class PlivoTest {
//
//	public static void main(String[] args) {
//		String authId = "SANDYYODNHMWI5YJNIMW";
//		String authToken = "YzU3NjFhMDYxNWQ0MGJjODU1MGQyNWMxN2FhMjMy";
//		RestAPI api = new RestAPI(authId, authToken, "v1");
//
//		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
//		parameters.put("src", "GrimmStory"); // Sender's phone number with country code
//		parameters.put("dst", "+79263959143"); // Receiver's phone number with country code
//		parameters.put("text", "Hi, text from Test"); // Your SMS text message
//		// Send Unicode text
//		//parameters.put("text", "こんにちは、元気ですか？"); // Your SMS text message - Japanese
//		//parameters.put("text", "Ce est texte généré aléatoirement"); // Your SMS text message - French
//		parameters.put("url", "http://example.com/report/"); // The URL to which with the status of the message is sent
//		parameters.put("method", "GET"); // The method used to call the url
//
//		try {
//			// Send the message
//			MessageResponse msgResponse = api.sendMessage(parameters);
//
//			// Print the response
//			System.out.println(msgResponse);
//			// Print the Api ID
//			System.out.println("Api ID : " + msgResponse.apiId);
//			// Print the Response Message
//			System.out.println("Message : " + msgResponse.message);
//
//			if (msgResponse.serverCode == 202) {
//				// Print the Message UUID
//				System.out.println("Message UUID : " + msgResponse.messageUuids.get(0).toString());
//			} else {
//				System.out.println(msgResponse.error);
//			}
//		} catch (PlivoException e) {
//			System.out.println(e.getLocalizedMessage());
//		}
//	}
//
//}
