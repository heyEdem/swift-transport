package com.example.swifttransport.util;

public class CustomMessages {
    public static final String DRIVER_NOT_FOUND = "Driver not found";

    // Logger messages for GlobalExceptionHandler
    public static final String LOG_RESOURCE_NOT_FOUND = "Resource not found: {}";
    public static final String LOG_BUSINESS_VALIDATION_FAILED = "Business validation failed: {}";
    public static final String LOG_ACCESS_DENIED = "Access denied: {}";
    public static final String LOG_UNEXPECTED_ERROR = "Unexpected error occurred";

    // Rate limiting
    public static final String LOG_RATE_LIMIT_EXCEEDED = "Rate limit exceeded for IP {} on {}";

    // Logger messages for JWT
    public static final String LOG_JWT_TOKEN_EXPIRED = "JWT token is expired: {}";
    public static final String LOG_JWT_TOKEN_UNSUPPORTED = "JWT token is unsupported: {}";
    public static final String LOG_JWT_TOKEN_MALFORMED = "JWT token is malformed: {}";
    public static final String LOG_JWT_SIGNATURE_VALIDATION_FAILED = "JWT signature validation failed: {}";
    public static final String LOG_JWT_TOKEN_EMPTY = "JWT token is empty or null: {}";
    public static final String LOG_JWT_AUTH_CONTEXT_ERROR = "Could not set user authentication in security context";
    public static final String LOG_UNAUTHORIZED_ERROR = "Unauthorized error: {}";

    // Validation messages for CreateDriverRequest
    public static final String VALIDATION_FIRST_NAME_REQUIRED = "First name is required";
    public static final String VALIDATION_FIRST_NAME_SIZE = "First name must be between 2 and 100 characters";
    public static final String VALIDATION_LAST_NAME_REQUIRED = "Last name is required";
    public static final String VALIDATION_LAST_NAME_SIZE = "Last name must be between 2 and 100 characters";
    public static final String VALIDATION_PHONE_REQUIRED = "Phone number is required";
    public static final String VALIDATION_PHONE_FORMAT = "Invalid phone number format";
    public static final String VALIDATION_LICENSE_REQUIRED = "License number is required";
    public static final String VALIDATION_LICENSE_SIZE = "License number must be between 5 and 50 characters";
    public static final String VALIDATION_STATUS_REQUIRED = "Status is required";

    // Validation messages for AssignVehicleRequest
    public static final String VALIDATION_DRIVER_ID_REQUIRED = "Driver ID is required";
    public static final String VALIDATION_VEHICLE_ID_REQUIRED = "Vehicle ID is required";

    // Validation messages for LoginRequest
    public static final String VALIDATION_USERNAME_REQUIRED = "Username is required";
    public static final String VALIDATION_PASSWORD_REQUIRED = "Password is required";

    // Business validation messages for VehicleAssignmentService
    public static final String VALIDATION_DRIVER_MUST_BE_ACTIVE = "Driver must be ACTIVE to assign vehicle";
    public static final String VALIDATION_VEHICLE_NOT_ACTIVE = "Vehicle is not active";
    public static final String VALIDATION_DRIVER_ALREADY_ASSIGNED = "Driver already has an active vehicle assignment";
    public static final String VALIDATION_VEHICLE_ALREADY_ASSIGNED = "Vehicle is already assigned to another driver";

    // Business validation messages for DriverService
    public static final String VALIDATION_LICENSE_ALREADY_EXISTS = "A driver with this license number already exists";
    public static final String VALIDATION_CANNOT_DELETE_ASSIGNED_DRIVER = "Cannot delete driver with active vehicle assignment. Please unassign vehicle first.";

    // Resource names for ResourceNotFoundException
    public static final String RESOURCE_DRIVER = "Driver";
    public static final String RESOURCE_VEHICLE = "Vehicle";

    // Resource not found message templates
    public static final String USER_NOT_FOUND_PREFIX = "User not found: ";
    public static final String NO_ACTIVE_ASSIGNMENT_PREFIX = "No active assignment found for driver with id: ";

    // Validation messages for Vehicle
    public static final String VALIDATION_REGISTRATION_NUMBER_REQUIRED = "Registration number is required";
    public static final String VALIDATION_REGISTRATION_NUMBER_SIZE = "Registration number must not exceed 50 characters";
    public static final String VALIDATION_MAKE_SIZE = "Make must not exceed 100 characters";
    public static final String VALIDATION_MODEL_SIZE = "Model must not exceed 100 characters";
    public static final String VALIDATION_ACTIVE_REQUIRED = "Active status is required";

    // Business validation messages for Vehicle
    public static final String VALIDATION_REGISTRATION_NUMBER_EXISTS = "A vehicle with this registration number already exists";
    public static final String VALIDATION_VEHICLE_NOT_FOUND = "Vehicle not found";
    public static final String VALIDATION_CANNOT_DELETE_ASSIGNED_VEHICLE = "Cannot delete vehicle with active driver assignment. Please unassign driver first.";
}
