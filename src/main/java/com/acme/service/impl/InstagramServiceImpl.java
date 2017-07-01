package com.acme.service.impl;

import com.acme.model.dto.InstagramPostDto;
import com.acme.model.InstagramUser;
import com.acme.service.InstagramService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Created by nikolay on 26.06.17.
 *
 * Сервис работы с Instagram API
 */
public class InstagramServiceImpl implements InstagramService {

    @Override
    public InstagramUser getSelf(String accessToken) {
        RestTemplate template = new RestTemplate();
        String selfUrl = "https://api.instagram.com/v1/users/self/?access_token=" + accessToken;
        //TODO: переделать парсинг пользователя
        return template.getForObject(selfUrl, InstagramUser.class);
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
}
