package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * Response DTO for paginated list of Drivers.
 */
@Builder
public record DriverListResponse(
    List<DriverResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
