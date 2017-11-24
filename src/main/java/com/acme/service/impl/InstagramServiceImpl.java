package com.acme.service.impl;

import com.acme.handlers.Base64BytesSerializer;
import com.acme.model.Content;
import com.acme.model.InstagramPost;
import com.acme.model.InstagramPostContent;
import com.acme.model.dto.instagram.InstagramPostDto;
import com.acme.model.InstagramUser;
import com.acme.model.dto.converter.InstagramConverter;
import com.acme.repository.ContentRepository;
import com.acme.repository.InstagramPostContentRepository;
import com.acme.repository.InstagramPostRepository;
import com.acme.repository.InstagramUserRepository;
import com.acme.service.ImageService;
import com.acme.service.InstagramService;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
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


    /**
     * Используем другую библиотеку для получения изображений
     * @param user
     * @param password
     * @param tag
     * @throws IOException
     */
    @Override
    public void fetchByTag(String user, String password, String tag) throws IOException {
        Instagram4j instagram = Instagram4j.builder().username(user).password(password).build();
        instagram.setup();
        instagram.login();

        // получим ответы от робота
        InstagramFeedResult tagFeed = instagram.sendRequest(new InstagramTagFeedRequest(tag));
        List<InstagramFeedItem> items = tagFeed.getItems();
        items.addAll(tagFeed.getRanked_items());

        // отсортируем по популярности и возьмем первые 30
        List<InstagramFeedItem> result = items.stream().sorted(Comparator.comparingInt(InstagramFeedItem::getLike_count)).limit(30).collect(Collectors.toList());

        // получим мапу уже выгруженых постов
        Map<String, InstagramPost> map = postRepository.findAll().stream().collect(Collectors.toMap(InstagramPost::getOriginId, Function.identity()));

        // получим мапу существующих пользователей
        Map<String, InstagramUser> userMap = userRepository.findAll().stream().collect(Collectors.toMap(InstagramUser::getOriginId, Function.identity()));

        InstagramPost post;
        Content content;
        InstagramPostContent postContent;

        // один ответ содержит только одно изображение (пока)
        for (InstagramFeedItem dto : result) {
            // проверяем есть ли у нас такой пост
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

            //найдем пользователя и создадим, если его нет у нас
            InstagramUser instagramUser = userMap.get(String.valueOf(dto.getUser().getPk()));
            String instagramUserId = instagramUser == null ? parseUser(dto.getUser()) : instagramUser.getId();


            // заполним информацию о владальце поста, только если его небыло раньше
            if(Strings.isNullOrEmpty(post.getInstagramUserId())){
                post.setInstagramUserId(instagramUserId);
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
    public List<Map<String, Object>> getFullPosts() {
        List<InstagramPost> posts = postRepository.findAllByShowOnMainIsTrue();
        Map<String, InstagramUser> users = Maps.uniqueIndex(userRepository.findAll(), InstagramUser::getId);
        return posts.stream()
            .map(post -> {
                List<InstagramPostContent> postContents = postContentRepository.findAllByPostId(post.getId());
                InstagramPostDto postDto = InstagramConverter.postToDto(post, postContents);
                Map<String, Object> result = Maps.newHashMap();
                result.put("post", postDto);
                result.put("user", InstagramConverter.userToDto(users.get(post.getInstagramUserId())));
                return result;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<InstagramPostDto> getPosts(boolean all) {
        List<InstagramPost> posts = all ? postRepository.findAll() : postRepository.findAllByWrongPostFalse();
        return posts.stream()
            .map(post -> {
                List<InstagramPostContent> postContents = postContentRepository.findAllByPostId(post.getId());
                return InstagramConverter.postToDto(post, postContents);
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
     *
     * @param dtoUser
     * @return
     * @throws IOException
     */
    private String parseUser(org.brunocvcunha.instagram4j.requests.payload.InstagramUser dtoUser) throws IOException {
        InstagramUser instagramUser;

        // сохраним фото профиля
        Content instagramUserProfileImage = new Content();
        instagramUserProfileImage.setInstagram(true);
        String type = dtoUser.getProfile_pic_url().substring(dtoUser.getProfile_pic_url().lastIndexOf('.')+1);
        instagramUserProfileImage.setType(type);
        String mime = "image/"+type;
        instagramUserProfileImage.setMime(mime);
        instagramUserProfileImage.setDefault(false);
        instagramUserProfileImage.setProfile(true);
        instagramUserProfileImage.setFileName("unknown_profile."+type);
        instagramUserProfileImage.setContent(Base64BytesSerializer.serialize(imageService.downloadImage(dtoUser.getProfile_pic_url(), type)));
        instagramUserProfileImage = contentRepository.save(instagramUserProfileImage);

        // создадим пользователя
        instagramUser = new InstagramUser();
        instagramUser.setOriginId(String.valueOf(dtoUser.getPk()));
        instagramUser.setUsername(dtoUser.getUsername());
        instagramUser.setFullName(dtoUser.getFull_name());
        instagramUser.setBio(dtoUser.getBiography());
        instagramUser.setProfilePictureId(instagramUserProfileImage.getId());
        instagramUser.setFollowedBy(dtoUser.getFollowing_count());
        instagramUser.setFollows(dtoUser.getFollower_count());

        return userRepository.save(instagramUser).getId();
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
    private static String clearTags(String text){
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
