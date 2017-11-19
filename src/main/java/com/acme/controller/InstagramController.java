package com.acme.controller;

import com.acme.model.dto.InstagramPostDto;
import com.acme.service.InstagramService;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.util.Lists;
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
    InstagramService instagramService;

    ///**
    // * загрузка последних своих публикаций
    // */
    //@RequestMapping(method = RequestMethod.POST, value = "/recent/self")
    //public void uploadRecentSelf() throws IOException {
    //    String accessToken = "1790249622.d721e87.87db8e7f779244edb7e47f52e65ec424";
    //    instagramService.uploadRecent("1790249622", accessToken);
    //}
    //
    //@RequestMapping(method = RequestMethod.POST, value = "/self")
    //public void uploadSelf() throws IOException {
    //    String accessToken = "1790249622.d721e87.87db8e7f779244edb7e47f52e65ec424";
    //    instagramService.uploadSelf(accessToken);
    //}
    //
    //@RequestMapping(method = RequestMethod.POST, value = "/most")
    //public void uploadMost() throws IOException {
    //    instagramService.getMostByTag("grimmstory","nina210313", "grimmstory");
    //}

    /**
     * Получаем список постов
     * @return
     * @throws IOException
     */
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
