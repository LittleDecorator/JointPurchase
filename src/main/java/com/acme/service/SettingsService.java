package com.acme.service;

import com.acme.model.Settings;
import java.util.Set;

public interface SettingsService {

    Set<Settings> getAllActive();

    int getInt(String key);

    float getFloat(String key);

    boolean getBoolean(String key);

    String getString(String key);

}
