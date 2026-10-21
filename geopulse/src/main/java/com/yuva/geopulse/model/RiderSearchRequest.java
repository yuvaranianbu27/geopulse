package com.yuva.geopulse.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderSearchRequest {
    @NotNull(message = "riderId is required")
    private String riderId;

    @NotNull(message = "latitude is required")
    @DecimalMin(value = "-90.0", message = "latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "latitude must be <= 90")
    private Double latitude = 0.0;

    @NotNull(message = "longitude is required")
    @DecimalMin(value = "-180.0", message = "longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "longitude must be <= 180")
    private Double longitude = 0.0;

    @NotNull(message = "radiusKm is required")
    @DecimalMin(value = "0.1", message = "radiusKm must be greater than 0")
    private Double radiusKm = 3.0;

    @Min(value = 1, message = "limit must be at least 1")
    @Max(value = 50, message = "limit must be at most 50")
    private Integer limit = 5;
}
