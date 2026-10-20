package com.yuva.geopulse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationEvent {
    private String driverId;
    private double latitude;
    private double longitude;
    private long timestamp;

}
