package com.example.swifttransport.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static com.example.swifttransport.util.CustomMessages.VALIDATION_DRIVER_ID_REQUIRED;
import static com.example.swifttransport.util.CustomMessages.VALIDATION_VEHICLE_ID_REQUIRED;

@Builder
public record AssignVehicleRequest(
    @NotNull(message = VALIDATION_DRIVER_ID_REQUIRED)
    Long driverId,

    @NotNull(message = VALIDATION_VEHICLE_ID_REQUIRED)
    Long vehicleId
) {}
