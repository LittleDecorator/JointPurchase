package com.acme.social;

import com.acme.model.dto.InstagramPostDto;
import com.acme.model.InstagramUser;
import com.acme.service.InstagramService;
import com.acme.service.impl.InstagramServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
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
//        test.self();
//        test.recent();
//        test.parseResentTest();
        test.downloadImage("/tmp/download.jpg","https://scontent.cdninstagram.com/t51.2885-15/e35/19379947_138472530061616_3467529966546059264_n.jpg");
    }

    private void self(){
        String accessToken = "1790249622.d721e87.e79398d1cdcc450492f7d8e115c999a6";
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
        List<InstagramPostDto> posts = service.parseRecent(data.toString());
        System.out.println(posts);
    }

}
