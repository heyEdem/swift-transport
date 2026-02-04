package com.example.swifttransport.dto.response;

import com.example.swifttransport.enums.DriverStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DriverResponse(
    Long id,
    String fullName,
    String phoneNumber,
    String licenseNumber,
    DriverStatus status,
    VehicleSummaryResponse currentVehicle,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
