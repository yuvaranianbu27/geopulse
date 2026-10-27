package com.yuva.geopulse.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Bean
    public HealthIndicator geopulseHealthIndicator() {
        return () -> Health.up()
                .withDetail("service", "geopulse")
                .withDetail("status", "running")
                .build();
    }
}
