package com.yuva.geopulse.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        @Valid @DefaultValue Geo geo,
        @Valid @DefaultValue Kafka kafka,
        @Valid @DefaultValue Matching matching,
        @Valid @DefaultValue Driver driver) {

    public record Geo(
            @NotBlank(message = "app.geo.redis-key must be configured")
            @DefaultValue("drivers:geo") String redisKey) {
    }

    public record Kafka(
            @NotBlank(message = "app.kafka.topic must be configured")
            @DefaultValue("driver-locations") String topic) {
    }

    public record Matching(
            @Positive(message = "app.matching.max-radius-km must be greater than zero")
            @DefaultValue("50.0") double maxRadiusKm,
            @Positive(message = "app.matching.max-limit must be greater than zero")
            @DefaultValue("25") int maxLimit) {
    }

    public record Driver(
            @Positive(message = "app.driver.stale-after-ms must be greater than zero")
            @DefaultValue("300000") long staleAfterMs) {
    }
}
