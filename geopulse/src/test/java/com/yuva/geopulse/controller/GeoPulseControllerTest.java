package com.yuva.geopulse.controller;

import com.yuva.geopulse.model.DriverLocationEvent;
import com.yuva.geopulse.model.NearbyDriverResponse;
import com.yuva.geopulse.model.RiderSearchRequest;
import com.yuva.geopulse.service.LocationProducerService;
import com.yuva.geopulse.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeoPulseControllerTest {

    @Test
    void ingestDriverPingShouldReturnAcceptedResponse() {
        LocationProducerService producerService = mock(LocationProducerService.class);
        MatchingService matchingService = mock(MatchingService.class);
        GeoPulseController controller = new GeoPulseController(producerService, matchingService);

        DriverLocationEvent event = new DriverLocationEvent("driver-42", 40.7128, -74.0060, 1720000000000L);
        when(producerService.publishDriverLocation(any(DriverLocationEvent.class))).thenReturn(Mono.empty());

        ResponseEntity<Map<String, Object>> response = controller.ingestDriverPing(event).block();

        assertNotNull(response);
        assertEquals(202, response.getStatusCode().value());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("queued", body.get("status"));
        assertEquals("driver-42", body.get("driverId"));
    }

    @Test
    void matchRiderShouldReturnNearbyDrivers() {
        LocationProducerService producerService = mock(LocationProducerService.class);
        MatchingService matchingService = mock(MatchingService.class);
        GeoPulseController controller = new GeoPulseController(producerService, matchingService);

        RedisGeoCommands.GeoLocation<String> driverLocation = new RedisGeoCommands.GeoLocation<>("driver-42", new Point(-74.0060, 40.7128));
        GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult = new GeoResult<>(driverLocation, new Distance(1.2, Metrics.KILOMETERS));

        when(matchingService.findNearbyDrivers(-74.0060, 40.7128, 3.0, 5)).thenReturn(Flux.just(geoResult));

        RiderSearchRequest request = new RiderSearchRequest();
        request.setRiderId("rider-7");
        request.setLatitude(40.7128);
        request.setLongitude(-74.0060);
        request.setRadiusKm(3.0);
        request.setLimit(5);

        java.util.List<NearbyDriverResponse> matchesList = controller.matchRider(request).collectList().block();

        assertNotNull(matchesList);
        NearbyDriverResponse[] matches = matchesList.toArray(new NearbyDriverResponse[matchesList.size()]);

        assertEquals(1, matches.length);
        assertEquals("driver-42", matches[0].driverId());
        assertEquals(1.2, matches[0].distanceKm(), 0.01);
    }
}
