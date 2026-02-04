package com.example.swifttransport.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    public static final String CACHE_DRIVERS = "drivers";
    public static final String CACHE_DRIVER_BY_ID = "driverById";
    public static final String CACHE_VEHICLES = "vehicles";
    public static final String CACHE_VEHICLE_BY_ID = "vehicleById";
    public static final String CACHE_ASSIGNMENTS = "assignments";
    public static final String CACHE_ASSIGNMENT_BY_ID = "assignmentById";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Use JDK serialization which handles complex objects reliably
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
        
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer))
                .disableCachingNullValues();

        // Custom cache configurations with different TTLs
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        
        // Driver caches - 5 minutes
        cacheConfigs.put(CACHE_DRIVERS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigs.put(CACHE_DRIVER_BY_ID, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Vehicle caches - 5 minutes
        cacheConfigs.put(CACHE_VEHICLES, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigs.put(CACHE_VEHICLE_BY_ID, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        // Assignment caches - shorter TTL (2 minutes) as they change more frequently
        cacheConfigs.put(CACHE_ASSIGNMENTS, defaultConfig.entryTtl(Duration.ofMinutes(2)));
        cacheConfigs.put(CACHE_ASSIGNMENT_BY_ID, defaultConfig.entryTtl(Duration.ofMinutes(2)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .transactionAware()
                .build();
    }
}
