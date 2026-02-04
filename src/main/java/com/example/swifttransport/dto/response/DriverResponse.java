package com.example.swifttransport.dto.response;

import com.example.swifttransport.enums.DriverStatus;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Response DTO for Driver details.
 */
@Builder
public record DriverResponse(
    Long id,
    String firstName,
    String lastName,
    String phoneNumber,
    String licenseNumber,
    DriverStatus status,
    VehicleSummaryResponse currentVehicle,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
