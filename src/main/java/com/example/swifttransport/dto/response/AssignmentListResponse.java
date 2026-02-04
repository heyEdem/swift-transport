package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * Response DTO for paginated list of Assignments.
 */
@Builder
public record AssignmentListResponse(
    List<AssignmentResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
