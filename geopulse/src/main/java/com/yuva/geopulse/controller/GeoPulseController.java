package com.yuva.geopulse.controller;

import com.yuva.geopulse.model.DriverLocationEvent;
import com.yuva.geopulse.model.NearbyDriverResponse;
import com.yuva.geopulse.model.RiderSearchRequest;
import com.yuva.geopulse.service.LocationProducerService;
import com.yuva.geopulse.service.MatchingService;
import jakarta.validation.Valid;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/geopulse")
public class GeoPulseController {

    private final LocationProducerService producerService;
    private final MatchingService matchingService;

    public GeoPulseController(LocationProducerService producerService, MatchingService matchingService) {
        this.producerService = producerService;
        this.matchingService = matchingService;
    }

    @PostMapping("/driver/ping")
    public Mono<ResponseEntity<Map<String, Object>>> ingestDriverPing(@Valid @RequestBody DriverLocationEvent event) {
        return producerService.publishDriverLocation(event)
                .thenReturn(ResponseEntity.accepted().body(Map.of(
                        "status", "queued",
                        "driverId", event.getDriverId(),
                        "timestamp", event.getTimestamp()
                )));
    }

    @GetMapping("/rider/match")
    public Flux<NearbyDriverResponse> matchRider(@Valid @ModelAttribute RiderSearchRequest request) {
        return matchingService.findNearbyDrivers(request.getLongitude(), request.getLatitude(), request.getRadiusKm(), request.getLimit())
                .map(this::toNearbyDriverResponse);
    }

    private NearbyDriverResponse toNearbyDriverResponse(GeoResult<RedisGeoCommands.GeoLocation<String>> result) {
        var content = result.getContent();
        var point = content.getPoint();

        return new NearbyDriverResponse(
                content.getName(),
                point.getX(),
                point.getY(),
                result.getDistance().getValue()
        );
    }
}
