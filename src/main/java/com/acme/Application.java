package com.acme;

import com.acme.util.CustomResolver;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.dezhik.sms.sender.SenderService;
import ru.dezhik.sms.sender.SenderServiceConfiguration;
import ru.dezhik.sms.sender.SenderServiceConfigurationBuilder;

import java.util.List;
import java.util.Locale;

@ComponentScan("com.acme")
@Configurable
@EnableCaching
@EnableScheduling
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = "com.acme.repository")
@EnableElasticsearchRepositories(basePackages = "com.acme.elasticsearch")
@PropertySource(value = "file:${properties.location}",ignoreResourceNotFound = true)
public class Application extends WebMvcConfigurerAdapter {

    @Value("${sms.api_id}")
    private String apiId;

//    @Autowired
//    private ResourceProperties resourceProperties = new ResourceProperties();

    /**
     * Фильтр jwt
     * @return
     */
    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter());
        registrationBean.addUrlPatterns("/private/*");
        return registrationBean;
    }

    /**
     * Конфигуратор сервиса sms.ru
     * @return
     */
    @Bean
    public SenderServiceConfiguration senderConfiguration(){
        SenderServiceConfiguration senderConfiguration = SenderServiceConfigurationBuilder.create()
                .setApiId(apiId)
                .build();
        return senderConfiguration;
    }

    /**
     * Сервис sms.ru
     * @return
     */
    @Bean
    public SenderService senderService(){
        return new SenderService(senderConfiguration());
    }

    /**
     * Redirect HTTP to HTTPS
     * @return
     */
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//
//        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
//        return tomcat;
//    }
//
//    private Connector initiateHttpConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        connector.setPort(8080);
//        connector.setSecure(false);
//        connector.setRedirectPort(8443);
//        return connector;
//    }

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
