package com.example.swifttransport.dto.request;

import static com.example.swifttransport.util.CustomMessages.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
    @NotBlank(message = VALIDATION_USERNAME_REQUIRED)
    String username,

    @NotBlank(message = VALIDATION_PASSWORD_REQUIRED)
    String password
) {}
