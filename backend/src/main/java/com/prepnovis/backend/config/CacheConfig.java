package com.prepnovis.backend.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.prepnovis.backend.dto.response.AnalyticsDashboardResponse;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    private static final Logger log =
            LoggerFactory.getLogger(CacheConfig.class);

    public static final String ANALYTICS_DASHBOARD_CACHE =
            "analyticsDashboard";

    private final RedisConnectionFactory connectionFactory;

    public CacheConfig(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean
    @Override
    public CacheManager cacheManager() {

        RedisCacheConfiguration defaultConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues();

        JacksonJsonRedisSerializer<AnalyticsDashboardResponse>
                analyticsSerializer =
                new JacksonJsonRedisSerializer<>(
                        AnalyticsDashboardResponse.class
                );

        RedisCacheConfiguration analyticsConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(
                                                new StringRedisSerializer()
                                        )
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(
                                                analyticsSerializer
                                        )
                        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfiguration)
                .withCacheConfiguration(
                        ANALYTICS_DASHBOARD_CACHE,
                        analyticsConfiguration
                )
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {

        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(
                    RuntimeException exception,
                    Cache cache,
                    Object key) {

                log.warn(
                        "Redis cache GET failed. Cache={}, key={}. Continuing without cache.",
                        cache.getName(),
                        key
                );
            }

            @Override
            public void handleCachePutError(
                    RuntimeException exception,
                    Cache cache,
                    Object key,
                    Object value) {

                log.warn(
                        "Redis cache PUT failed. Cache={}, key={}. Continuing without cache.",
                        cache.getName(),
                        key
                );
            }

            @Override
            public void handleCacheEvictError(
                    RuntimeException exception,
                    Cache cache,
                    Object key) {

                log.warn(
                        "Redis cache EVICT failed. Cache={}, key={}. Continuing without cache.",
                        cache.getName(),
                        key
                );
            }

            @Override
            public void handleCacheClearError(
                    RuntimeException exception,
                    Cache cache) {

                log.warn(
                        "Redis cache CLEAR failed. Cache={}. Continuing without cache.",
                        cache.getName()
                );
            }
        };
    }
}