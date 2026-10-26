package com.yuva.geopulse.service;

import com.yuva.geopulse.config.KafkaConfig;
import com.yuva.geopulse.model.DriverLocationEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveGeoOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
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

    @Test
    void findNearbyDrivers_shouldReturnMatchingLocations() {
        ReactiveStringRedisTemplate redisTemplate = mock(ReactiveStringRedisTemplate.class);
        ReactiveGeoOperations<String, String> geoOperations = mock(ReactiveGeoOperations.class);
        when(redisTemplate.opsForGeo()).thenReturn(geoOperations);

        GeoResult<RedisGeoCommands.GeoLocation<String>> result = new GeoResult<>(
                new RedisGeoCommands.GeoLocation<>("driver-42", new Point(-74.0060, 40.7128)),
                new Distance(1.2, Metrics.KILOMETERS)
        );
        when(geoOperations.radius(anyString(), any(Circle.class), any(RedisGeoCommands.GeoRadiusCommandArgs.class)))
                .thenReturn(Flux.just(result));

        MatchingService service = new MatchingService(redisTemplate, "drivers:geo");

        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> matches = service
                .findNearbyDrivers(-74.0060, 40.7128, 3.0, 5)
                .collectList()
                .block();

        assertEquals(1, matches.size());
        assertEquals("driver-42", matches.get(0).getContent().getName());
        assertEquals(1.2, matches.get(0).getDistance().getValue(), 0.01);
        verify(geoOperations).radius(eq("drivers:geo"), any(Circle.class), any(RedisGeoCommands.GeoRadiusCommandArgs.class));
    }

    @Test
    void findNearbyDrivers_shouldRejectInvalidRadiusAndLimit() {
        ReactiveStringRedisTemplate redisTemplate = mock(ReactiveStringRedisTemplate.class);
        MatchingService service = new MatchingService(redisTemplate, "drivers:geo");

        IllegalArgumentException radiusException = assertThrows(
                IllegalArgumentException.class,
                () -> service.findNearbyDrivers(-74.0060, 40.7128, 0.0, 5).collectList().block()
        );
        assertEquals("radiusKm must be greater than 0", radiusException.getMessage());

        IllegalArgumentException limitException = assertThrows(
                IllegalArgumentException.class,
                () -> service.findNearbyDrivers(-74.0060, 40.7128, 3.0, 0).collectList().block()
        );
        assertEquals("limit must be greater than 0", limitException.getMessage());
    }


}
