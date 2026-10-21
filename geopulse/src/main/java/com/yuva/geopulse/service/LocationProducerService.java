package com.yuva.geopulse.service;

import com.yuva.geopulse.config.KafkaConfig;
import com.yuva.geopulse.model.DriverLocationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LocationProducerService {

    private final KafkaTemplate<String, DriverLocationEvent> kafkaTemplate;

    public LocationProducerService(KafkaTemplate<String, DriverLocationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Void> publishDriverLocation(DriverLocationEvent event) {
        if (event == null) {
            return Mono.error(new IllegalArgumentException("Driver location event must not be null"));
        }

        if (event.getDriverId() == null || event.getDriverId().isBlank()) {
            return Mono.error(new IllegalArgumentException("driverId must not be blank"));
        }

        if (event.getLatitude() == null || event.getLongitude() == null) {
            return Mono.error(new IllegalArgumentException("latitude and longitude are required"));
        }

        return Mono.fromFuture(kafkaTemplate.send(KafkaConfig.DRIVER_LOCATION_TOPIC, event.getDriverId(), event))
                .then();
    }
}
