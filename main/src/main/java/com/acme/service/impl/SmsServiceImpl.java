package com.acme.service.impl;

import com.acme.service.SmsService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class SmsServiceImpl implements SmsService {

    private final String USER_AGENT = "Mozilla/5.0";

    @Override
    public void sendCallback(){
        System.out.println("Testing 1 - Send Http GET request");
        try {
            send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send() throws Exception{
        String url = "http://safexi.ru/api.php?action=send&login=knpdeveloper@gmail.com&pass=25oct87!&number=79263959143&name=BLAAA&mess=Just+a+test+sms";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        //name
//        con.addRequestProperty("login","knpdeveloper@gmail.com");
//        con.addRequestProperty("pass","25oct87!");
//        con.addRequestProperty("number","79263959143");
//        con.addRequestProperty("name","GrimmStory");
//        con.addRequestProperty("mess","Just a test sms");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
    }



}
