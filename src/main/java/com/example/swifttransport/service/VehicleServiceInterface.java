package com.example.swifttransport.service;

import com.example.swifttransport.dto.request.CreateVehicleRequest;
import com.example.swifttransport.dto.request.UpdateVehicleRequest;
import com.example.swifttransport.dto.response.VehicleListResponse;
import com.example.swifttransport.dto.response.VehicleResponse;
import org.springframework.data.domain.Pageable;

public interface VehicleServiceInterface {

    /**
     * Creates a new vehicle based on the provided request.
     *
     * @param createVehicleRequest The request containing vehicle details.
     * @return The created vehicle details.
     */
    VehicleResponse createVehicle(CreateVehicleRequest createVehicleRequest);

    /**
     * Retrieves a paginated list of vehicles, with optional filtering by active status and search term.
     *
     * @param pageable   Pagination information.
     * @param activeOnly If true, only active vehicles are returned.
     * @param search     Optional search term to filter vehicles by make or model.
     * @return A paginated list of vehicles.
     */
    VehicleListResponse getVehicles (Pageable pageable, Boolean activeOnly, String search);

    /**
     * Retrieves a vehicle by its ID.
     *
     * @param id The ID of the vehicle to retrieve.
     * @return The vehicle details.
     */
    VehicleResponse getVehicleById(Long id);

    /**
     * Updates the details of an existing vehicle.
     *
     * @param id      The ID of the vehicle to update.
     * @param request The request containing updated vehicle details.
     * @return The updated vehicle details.
     */
    VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request);

    /**
     * Deletes a vehicle from the system by its ID.
     *
     * @param id The ID of the account to delete.
     */
    void deleteVehicle(Long id);
}
