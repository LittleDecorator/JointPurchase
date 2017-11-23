package com.acme.service;

import com.acme.model.dto.instagram.InstagramPostDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by nikolay on 26.06.17.
 */
public interface InstagramService {

    //InstagramUser getSelf(String accessToken);
    //
    //InstagramUser getUser(String userId, String accessToken);
    //
    //List<InstagramPostDto> getRecent(String userId, String accessToken);
    //
    //void uploadRecent(String userId, String accessToken) throws IOException;
    //
    //List<InstagramPostDto> parseRecent(String input);
    //
    //public void uploadSelf(String accessToken);
    //
    //public void uploadUser(String userId, String accessToken);

    void fetchByTag(String user, String password, String tag) throws IOException;

    List<InstagramPostDto> getPosts(boolean all);

    Map<String, Object> getPostByContentId(String contentId);

    void updatePost(List<InstagramPostDto> posts);

    void deletePost(String id);
}
