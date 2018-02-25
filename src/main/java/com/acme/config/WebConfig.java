//package com.acme.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
//import java.util.List;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
//@Configuration
//public class WebConfig extends WebMvcConfigurationSupport {
//
//    public MappingJackson2HttpMessageConverter jacksonMessageConverter(){
//        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//
//        ObjectMapper objectMapper = messageConverter.getObjectMapper();
//
//        //--- register hibernateModule in MappingJackson2HttpMessageConverter.objectMapper
//        objectMapper.registerModule(new Hibernate5Module());
//
//        //--- other configurations
//        messageConverter.setPrettyPrint( true );
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//
//        messageConverter.setObjectMapper(objectMapper);
//        return messageConverter;
//
//    }
//
//    @Override
//    protected void extendMessageConverters( List<HttpMessageConverter<?>> converters ) {
//        //Here we add our custom-configured HttpMessageConverter
//        converters.add(jacksonMessageConverter());
//        super.configureMessageConverters(converters);
//    }
//
//}
