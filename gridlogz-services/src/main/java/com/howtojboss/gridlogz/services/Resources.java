package com.howtojboss.gridlogz.services;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.infinispan.cdi.ConfigureCache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 *
 * @author Shane K Johnson
 */
public class Resources {
    
    @ConfigureCache("message-cache")
    @MessageCache
    @Produces
    public Configuration configuration;

    @Produces
    @ApplicationScoped
    public EmbeddedCacheManager defaultClusteredCacheManager() {
        
        return new DefaultCacheManager(
                new GlobalConfigurationBuilder().transport().defaultTransport().globalJmxStatistics().enable().jmxDomain("grid-logger").build(),
                new ConfigurationBuilder().clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(1).jmxStatistics().enable().build());
    }
}
