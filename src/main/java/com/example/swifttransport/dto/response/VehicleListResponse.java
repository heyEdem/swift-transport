package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.util.List;
/**
 * Response DTO for paginated list of Vehicles.
 */
@Builder
public record VehicleListResponse(
    List<VehicleResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {}
