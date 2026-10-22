package com.yuva.geopulse.service;


import com.yuva.geopulse.model.DriverLocationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LocationConsumerService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final String geoKey;

    public LocationConsumerService(ReactiveStringRedisTemplate redisTemplate,
                                  @Value("${app.geo.redis-key:drivers:geo}") String geoKey) {
        this.redisTemplate = redisTemplate;
        this.geoKey = geoKey;
    }

    @KafkaListener(topics = "${app.kafka.topic:driver-locations}", groupId = "${spring.kafka.consumer.group-id:geopulse-group}")
    public void consume(DriverLocationEvent event) {
        if (event == null || event.getDriverId() == null || event.getDriverId().isBlank()) {
            return;
        }

        Point point = new Point(event.getLongitude(), event.getLatitude());

        redisTemplate.opsForGeo()
                .add(geoKey, point, event.getDriverId())
                .subscribe();
    }
}
