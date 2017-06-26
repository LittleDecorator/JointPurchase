package com.acme.service.impl;

import com.acme.model.dto.instagram.UserInfo;
import com.acme.service.InstagramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by nikolay on 26.06.17.
 */
public class InstagramServiceImpl implements InstagramService {

    @Override
    public UserInfo getSelf(String accessToken) {
        RestTemplate template = new RestTemplate();
        String selfUrl = "https://api.instagram.com/v1/users/self/?access_token=" + accessToken;
        return template.getForObject(selfUrl, UserInfo.class);
    }

    @Override
    public UserInfo getUser(String userId, String accessToken) {
        return null;
    }

    @Override
    public String getRecentMedia(String userId, String accessToken) {
        RestTemplate template = new RestTemplate();
        String recentUrl = "https://api.instagram.com/v1/users/"+userId+"/media/recent/?access_token="+accessToken;
        ResponseEntity<String> response = template.getForEntity(recentUrl, String.class);
        return null;
    }
}
