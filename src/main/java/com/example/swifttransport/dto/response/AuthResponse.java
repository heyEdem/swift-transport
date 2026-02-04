package com.example.swifttransport.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
    String token,
    String type,
    String username,
    String role,
    Long expiresIn
) {}
