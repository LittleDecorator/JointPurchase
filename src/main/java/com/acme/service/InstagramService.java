package com.acme.service;

import com.acme.model.dto.instagram.UserInfo;

/**
 * Created by nikolay on 26.06.17.
 */
public interface InstagramService {

    UserInfo getSelf(String accessToken);

    UserInfo getUser(String userId, String accessToken);

    String getRecentMedia(String userId, String accessToken);
}
