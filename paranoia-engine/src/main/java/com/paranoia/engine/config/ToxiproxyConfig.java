package com.paranoia.engine.config;

import com.paranoia.engine.execution.ToxiproxyManager;
import com.paranoia.engine.execution.ToxiproxyManagerImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty("toxiproxy.url")
public class ToxiproxyConfig {

    @Bean
    ToxiproxyManager toxiproxyManager(
            @Value("${toxiproxy.url}") String url,
            @Value("${toxiproxy.proxy-name:postgres}") String proxyName) {
        return new ToxiproxyManagerImpl(url, proxyName);
    }
}
