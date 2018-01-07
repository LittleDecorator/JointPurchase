package com.acme.controller;

import com.acme.model.dto.instagram.InstagramPostDto;
import com.acme.service.InstagramService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by nikolay on 26.06.17.
 */
@RestController
@RequestMapping(value = "/api/instagram")
public class InstagramController {

    @Autowired
    private InstagramService instagramService;

    @RequestMapping(method = RequestMethod.POST, value = "/most")
    public void uploadMost() throws IOException {
        instagramService.fetchByTag("grimmstory","nina210313", "grimmstory");
    }

    /**
     * Получаем список постов
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET, value = "/visible")
    public List<Map<String, Object>> getFullPosts() throws IOException {
        return instagramService.getFullPosts();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/refresh")
    public List<InstagramPostDto> refreshPosts() throws IOException {
        uploadMost();
        return instagramService.getPosts(false);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<InstagramPostDto> getPosts(@RequestParam(value = "all", required = false, defaultValue = "false") boolean all) throws IOException {
        return instagramService.getPosts(all);
    }

    /**
     * Обновляем посты
     * @param dtos
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void updatePosts(@RequestBody InstagramPostDto[] dtos) throws IOException {
        instagramService.updatePost(Arrays.asList(dtos));
    }

    /**
     * Удаляем пост
     * @param id
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public void setToIgnore(@PathVariable String id) throws IOException {
        instagramService.deletePost(id);
    }
}
