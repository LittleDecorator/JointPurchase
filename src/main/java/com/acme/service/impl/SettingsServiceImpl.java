package com.acme.service.impl;

import com.acme.exception.SettingsException;
import com.acme.model.Settings;
import com.acme.repository.SettingsRepository;
import com.acme.service.SettingsService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Sets;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SettingsServiceImpl implements SettingsService {

    private static final String INTEGER = "Integer";
    private static final String FLOAT = "Float";
    private static final String BOOLEAN = "Boolean";
    private static final String STRING = "String";

    private SettingsRepository repository;

    @Autowired
    public SettingsServiceImpl(SettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    //@Cacheable(value = "settings")
    public Set<Settings> getAllActive() {
        Set<Settings> set = Sets.newHashSet();
        for (Settings property : repository.findAll()) {
            if (property.isActive()) set.add(property);
        }
        return set;
    }

    @Override
    public int getInt(String key) {
        Settings property = getPropertyAndCheckType(key, INTEGER);
        return Integer.parseInt(property.getValue());
    }

    @Override
    public float getFloat(String key) {
        Settings property = getPropertyAndCheckType(key, FLOAT);
        return Float.parseFloat(property.getValue());
    }

    @Override
    public boolean getBoolean(String key) {
        Settings property = getPropertyAndCheckType(key, BOOLEAN);
        return Boolean.parseBoolean(property.getValue());
    }

    @Override
    public String getString(String key) {
        Settings property = getPropertyAndCheckType(key, STRING);
        return property.getValue();
    }

    @NotNull
    //@Cacheable(value = "settings")
    public Settings getPropertyAndCheckType(String key, String type) {
        Settings property = getSettings(key);
        if (type.equals(property.getType())) {
            return property;
        }
        throw new SettingsException(String.format("Property with key '%s' is not of specified type", property.getKey()));
    }

    @NotNull
    private Settings getSettings(String key) {
        Settings property = repository.findOne(key);
        if (property == null) throw new SettingsException(String.format("Property with key '%s' doesn't exists", key));
        return property;
    }
}
