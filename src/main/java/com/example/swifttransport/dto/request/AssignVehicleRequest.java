package com.example.swifttransport.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssignVehicleRequest(
    @NotNull(message = "Driver ID is required")
    Long driverId,

    @NotNull(message = "Vehicle ID is required")
    Long vehicleId
) {}
