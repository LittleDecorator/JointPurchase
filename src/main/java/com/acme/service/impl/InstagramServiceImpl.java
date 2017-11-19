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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.assertj.core.util.Strings;
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


    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public void uploadSelf(String accessToken){
        uploadUser("1790249622", accessToken);
    }

    @Deprecated
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

        // получим мапу уже выгруженых постов
        Map<String, InstagramPost> map = postRepository.findAll().stream().collect(Collectors.toMap(InstagramPost::getOriginId, Function.identity()));

        InstagramPost post;
        Content content;
        InstagramPostContent postContent;

        // один ответ содержит только одно изображение (пока)
        for (InstagramFeedItem dto : result) {
            // проверяем если у нас такой пост
            post = map.get(dto.getId());

            boolean isNewPost = post == null;

            //если пост игнорируемый, то перейдем к следующему
            if(!isNewPost && post.isWrongPost()) continue;

            // если новый, то создадим и наполним
            if(isNewPost){
                // создаем пост
                post = new InstagramPost();
                post.setOriginId(dto.getId());

                // почистим и сохраним текст поста
                String postText = String.valueOf(dto.getCaption().get("text"));
                post.setContent(clearTags(postText));

                // вычленим все hashtag'и
                List<String> tags = Lists.newArrayList(findTags(postText));
                if(dto.getUsertags()!=null){
                    tags.addAll(dto.getUsertags().values().stream().map(String::valueOf).collect(Collectors.toList()));
                }
                post.setTags(tags);
                // время создания
                post.setCreateTime(Long.parseLong(String.valueOf(dto.getCaption().get("created_at"))));
                // внешние ссылки на изображения
                String rawUrl = String.valueOf(((Map)((ArrayList)dto.getImage_versions2().values().iterator().next()).get(0)).get("url"));
                post.setExternalUrl(rawUrl.substring(0, rawUrl.indexOf('?')));
            }

            // кол-во отметок нравиться
            post.setLikesCount(dto.getLike_count());
            // признак того что пост нравиться
            post.setUserHasLiked(dto.isHas_liked());
            // сохраняем
            post = postRepository.save(post);

            if(isNewPost){
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

    }

    @Override
    public List<InstagramPostDto> getPosts(boolean all) {
        List<InstagramPost> posts = all ? postRepository.findAll() : postRepository.findAllByWrongPostFalse();
        return posts.stream()
            .map(post -> {
                List<InstagramPostContent> postContents = postContentRepository.findAllByPostId(post.getId());
                InstagramPostDto dto = new InstagramPostDto();
                dto.setId(post.getId());
                dto.setTags(post.getTags());
                dto.setOriginId(post.getOriginId());
                String content = clearTags(post.getContent());
                String newContent = content.length() > 200 ? content.substring(0, 200) + " ..." : content;
                dto.setCaption(newContent);
                dto.setLikesCount(post.getLikesCount());
                dto.setCreatedTime(post.getCreateTime().getTime());
                dto.setExternalUrl(post.getExternalUrl());
                dto.setWrongPost(post.isWrongPost());
                dto.setShowOnMain(post.isShowOnMain());
                List<String> urls = postContents.stream().map(link -> "media/image/gallery/" + link.getContentId()).collect(Collectors.toList());
                dto.setContentUrls(urls);
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    public void deletePost(String id) {
        List<InstagramPostContent> postContent = postContentRepository.findAllByPostId(id);
        List<String> contentIds = postContent.stream().map(InstagramPostContent::getContentId).collect(Collectors.toList());
        postContentRepository.deleteByPostId(id);
        postRepository.delete(id);
        contentRepository.deleteAllByIdIn(contentIds);
    }

    @Override
    public void updatePost(List<InstagramPostDto> dtos) {
        for(InstagramPostDto dto : dtos){
            InstagramPost post = postRepository.findOne(dto.getId());
            post.setWrongPost(dto.isWrongPost());
            post.setShowOnMain(dto.isShowOnMain());
            postRepository.save(post);
        }
    }

    /**
     * Парсинг последних публикаций
     * @param input
     * @return
     */
    @Deprecated
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
    @Deprecated
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

    /**
     * Парсинг пользователя
     * @param input
     * @return
     */
    @Deprecated
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

    /**
     * Получение тэгов из содержимого
     * @param text
     * @return
     */
    private  List<String> findTags(String text){
        Pattern MY_PATTERN = Pattern.compile("(#\\w+)");
        Matcher mat = MY_PATTERN.matcher(text);
        List<String> tags = new ArrayList<>();
        while (mat.find()) {
            tags.add(mat.group(1));
        }
        return tags;
    }

    /**
     * Убираем hashtags из содержимого
     * @param text
     * @return
     */
    private String clearTags(String text){
        String[] arr = text.split("\\s+|#\\w+");
        for(int i=1;i<arr.length ; i++){
            if(Strings.isNullOrEmpty(arr[i]) || arr[i].startsWith("#")){
                if(arr[i-1] !=null && arr[i-1].startsWith("#")){
                    arr[i-1] = null;
                }
                if(i+1 < arr.length && (arr[i+1] !=null && arr[i+1].startsWith("#"))){
                    arr[i+1] = null;
                }
            }
        }

        arr = Arrays.stream(arr)
            .filter(s -> (s != null && s.length() > 0))
            .toArray(String[]::new);

        return String.join(" ", arr);
    }
}
