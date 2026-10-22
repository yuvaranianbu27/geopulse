package com.yuva.geopulse.model;

public record NearbyDriverResponse(
        String driverId,
        double longitude,
        double latitude,
        double distanceKm
) {
}
