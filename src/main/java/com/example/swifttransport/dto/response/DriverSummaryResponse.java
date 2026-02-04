package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.io.Serializable;

/**
 * Summary Response DTO for Driver.
 */
@Builder
public record DriverSummaryResponse(
    Long id,
    String firstName,
    String lastName,
    String licenseNumber
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
