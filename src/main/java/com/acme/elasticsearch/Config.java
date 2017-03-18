//package com.acme.elasticsearch;
//
///**
// * Created by nikolay on 11.02.17.
// */
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.baeldung.spring.data.es.repository")
//@ComponentScan(basePackages = { "com.baeldung.spring.data.es.service" })
//public class Config {
//
//    @Value("${elasticsearch.home:/usr/local/Cellar/elasticsearch/2.3.2}")
//    private String elasticsearchHome;
//
//    private static Logger logger = LoggerFactory.getLogger(Config.class);
//
//    @Bean
//    public Client client() {
//        try {
//            final Path tmpDir = Files.createTempDirectory(Paths.get(System.getProperty("java.io.tmpdir")), "elasticsearch_data");
//            logger.debug(tmpDir.toAbsolutePath().toString());
//
//            // @formatter:off
//
//            final Settings.Builder elasticsearchSettings =
//                    Settings.settingsBuilder().put("http.enabled", "false")
//                            .put("path.data", tmpDir.toAbsolutePath().toString())
//                            .put("path.home", elasticsearchHome);
//
//            return new NodeBuilder()
//                    .local(true)
//                    .settings(elasticsearchSettings)
//                    .node()
//                    .client();
//
//            // @formatter:on
//        } catch (final IOException ioex) {
//            logger.error("Cannot create temp dir", ioex);
//            throw new RuntimeException();
//        }
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        return new ElasticsearchTemplate(client());
//    }
//
//}
