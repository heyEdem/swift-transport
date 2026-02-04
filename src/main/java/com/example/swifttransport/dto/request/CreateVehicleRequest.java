package com.example.swifttransport.dto.request;

import com.example.swifttransport.util.CustomMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Request DTO for creating a new vehicle.
 */
@Builder
public record CreateVehicleRequest(
    @NotBlank(message = CustomMessages.VALIDATION_REGISTRATION_NUMBER_REQUIRED)
    @Size(max = 50, message = CustomMessages.VALIDATION_REGISTRATION_NUMBER_SIZE)
    String registrationNumber,

    @Size(max = 100, message = CustomMessages.VALIDATION_MAKE_SIZE)
    String make,

    @Size(max = 100, message = CustomMessages.VALIDATION_MODEL_SIZE)
    String model,

    Integer year,

    @NotNull(message = CustomMessages.VALIDATION_ACTIVE_REQUIRED)
    Boolean active
) {}
