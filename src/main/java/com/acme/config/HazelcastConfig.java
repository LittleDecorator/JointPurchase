package com.acme.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.config.MultiMapConfig;
import java.util.List;
import org.assertj.core.util.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Bean
    public Config hazelCastConfig(){
        /*
            Для всех кэшей выставляем максимальный размер в 100Mb.
            Очищаем те что реже всего используются
            Время жизни 20 сек
        */
        List<String> cacheNames = Lists.newArrayList("decode","encode","image","write","read","view","gallery","preview","thumb","origin");
        Config config = new Config().setInstanceName("default");
        cacheNames.forEach(name-> config.addMapConfig(
            new MapConfig()
                .setName(name)
                .setMaxSizeConfig(new MaxSizeConfig(100, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setTimeToLiveSeconds(60 * 60)));
        return config;
    }

}
