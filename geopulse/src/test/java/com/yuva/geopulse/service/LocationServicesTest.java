package com.yuva.geopulse.service;

import com.yuva.geopulse.config.KafkaConfig;
import com.yuva.geopulse.model.DriverLocationEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LocationServicesTest {

    @Test
    void publishDriverLocation_shouldSendToKafka() {
        KafkaTemplate<String, DriverLocationEvent> kafkaTemplate = mock(KafkaTemplate.class);
        LocationProducerService service = new LocationProducerService(kafkaTemplate);
        DriverLocationEvent event = new DriverLocationEvent("driver-42", 40.7128, -74.0060, 1720000000000L);

        CompletableFuture<SendResult<String, DriverLocationEvent>> future = CompletableFuture.completedFuture(
                new SendResult<>(new ProducerRecord<>(KafkaConfig.DRIVER_LOCATION_TOPIC, "driver-42", event), null)
        );
        when(kafkaTemplate.send(eq(KafkaConfig.DRIVER_LOCATION_TOPIC), eq("driver-42"), eq(event))).thenReturn(future);

        assertDoesNotThrow(() -> service.publishDriverLocation(event).block());
        verify(kafkaTemplate).send(KafkaConfig.DRIVER_LOCATION_TOPIC, "driver-42", event);
    }

    @Test
    void publishDriverLocation_shouldRejectInvalidEvent() {
        KafkaTemplate<String, DriverLocationEvent> kafkaTemplate = mock(KafkaTemplate.class);
        LocationProducerService service = new LocationProducerService(kafkaTemplate);

        IllegalArgumentException nullEventException = assertThrows(
                IllegalArgumentException.class,
                () -> service.publishDriverLocation(null).block()
        );
        assertEquals("Driver location event must not be null", nullEventException.getMessage());

        IllegalArgumentException blankDriverException = assertThrows(
                IllegalArgumentException.class,
                () -> service.publishDriverLocation(new DriverLocationEvent("", 40.7128, -74.0060, 1720000000000L)).block()
        );
        assertEquals("driverId must not be blank", blankDriverException.getMessage());

        IllegalArgumentException missingCoordinatesException = assertThrows(
                IllegalArgumentException.class,
                () -> service.publishDriverLocation(new DriverLocationEvent("driver-42", null, -74.0060, 1720000000000L)).block()
        );
        assertEquals("latitude and longitude are required", missingCoordinatesException.getMessage());

        verifyNoInteractions(kafkaTemplate);
    }


}
