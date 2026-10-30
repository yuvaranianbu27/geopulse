package com.yuva.geopulse.service;

import com.yuva.geopulse.config.AppProperties;
import com.yuva.geopulse.model.DriverLocationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LocationProducerService {

    private static final Logger log = LoggerFactory.getLogger(LocationProducerService.class);

    private final KafkaTemplate<String, DriverLocationEvent> kafkaTemplate;
    private final String topic;

    public LocationProducerService(
            KafkaTemplate<String, DriverLocationEvent> kafkaTemplate,
            AppProperties appProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = appProperties.kafka().topic();
    }

    public Mono<Void> publishDriverLocation(DriverLocationEvent event) {
        validate(event);

        return Mono.fromFuture(kafkaTemplate.send(topic, event.getDriverId(), event))
                .doOnSuccess(result -> log.debug("Published driver location for {} to topic {}", event.getDriverId(), topic))
                .doOnError(error -> log.error("Failed to publish driver location for {}", event.getDriverId(), error))
                .then();
    }

    private void validate(DriverLocationEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Driver location event must not be null");
        }

        if (event.getDriverId() == null || event.getDriverId().isBlank() || event.getDriverId().length() > 128) {
            throw new IllegalArgumentException("driverId must contain between 1 and 128 characters");
        }

        if (event.getLatitude() == null || event.getLongitude() == null) {
            throw new IllegalArgumentException("latitude and longitude are required");
        }

        if (!Double.isFinite(event.getLatitude()) || event.getLatitude() < -90.0 || event.getLatitude() > 90.0) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }

        if (!Double.isFinite(event.getLongitude()) || event.getLongitude() < -180.0 || event.getLongitude() > 180.0) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }
    }
}
