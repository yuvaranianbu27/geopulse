package com.yuva.geopulse.service;

import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MatchingService {

    private final ReactiveStringRedisTemplate redisTemplate;

    private final String geoKey;

    public MatchingService(ReactiveStringRedisTemplate redisTemplate,
                          @org.springframework.beans.factory.annotation.Value("${app.geo.redis-key:drivers:geo}") String geoKey) {
        this.redisTemplate = redisTemplate;
        this.geoKey = geoKey;
    }

    public Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>> findNearbyDrivers(double longitude, double latitude, double radiusKm, int limit) {
        if (radiusKm <= 0) {
            return Flux.error(new IllegalArgumentException("radiusKm must be greater than 0"));
        }

        if (limit <= 0) {
            return Flux.error(new IllegalArgumentException("limit must be greater than 0"));
        }

        Circle circle = new Circle(new Point(longitude, latitude), new Distance(radiusKm, Metrics.KILOMETERS));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(limit);

        return redisTemplate.opsForGeo()
                .radius(geoKey, circle, args);
    }
}
