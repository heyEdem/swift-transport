package com.example.swifttransport.dto.response;

import lombok.Builder;

@Builder
public record DriverSummaryResponse(
    Long id,
    String fullName,
    String licenseNumber
) {}
