package com.acme.config;

import com.google.common.collect.Lists;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.List;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HazelcastConfig {

    private final Logger log = LoggerFactory.getLogger(HazelcastConfig.class);

    ////@Bean
    //private Config hazelCastConfig(){
    //    /*
    //        Для всех кэшей выставляем максимальный размер в 100Mb.
    //        Очищаем те что реже всего используются
    //        Время жизни 20 сек
    //    */
    //    List<String> cacheNames = Lists.newArrayList("item", "category","company","content","delivery","item_content","sale");
    //    Config config = new Config().setInstanceName("default");
    //    cacheNames.forEach(name-> config.addMapConfig(
    //        new MapConfig()
    //            .setName(name)
    //            .setMaxSizeConfig(new MaxSizeConfig(10, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
    //            .setEvictionPolicy(EvictionPolicy.LRU)
    //            .setTimeToLiveSeconds(60 * 60)));
    //    log.info("Cache configured!");
    //    return config;
    //}


    //@Bean
    //public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
    //    log.debug("Starting HazelcastCacheManager");
    //    CacheManager cacheManager = new HazelcastCacheManager(hazelcastInstance);
    //    return cacheManager;
    //}
    //
    //@PreDestroy
    //public void destroy() {
    //    log.info("Closing Cache Manager");
    //    Hazelcast.shutdownAll();
    //}

    //@Bean
    //public HazelcastInstance hazelcastInstance() {
    //    log.debug("Configuring Hazelcast");
    //    //Config config = new Config();
    //    Config config = hazelCastConfig();
    //    config.setInstanceName("hazelcast");
    //    config.getNetworkConfig().setPort(5701);
    //    config.getNetworkConfig().setPortAutoIncrement(true);
    //
    //    config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
    //    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
    //    config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("localhost").setEnabled(true);
    //
    //    //config.getMapConfigs().put("default", hazelCastConfig());
    //    config.getManagementCenterConfig().
    //        setUrl("http://localhost:8080/mancenter")
    //        .setEnabled(true);
    //    log.info("Cache instance configured!");
    //    return Hazelcast.newHazelcastInstance(config);
    //}

}
