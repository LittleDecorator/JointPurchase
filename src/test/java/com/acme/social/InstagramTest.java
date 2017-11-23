package com.acme.social;

import com.acme.model.Content;
import com.acme.model.InstagramPost;
import com.acme.model.InstagramPostContent;
import com.acme.model.dto.instagram.InstagramPostDto;
import com.acme.model.InstagramUser;
import com.acme.service.InstagramService;
import com.acme.service.impl.InstagramServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by nikolay on 26.06.17.
 */
public class InstagramTest {

    /*Manage Client: GrimmStory
Client ID d721e87b653045fc86c303af04703e06

Client Secret 3adbd57d908445be9b241919e8df6166  RESET SECRET

Client Status Sandbox Mode*/

    public static void main(String[] args) throws IOException, URISyntaxException {
        InstagramTest test = new InstagramTest();

        test.i4jTest();

        //test.self();

//        test.recent();
//        test.parseResentTest();
//        test.downloadImage("/tmp/download.jpg","https://scontent.cdninstagram.com/t51.2885-15/e35/19379947_138472530061616_3467529966546059264_n.jpg");
    }

    private void self(){
        String accessToken = "1790249622.d721e87.87db8e7f779244edb7e47f52e65ec424";
        RestTemplate template = new RestTemplate();
        String selfUrl = "https://api.instagram.com/v1/users/self/?access_token=" + accessToken;
        try{
            ResponseEntity<String> response = template.getForEntity(selfUrl, String.class);
            System.out.println(response.getBody());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.getBody());
            JsonNode data = node.at("/data");

            String dataNodeAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            System.out.println(dataNodeAsString);
            InstagramUser info = mapper.treeToValue(data, InstagramUser.class);
            System.out.println(info);

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void recent(){

    }

    private void downloadImage(String targetName,String fileUrl) throws IOException {
        InputStream initialStream = new URL(fileUrl).openStream();
        Files.copy(initialStream, Paths.get(targetName), StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(initialStream);
    }

    private void parseResentTest() throws URISyntaxException, IOException {
        Path path = Paths.get(getClass().getClassLoader().getResource("resentMedia.json").toURI());

        StringBuilder data = new StringBuilder();
        Stream<String> lines = Files.lines(path);
        lines.forEach(line -> data.append(line).append("\n"));
        lines.close();

        InstagramService service = new InstagramServiceImpl();
        //List<InstagramPostDto> posts = service.parseRecent(data.toString());
        //System.out.println(posts);
    }

    private void i4jTest() throws IOException {
        // Login to instagram
        Instagram4j instagram = Instagram4j.builder().username("grimmstory").password("nina210313").build();
        instagram.setup();
        instagram.login();

        InstagramFeedResult tagFeed = instagram.sendRequest(new InstagramTagFeedRequest("grimmstory"));
        List<InstagramFeedItem> items = tagFeed.getItems();
        items.addAll(tagFeed.getRanked_items());
        List<InstagramFeedItem> result = items.stream().sorted(Comparator.comparingInt(InstagramFeedItem::getLike_count)).limit(30).collect(Collectors.toList());

        InstagramPost post;
        Content content;
        InstagramPostContent postContent;

        for (InstagramFeedItem dto : result) {
            // создаем пост
            post = new InstagramPost();
            post.setOriginId(dto.getId());
            post.setContent(String.valueOf(dto.getCaption().get("text")));
            List<String> tags = Lists.newArrayList("grimmstory");
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
            //post = postRepository.save(post);

            // загружаем изображения
            //for(String url : dto.getContentUrls()){
                content = new Content();
                content.setInstagram(true);
                String type = post.getExternalUrl().substring(post.getExternalUrl().lastIndexOf('.')+1);
                content.setType(type);
                String mime = "image/"+type;
                content.setMime(mime);
                content.setDefault(false);
                content.setProfile(false);
                content.setFileName("unknown."+type);
                //content.setContent(Base64BytesSerializer.serialize(imageService.downloadImage(post.getExternalUrl(), type)));
                //content = contentRepository.save(content);
            //
            //    // добавим связь
                postContent = new InstagramPostContent();
                postContent.setPostId(post.getId());
                postContent.setContentId(content.getId());
                //postContentRepository.save(postContent);
            //}
            //System.out.println(feedResult.getUser().getUsername());


        }
    }

}


/*
* {
    "meta": {
        "code": 400,
        "error_type": "OAuthAccessTokenException",
        "error_message": "The access_token provided is invalid."
    }
}*/



/*curl -F 'client_id=d721e87b653045fc86c303af04703e06' -F 'client_secret=3adbd57d908445be9b241919e8df6166' -F 'grant_type=authorization_code' -F 'redirect_uri=https://grimmstory.ru' -F 'code=06d7a34c48a4460283683ba13ae29042' https://api.instagram.com/oauth/access_token
*/