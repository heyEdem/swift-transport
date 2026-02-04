package com.example.swifttransport.dto.request;

import com.example.swifttransport.enums.DriverStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateDriverRequest(
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    String fullName,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    DriverStatus status
) {}
