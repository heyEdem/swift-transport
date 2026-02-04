package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Response DTO for Vehicle details.
 */
@Builder
public record VehicleResponse(
    Long id,
    String registrationNumber,
    String make,
    String model,
    Integer year,
    Boolean active,
    DriverSummaryResponse currentDriver,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
