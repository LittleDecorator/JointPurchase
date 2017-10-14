package com.acme.controller;

import com.acme.service.InstagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    /**
     * загрузка последних своих публикаций
     */
    @RequestMapping(method = RequestMethod.POST, value = "/recent/self")
    public void uploadRecentSelf() throws IOException {
        String accessToken = "1790249622.d721e87.87db8e7f779244edb7e47f52e65ec424";
        instagramService.uploadRecent("1790249622", accessToken);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/self")
    public void uploadSelf() throws IOException {
        String accessToken = "1790249622.d721e87.87db8e7f779244edb7e47f52e65ec424";
        instagramService.uploadSelf(accessToken);
    }

}
