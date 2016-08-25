package com.acme;

import com.acme.servlet.PublicServlet;
import com.acme.util.CustomResolver;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;
import java.util.Locale;

//import com.acme.util.CustomResolver;

@ComponentScan("com.acme")
@Configurable
@EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass=true)
@PropertySource(value = "file:${properties.location}",ignoreResourceNotFound = true)
public class Application extends WebMvcConfigurerAdapter {

//    @Autowired
//    private ResourceProperties resourceProperties = new ResourceProperties();


    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/private/*");
        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean publicServlet(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(new PublicServlet());
        registrationBean.addUrlMappings("/public/auth/*");
        return registrationBean;
    }

    //TODO: так мы можем резолвить входящие параметры
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CustomResolver());
    }

    public static void main(String[] args) {
        Locale american = new Locale("en", "US");
        Locale.setDefault(american);
        SpringApplication.run(Application.class, args);
    }

}
