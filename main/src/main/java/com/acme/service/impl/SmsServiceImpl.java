package com.acme.service.impl;

import com.acme.service.SmsService;
import com.acme.sms.CredentialKey;
import com.acme.sms.SMSAccount;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class SmsServiceImpl implements SmsService {

    private static final String SEND_URL = "http://safexi.ru/api.php?action=send";
    private static final String CODE_URL = "http://safexi.ru/api.php?action=code_sms";
    private static final String STATUS_URL = "http://safexi.ru/api.php?action=status";

    @Override
    public void sendCallback(SMSAccount account, String from, String to, String text){
        System.out.println("Testing 1 - Send Http GET request");
        try {
            send(account, from, to, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(SMSAccount account, String from, String to, String text) throws Exception{

        URI uri = null;
        try {
            URIBuilder b = new URIBuilder(SEND_URL)
                    .setParameter("login", account.get(CredentialKey.EMAIL))
                    .setParameter("pass", account.get(CredentialKey.PASSWORD))
                    .setParameter("mess", text)
                    .setParameter("number", to)
                    .setParameter("name", from);
            uri = b.build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
        final String result = execute(uri);

        //print result
        System.out.println(result);
    }

    private String execute(HttpClient httpClient, URI uri) throws IOException {
        System.out.println("\nSending 'GET' request to URL : " + uri);
        final HttpGet get = new HttpGet(uri);
        return httpClient.execute(get, new BasicResponseHandler());
    }

    private String execute(URI uri) throws IOException {
        HttpClient httpClient = HttpClientBuilder
                .create()
                .setConnectionManager(ConnectionManagerHolder.connectionManager)
                .build();
        return execute(httpClient, uri);
    }

    private static class ConnectionManagerHolder {
        private static final HttpClientConnectionManager connectionManager = init();

        private static HttpClientConnectionManager init() {
            PoolingHttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager();
            ccm.setMaxTotal(10);
            return ccm;
        }
    }

}
