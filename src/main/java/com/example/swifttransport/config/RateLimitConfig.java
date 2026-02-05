package com.example.swifttransport.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.rate-limit.login")
public class RateLimitConfig {

    private int maxRequests;
    private int windowDurationSeconds;
}
