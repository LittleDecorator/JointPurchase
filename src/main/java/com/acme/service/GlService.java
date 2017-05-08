package com.acme.service;

import com.acme.model.dto.GoogleGl;

/**
 * Created by nikolay on 08.05.17.
 */
public interface GlService {

    /**
     * Использование Google "URL Shortener" для получение короткой версии исходного URL
     *
     * {@link https://developers.google.com/url-shortener/v1/getting_started#APIKey}
     *
     * @param url
     * @return
     */
    GoogleGl getGlLink(String url);

}
