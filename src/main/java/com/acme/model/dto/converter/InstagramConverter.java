package com.acme.model.dto.converter;

import com.acme.model.InstagramPost;
import com.acme.model.InstagramPostContent;
import com.acme.model.InstagramUser;
import com.acme.model.dto.instagram.InstagramPostDto;
import com.acme.model.dto.instagram.InstagramUserDto;
import java.util.List;
import java.util.stream.Collectors;

public class InstagramConverter {

    /**
     * Получение DTO для поста в Instagram
     * @param post
     * @param postContents
     * @return
     */
    public static InstagramPostDto postToDto(InstagramPost post, List<InstagramPostContent> postContents){
        InstagramPostDto dto = new InstagramPostDto();
        dto.setId(post.getId());
        dto.setTags(post.getTags());
        dto.setOriginId(post.getOriginId());
        String content = post.getContent();
        //String newContent = content.length() > 200 ? content.substring(0, 200) + " ..." : content;
        dto.setCaption(content);
        dto.setLikesCount(post.getLikesCount());
        dto.setCreatedTime(post.getCreateTime().getTime());
        dto.setExternalUrl(post.getExternalUrl());
        dto.setWrongPost(post.isWrongPost());
        dto.setShowOnMain(post.isShowOnMain());
        List<String> urls = postContents.stream().map(link -> "media/image/gallery/" + link.getContentId()).collect(Collectors.toList());
        dto.setContentUrls(urls);
        return dto;
    }

    /**
     *
     * @param user
     * @return
     */
    public static InstagramUserDto userToDto(InstagramUser user){
        InstagramUserDto dto = new InstagramUserDto();
        dto.setId(user.getId());
        dto.setProfilePictureUrl("media/image/thumb/" + user.getProfilePictureId());
        dto.setName(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setCreatedTime(user.getDateAdd().getTime());
        return dto;
    }

}
