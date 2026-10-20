package com.yuva.geopulse.service;

import com.yuva.geopulse.config.KafkaConfig;
import com.yuva.geopulse.model.DriverLocationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LocationProducerService {

    private final KafkaTemplate<String, DriverLocationEvent> kafkaTemplate;

    public LocationProducerService(KafkaTemplate<String, DriverLocationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDriverLocation(DriverLocationEvent event) {
        kafkaTemplate.send(KafkaConfig.DRIVER_LOCATION_TOPIC, event.getDriverId(), event);
    }
}
