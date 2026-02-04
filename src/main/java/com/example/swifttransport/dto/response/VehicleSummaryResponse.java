package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.io.Serializable;

/**
 * Summary Response DTO for Vehicle.
 */
@Builder
public record VehicleSummaryResponse(
    Long id,
    String registrationNumber
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
