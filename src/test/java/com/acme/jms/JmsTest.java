package com.acme.jms;

import com.acme.Application;
import com.acme.TestApplication;
import com.google.api.client.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;

/**
 * Created by nikolay on 13.08.17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JmsTest {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    JmsTemplate jmsTemplate;

    @Test
    public void jmsRunner() {
        System.out.println("Sending an email message.");
        Map<String, String> params = Maps.newHashMap();
        params.put("recipient", "bla");
        params.put("token", "fla");
        jmsTemplate.convertAndSend("registration_request", params);
    }
}
