package com.example.swifttransport.dto.response;

import lombok.Builder;

@Builder
public record VehicleSummaryResponse(
    Long id,
    String registrationNumber
) {}
