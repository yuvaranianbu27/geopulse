package com.yuva.geopulse.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String DRIVER_LOCATION_TOPIC = "driver-locations";

    @Bean
    public NewTopic driverLocationTopic() {
        return TopicBuilder.name(DRIVER_LOCATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
