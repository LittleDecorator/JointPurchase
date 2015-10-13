package com.acme;

import com.acme.handlers.Base64BytesTypeHandler;
import com.acme.handlers.StringBooleanTypeHandler;
import com.acme.handlers.StringUUIDTypeHandler;
import com.acme.interceptors.UUIDGeneratorInterceptor;
import com.acme.model.mapper.CustomMapper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@MapperScan(basePackages="com.acme.gen")
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix="datasource.primary")
    public DataSource dataSource(){
//        return DataSourceBuilder.create().driverClassName("org.h2.Driver").url("jdbc:h2:file:./target/h2/gen/db").username("sa").build();
        return DataSourceBuilder.create().driverClassName("org.postgresql.Driver").url("jdbc:postgresql://localhost:5432/purchase").username("postgres").password("postgres").build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeHandlersPackage("com.acme");
        //can't use alias package scanner for inner jar
        Class[] classes = new Class[]{StringUUIDTypeHandler.class,StringBooleanTypeHandler.class, Base64BytesTypeHandler.class};
        sessionFactory.setTypeAliases(classes);


        /* if use xml for config*/
//        sessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        /*for some reason if NOT define as resource it would not see this mapper*/
        Resource mapperResource = new ClassPathResource("CustomMapper.xml");
        sessionFactory.setMapperLocations(new Resource[] {mapperResource});
        sessionFactory.setPlugins(getPlugins());
        return sessionFactory.getObject();
    }

    //collect interceptors
    private Interceptor[] getPlugins(){
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(new UUIDGeneratorInterceptor());
        return interceptors.toArray(new Interceptor[]{});
    }

    @Bean
    public CustomMapper customMapper() throws Exception {
        SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory(dataSource()));
        return sessionTemplate.getMapper(CustomMapper.class);
    }


}
