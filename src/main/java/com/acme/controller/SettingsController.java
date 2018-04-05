package com.acme.controller;

import com.acme.model.Settings;
import com.acme.service.SettingsService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/settings")
public class SettingsController {

    private SettingsService settingsService;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Set<Settings> getSettings() {
        return settingsService.getAllActive();
    }

}
