package com.acme;

import com.acme.servlet.PublicServlet;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Locale;

@ComponentScan("com.acme")
@Configurable
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableTransactionManagement(proxyTargetClass=true)
@EnableAutoConfiguration
public class TestApplication extends WebMvcConfigurerAdapter {

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

    public static void main(String[] args) {
        Locale american = new Locale("en", "US");
        Locale.setDefault(american);
        SpringApplication.run(TestApplication.class, args);
    }

}
