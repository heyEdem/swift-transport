package com.example.swifttransport.dto.request;

import com.example.swifttransport.enums.DriverStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.example.swifttransport.util.CustomMessages.*;

@Builder
public record CreateDriverRequest(
    @NotBlank(message = VALIDATION_FIRST_NAME_REQUIRED)
    @Size(min = 2, max = 100, message = VALIDATION_FIRST_NAME_SIZE)
    String firstName,

    @NotBlank(message = VALIDATION_LAST_NAME_REQUIRED)
    @Size(min = 2, max = 100, message = VALIDATION_LAST_NAME_SIZE)
    String lastName,

    @NotBlank(message = VALIDATION_PHONE_REQUIRED)
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = VALIDATION_PHONE_FORMAT)
    String phoneNumber,

    @NotBlank(message = VALIDATION_LICENSE_REQUIRED)
    @Size(min = 5, max = 50, message = VALIDATION_LICENSE_SIZE)
    String licenseNumber,

    @NotNull(message = VALIDATION_STATUS_REQUIRED)
    DriverStatus status
) {}
