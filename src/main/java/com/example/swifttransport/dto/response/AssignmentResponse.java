package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Response DTO for Assignment details.
 */
@Builder
public record AssignmentResponse(
    Long id,
    DriverSummaryResponse driver,
    VehicleSummaryResponse vehicle,
    LocalDateTime assignedAt,
    LocalDateTime unassignedAt,
    String assignedBy,
    Boolean isActive
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
