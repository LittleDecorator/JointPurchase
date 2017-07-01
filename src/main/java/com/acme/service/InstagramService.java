package com.acme.service;

import com.acme.model.dto.InstagramPostDto;
import com.acme.model.InstagramUser;

import java.util.List;

/**
 * Created by nikolay on 26.06.17.
 */
public interface InstagramService {

    InstagramUser getSelf(String accessToken);

    InstagramUser getUser(String userId, String accessToken);

    List<InstagramPostDto> getRecent(String userId, String accessToken);

    List<InstagramPostDto> parseRecent(String input);
}
