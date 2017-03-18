package com.acme.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * Created by nikolay on 25.02.17.
 */
@Component
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.acme.elasticsearch")
@PropertySources({
        @PropertySource(value = "classpath:elasticsearch.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "classpath:app.properties", ignoreResourceNotFound = true) })
@ComponentScan(basePackages = "com.acme.elasticsearch")
public class ElasticSearchConfig implements DisposableBean {

    private static Log logger = LogFactory.getLog(ElasticSearchConfig.class);

    @Autowired
    private ElasticsearchProperties properties;

    private NodeClient client;

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() {
        return new ElasticsearchTemplate(esClient());
    }

    @Bean
    public Client esClient() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Starting Elasticsearch client");
            }
            NodeBuilder nodeBuilder = new NodeBuilder();
            nodeBuilder
                    .clusterName(this.properties.getClusterName())
                    .local(false)
            ;
            nodeBuilder.settings()
                    .put("http.enabled", true)
            ;
            this.client = (NodeClient)nodeBuilder.node().client();
            return this.client;
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (this.client != null) {
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("Closing Elasticsearch client");
                }
                if (this.client != null) {
                    this.client.close();
                }
            }
            catch (final Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Error closing Elasticsearch client: ", ex);
                }
            }
        }
    }

}