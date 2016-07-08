package com.acme;

import com.acme.servlet.PublicServlet;
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Locale;

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

    public static void main(String[] args) {
        Locale american = new Locale("en", "US");
        Locale.setDefault(american);
        SpringApplication.run(Application.class, args);
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        Integer cachePeriod = resourceProperties.getCachePeriod();
//
//        registry.addResourceHandler("/public/**")
//                .addResourceLocations("classpath:/public/")
//                .setCachePeriod(cachePeriod);
//
//        registry.addResourceHandler("/**")
//                .addResourceLocations("classpath:/public/index.html")
//                .setCachePeriod(cachePeriod).resourceChain(true)
//                .addResolver(new PathResourceResolver() {
//                    @Override
//                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
//                        return location.exists() && location.isReadable() ? location : null;
//                    }
//                });
//    }

}
