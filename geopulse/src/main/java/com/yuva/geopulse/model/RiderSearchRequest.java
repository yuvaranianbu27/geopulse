package com.yuva.geopulse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderSearchRequest {
    private String riderId;
    private double latitude;
    private double longitude;
    private double radiusKm;
}
