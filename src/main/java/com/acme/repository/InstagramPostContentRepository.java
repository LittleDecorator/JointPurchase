package com.acme.repository;

import com.acme.model.InstagramPostContent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by nikolay on 03.07.17.
 */
public interface InstagramPostContentRepository extends CrudRepository<InstagramPostContent, String> {

    void deleteByPostId(String postId);

    List<InstagramPostContent> findAllByPostId(String postId);

    List<InstagramPostContent> findAllByPostIdIn(List<String> postId);

    InstagramPostContent findByPostIdAndContentId(String postId, String contentId);

}
