package com.acme.service.impl;

import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.Content;
import com.acme.model.InstagramPost;
import com.acme.model.InstagramPostContent;
import com.acme.model.dto.InstagramPostDto;
import com.acme.model.InstagramUser;
import com.acme.repository.ContentRepository;
import com.acme.repository.InstagramPostContentRepository;
import com.acme.repository.InstagramPostRepository;
import com.acme.repository.InstagramUserRepository;
import com.acme.service.ImageService;
import com.acme.service.InstagramService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by nikolay on 26.06.17.
 *
 * Сервис работы с Instagram API
 */
@Service
public class InstagramServiceImpl implements InstagramService {

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    InstagramUserRepository userRepository;

    @Autowired
    InstagramPostRepository postRepository;

    @Autowired
    InstagramPostContentRepository postContentRepository;

    @Autowired
    ImageService imageService;

    public void uploadRecent(String userId, String accessToken) throws IOException {
        InstagramUser user = userRepository.findOneByoriginId(userId);
        List<InstagramPostDto> posts = getRecent(userId, accessToken);
        InstagramPost post;
        Content content;
        InstagramPostContent postContent;

        for(InstagramPostDto dto : posts){
            // создаем пост
            post = new InstagramPost();
            post.setOriginId(dto.getOriginId());
            post.setContent(dto.getCaption());
            post.setTags(dto.getTags());
            post.setCreateTime(dto.getCreatedTime());
            post.setExternalUrl(dto.getExternalUrl());
            post.setInstagramUserId(user.getId());
            post.setLikesCount(dto.getLikesCount());
            post.setUserHasLiked(dto.isUserHasLiked());
            post = postRepository.save(post);

            // загружаем изображения
            for(String url : dto.getContentUrls()){
                content = new Content();
                content.setInstagram(true);
                String type = url.substring(url.lastIndexOf('.')+1);
                content.setType(type);
                String mime = "image/"+type;
                content.setMime(mime);
                content.setDefault(false);
                content.setProfile(false);
                content.setFileName("unknown."+type);
                content.setContent(Base64BytesSerializer.serialize(imageService.downloadImage(url,type)));
                content = contentRepository.save(content);

                // добавим связь
                postContent = new InstagramPostContent();
                postContent.setPostId(post.getId());
                postContent.setContentId(content.getId());
                postContentRepository.save(postContent);
            }
        }
    }

    @Override
    public InstagramUser getSelf(String accessToken) {
        RestTemplate template = new RestTemplate();
        String selfUrl = "https://api.instagram.com/v1/users/self/?access_token=" + accessToken;
        //TODO: переделать парсинг пользователя
//        return template.getForObject(selfUrl, InstagramUser.class);
        ResponseEntity<String> response = template.getForEntity(selfUrl, String.class);
        return parseUser(response.getBody());
    }

    @Override
    public InstagramUser getUser(String userId, String accessToken) {
        return null;
    }

    @Override
    public List<InstagramPostDto> getRecent(String userId, String accessToken) {
        RestTemplate template = new RestTemplate();
        String recentUrl = "https://api.instagram.com/v1/users/"+userId+"/media/recent/?access_token="+accessToken;
        ResponseEntity<String> response = template.getForEntity(recentUrl, String.class);
        return parseRecent(response.getBody());
    }

    public void uploadSelf(String accessToken){
        uploadUser("1790249622", accessToken);
    }

    public void uploadUser(String userId, String accessToken){
        InstagramUser user = getSelf(accessToken);
        userRepository.save(user);
    }

    /**
     * Используем другую библиотеку для получения изображений
     * @param user
     * @param password
     * @param tag
     * @throws IOException
     */
    @Override
    public void getMostByTag(String user, String password, String tag) throws IOException {
        Instagram4j instagram = Instagram4j.builder().username(user).password(password).build();
        instagram.setup();
        instagram.login();

        /* получим изображения */
        InstagramFeedResult tagFeed = instagram.sendRequest(new InstagramTagFeedRequest(tag));
        List<InstagramFeedItem> items = tagFeed.getItems();
        items.addAll(tagFeed.getRanked_items());
        List<InstagramFeedItem> result = items.stream().sorted(Comparator.comparingInt(InstagramFeedItem::getLike_count)).limit(30).collect(Collectors.toList());

        InstagramPost post;
        Content content;
        InstagramPostContent postContent;

        // один ответ содержит только одно изображение (пока)
        for (InstagramFeedItem dto : result) {
            // создаем пост
            post = new InstagramPost();
            post.setOriginId(dto.getId());
            post.setContent(String.valueOf(dto.getCaption().get("text")));
            List<String> tags = com.google.common.collect.Lists.newArrayList(tag);
            if(dto.getUsertags()!=null){
                tags.addAll(dto.getUsertags().values().stream().map(String::valueOf).collect(Collectors.toList()));
            }
            post.setTags(tags);
            post.setCreateTime(Long.parseLong(String.valueOf(dto.getCaption().get("created_at"))));
            String rawUrl = String.valueOf(((Map)((ArrayList)dto.getImage_versions2().values().iterator().next()).get(0)).get("url"));
            post.setExternalUrl(rawUrl.substring(0, rawUrl.indexOf('?')));
            //post.setInstagramUserId(dto.getUser().getPk());
            post.setLikesCount(dto.getLike_count());
            post.setUserHasLiked(dto.isHas_liked());
            post = postRepository.save(post);

            // загружаем изображения
            content = new Content();
            content.setInstagram(true);
            String type = post.getExternalUrl().substring(post.getExternalUrl().lastIndexOf('.')+1);
            content.setType(type);
            String mime = "image/"+type;
            content.setMime(mime);
            content.setDefault(false);
            content.setProfile(false);
            content.setFileName("unknown."+type);
            content.setContent(Base64BytesSerializer.serialize(imageService.downloadImage(post.getExternalUrl(), type)));
            content = contentRepository.save(content);

                // добавим связь
            postContent = new InstagramPostContent();
            postContent.setPostId(post.getId());
            postContent.setContentId(content.getId());
            postContentRepository.save(postContent);

        }

    }

    /**
     * Парсинг последних публикаций
     * @param input
     * @return
     */
    public List<InstagramPostDto> parseRecent(String input){
        List<InstagramPostDto> result = Lists.newArrayList();
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode data = mapper.readTree(input).get("data");
            if (data.isArray()) {
                for (final JsonNode node : data) {
                    result.add(parsePost(node));
                }
            }
            result.removeAll(Collections.singleton(null));
        } catch (Exception ex){
            System.out.println("Не удалось разобрать ответ!");
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Парсинг публикации
     * @param data
     * @return
     */
    private InstagramPostDto parsePost(JsonNode data){
        InstagramPostDto result = new InstagramPostDto();
        List<String> images = Lists.newArrayList();
        ObjectMapper mapper = new ObjectMapper();
        try{
            String type = data.at("/type").asText();
            if(type.contentEquals("video")) return null;
            // получим оригинальный id
            result.setOriginId(data.at("/id").asText());
            result.setExternalUrl(data.at("/link").asText());
            result.setCreatedTime(data.at("/created_time").asLong());
            result.setCaption(data.at("/caption/text").asText());
            result.setUserHasLiked(data.at("/user_has_liked").asBoolean());
            result.setLikesCount(data.at("/likes/count").asInt());
            result.setTags(mapper.readerFor(new TypeReference<List<String>>(){}).readValue(data.at("/tags")));
            switch(type) {
                case "carousel" : {
                    for(JsonNode imageInfo : data.at("/carousel_media").findValues("standard_resolution")){
                        images.add(imageInfo.at("/url").asText());
                    }
                    break;
                }
                case "image" : {
                    images.add(data.at("/images").findValue("standard_resolution").at("/url").asText());
                    break;
                }
            }
            result.setContentUrls(images);
        } catch (Exception ex){
            System.out.println("Не удалось разобрать узел ответа!");
            ex.printStackTrace();
        }
        return result;
    }

    private InstagramUser parseUser(String input){
        InstagramUser result = new InstagramUser();
        ObjectMapper mapper = new ObjectMapper();
        try{
            JsonNode data = mapper.readTree(input).get("data");
            String type = data.at("/type").asText();
            if(type.contentEquals("video")) return null;
            // получим оригинальный id
            result.setOriginId(data.at("/id").asText());
            result.setUsername(data.at("/username").asText());
            result.setFullName(data.at("/full_name").asText());
            result.setBio(data.at("/bio").asText());
            result.setWebsite(data.at("/website").asText());
            result.setFollows(data.at("/counts/follows").asInt());
            result.setFollowedBy(data.at("/counts/followed_by").asInt());
        } catch (Exception ex){
            System.out.println("Failed");
            ex.printStackTrace();
        }
        return result;
    }
}
