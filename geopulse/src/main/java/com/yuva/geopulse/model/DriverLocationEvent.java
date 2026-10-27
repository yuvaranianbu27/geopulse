package com.yuva.geopulse.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationEvent {
    @Schema(description = "Unique driver identifier", example = "driver-42")
    @NotBlank(message = "driverId is required")
    private String driverId;

    @Schema(description = "Driver latitude in decimal degrees", example = "12.9716")
    @NotNull(message = "latitude is required")
    @DecimalMin(value = "-90.0", message = "latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "latitude must be <= 90")
    private Double latitude;

    @Schema(description = "Driver longitude in decimal degrees", example = "77.5946")
    @NotNull(message = "longitude is required")
    @DecimalMin(value = "-180.0", message = "longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "longitude must be <= 180")
    private Double longitude;

    @Schema(description = "Location timestamp in epoch milliseconds", example = "1730000000000")
    private long timestamp;

    public long getTimestamp() {
        return timestamp == 0 ? System.currentTimeMillis() : timestamp;
    }
}
