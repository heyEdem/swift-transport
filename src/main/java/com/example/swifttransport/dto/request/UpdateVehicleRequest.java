package com.example.swifttransport.dto.request;

import com.example.swifttransport.util.CustomMessages;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Request DTO for updating an existing vehicle.
 */
@Builder
public record UpdateVehicleRequest(
    @Size(max = 100, message = CustomMessages.VALIDATION_MAKE_SIZE)
    String make,

    @Size(max = 100, message = CustomMessages.VALIDATION_MODEL_SIZE)
    String model,

    Integer year,

    Boolean active
) {}
