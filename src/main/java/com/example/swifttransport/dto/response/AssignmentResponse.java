package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AssignmentResponse(
    Long id,
    DriverSummaryResponse driver,
    VehicleSummaryResponse vehicle,
    LocalDateTime assignedAt,
    LocalDateTime unassignedAt,
    String assignedBy,
    Boolean isActive
) {}
