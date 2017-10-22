package com.acme.config;

import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

/**
 * Created by nikolay on 22.10.17.
 */
public class MustacheXmlLoader {

    @Bean
    public MustacheResourceTemplateLoader getXmlLoader(){
        MustacheResourceTemplateLoader loader = new MustacheResourceTemplateLoader("classpath:/templates/", ".xml");
        loader.setCharset(StandardCharsets.UTF_8.name());
        return loader;
    }

}
