package com.example.swifttransport.dto.request;

import com.example.swifttransport.enums.DriverStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateDriverRequest(
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    String firstName,

    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    String lastName,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    DriverStatus status
) {}
