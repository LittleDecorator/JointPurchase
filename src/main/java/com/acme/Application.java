package com.acme;

import com.acme.util.CustomResolver;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.dezhik.sms.sender.SenderService;
import ru.dezhik.sms.sender.SenderServiceConfiguration;
import ru.dezhik.sms.sender.SenderServiceConfigurationBuilder;

import javax.jms.ConnectionFactory;
import javax.persistence.EntityManagerFactory;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ComponentScan("com.acme")
@Configurable
@EnableCaching
@EnableScheduling
@EnableAsync
@EnableJms
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
        return SenderServiceConfigurationBuilder.create().setApiId(apiId).build();
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
     * Фабрика поставляющая слушателей для JMS
     * @param connectionFactory
     * @param configurer
     * @return
     */
    @Bean
    public JmsListenerContainerFactory<?> defaultFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
		factory.setConcurrency("1-1");
        return factory;
    }

    /**
     * Конвертер POJO в JSON для отправки по JMS
     * @return
     */
    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
		HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
		factory.setEntityManagerFactory(emf);
		return factory;
	}

    /**
     * Так мы можем резолвить входящие параметры
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CustomResolver());
    }

    /**
     * Очищаем все кэши через 10 мин
     */
    @CacheEvict(allEntries = true, value = {"view", "gallery", "preview","origin","write","decode","read","image"})
    @Scheduled(fixedDelay = 10 * 60 * 1000 ,  initialDelay = 500)
    public void reportCacheEvict() {
        System.out.println("Flush Cache " + new Date());
    }

    public static void main(String[] args) {
        Locale american = new Locale("en", "US");
        Locale.setDefault(american);
        SpringApplication.run(Application.class, args);
    }

}
