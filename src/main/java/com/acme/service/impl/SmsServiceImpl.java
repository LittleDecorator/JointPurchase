package com.acme.service.impl;

import com.acme.model.dto.sms.BalanceDto;
import com.acme.model.dto.sms.LimitDto;
import com.acme.model.dto.sms.SendDto;
import com.acme.service.SmsService;
import com.acme.sms.CredentialKey;
import com.acme.sms.SMSAccount;
import com.acme.util.StringTemplate;
import com.google.api.client.util.Maps;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {

    private final static String ID = "68322532-51b1-d4b4-3dd5-752cf2c20589";
    private final static String SMS_HOST = "https://sms.ru/";
    private final static String BASE_TEMPLATE = "{host}{prefix}?api_id={id}";

    @Override
    public void passChangeConfirm() {
        System.out.println("passChangeConfirm call");
    }


    @Override
    public void getDayLimitInfo() {
        String pathFragment = "/my/limit";
        RestTemplate restTemplate = new RestTemplate();
        String limit = restTemplate.getForObject(SMS_HOST + pathFragment + "?api_id="+ID, String.class);
        System.out.println(limit);
    }

    @Override
    public void getBalanceInfo() {
        RestTemplate restTemplate = new RestTemplate();

        StringTemplate template = new StringTemplate(BASE_TEMPLATE);

        Map<String, String> params = Maps.newHashMap();
        params.put("host", SMS_HOST);
        params.put("prefix", "/my/balance");
        params.put("id", ID);

        String balance = restTemplate.getForObject(template.format(params), String.class);
        System.out.println(balance);
    }

    @Override
    public void sendSimple(SMSAccount account, String from, String to, String text) {
        //TODO: нужна поддержка SMSAccount'а клиента
    }

    @Override
    public void sendSimple(String to, String text) {
        sendSimple(to, text, false);
    }

    @Override
    public void sendSimple(String to, String text, boolean isTest) {
        RestTemplate restTemplate = new RestTemplate();

        StringTemplate template = new StringTemplate(BASE_TEMPLATE+"&to={to}&text={text}");

        Map<String, String> params = Maps.newHashMap();
        params.put("host", SMS_HOST);
        params.put("prefix", "/sms/send");
        params.put("id", ID);
        params.put("to", to);
        params.put("text", text);

        String send = restTemplate.getForObject(template.format(params), String.class);
        System.out.println(send);
    }
}
