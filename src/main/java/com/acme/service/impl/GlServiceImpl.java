package com.acme.service.impl;

import com.acme.model.dto.GoogleGl;
import com.acme.service.GlService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by nikolay on 08.05.17.
 */
@Service
@Slf4j
public class GlServiceImpl implements GlService{

    @Value("${gl.url}")
    private String glUrl;

    @Override
    public GoogleGl getGlLink(String url) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject object = new JSONObject();
        object.put("longUrl", url);
        return restTemplate.postForObject(glUrl, object, GoogleGl.class);
    }
}
