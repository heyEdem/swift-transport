package com.example.swifttransport.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AssignmentListResponse(
    List<AssignmentResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {}
