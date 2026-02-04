package com.example.swifttransport.service;

import com.example.swifttransport.dto.request.CreateDriverRequest;
import com.example.swifttransport.dto.request.UpdateDriverRequest;
import com.example.swifttransport.dto.response.DriverListResponse;
import com.example.swifttransport.dto.response.DriverResponse;
import com.example.swifttransport.enums.DriverStatus;
import org.springframework.data.domain.Pageable;

public interface DriverServiceInterface {
    /**
     * Creates a new driver based on the provided request.
     *
     * @param request The request containing driver details.
     * @return The created driver details.
     */
    DriverResponse createDriver(CreateDriverRequest request);

    /**
     * Retrieves a paginated list of drivers, with optional filtering by status, search term, and inclusion of deleted drivers.
     *
     * @param pageable       Pagination information.
     * @param status         Optional status to filter drivers.
     * @param search         Optional search term to filter drivers by name or license number.
     * @param includeDeleted If true, deleted drivers are included in the results.
     * @return A paginated list of drivers.
     */
    DriverListResponse getDrivers(Pageable pageable, DriverStatus status, String search, boolean includeDeleted);

    /**
     * Retrieves a driver by its ID.
     *
     * @param id The ID of the driver to retrieve.
     * @return The driver details.
     */
    DriverResponse getDriverById(Long id);

    /**
     * Updates the details of an existing driver.
     *
     * @param id      The ID of the driver to update.
     * @param request The request containing updated driver details.
     * @return The updated driver details.
     */
    DriverResponse updateDriver(Long id, UpdateDriverRequest request);

    /**
     * Deletes a driver from the system by its ID.
     *
     * @param id The ID of the driver to delete.
     */
    void deleteDriver(Long id);
}
