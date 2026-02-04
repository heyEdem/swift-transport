package com.example.swifttransport.service;

import com.example.swifttransport.config.RedisConfig;
import com.example.swifttransport.dto.request.CreateVehicleRequest;
import com.example.swifttransport.dto.request.UpdateVehicleRequest;
import com.example.swifttransport.dto.response.DriverSummaryResponse;
import com.example.swifttransport.dto.response.VehicleListResponse;
import com.example.swifttransport.dto.response.VehicleResponse;
import com.example.swifttransport.entity.Vehicle;
import com.example.swifttransport.entity.VehicleAssignment;
import com.example.swifttransport.exception.BusinessValidationException;
import com.example.swifttransport.exception.ResourceNotFoundException;
import com.example.swifttransport.repository.VehicleAssignmentRepository;
import com.example.swifttransport.repository.VehicleRepository;
import com.example.swifttransport.util.CustomMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService implements VehicleServiceInterface{

    private final VehicleRepository vehicleRepository;
    private final VehicleAssignmentRepository assignmentRepository;

    /**
     {@inheritDoc}
     */
    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_VEHICLES, allEntries = true)
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        log.debug("Creating new vehicle with registration: {}", request.registrationNumber());
        
        if (vehicleRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new BusinessValidationException(CustomMessages.VALIDATION_REGISTRATION_NUMBER_EXISTS);
        }

        Vehicle vehicle = Vehicle.builder()
                .registrationNumber(request.registrationNumber())
                .make(request.make())
                .model(request.model())
                .year(request.year())
                .active(request.active())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return toVehicleResponse(saved);
    }

    /**
     {@inheritDoc}
     */
    @Cacheable(
        value = RedisConfig.CACHE_VEHICLES,
        key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize + ':active:' + #activeOnly + ':search:' + #search"
    )
    public VehicleListResponse getVehicles(Pageable pageable, Boolean activeOnly, String search) {
        log.debug("Fetching vehicles from database - cache miss for page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Vehicle> vehiclePage;

        if (search != null && !search.isBlank()) {
            // Search by registration number, make, or model
            vehiclePage = vehicleRepository.findByRegistrationNumberContainingOrMakeContainingOrModelContaining(
                    search, search, search, pageable);
        } else if (activeOnly != null && activeOnly) {
            vehiclePage = vehicleRepository.findAllByActiveTrue(pageable);
        } else {
            vehiclePage = vehicleRepository.findAll(pageable);
        }

        return VehicleListResponse.builder()
                .content(vehiclePage.getContent().stream()
                        .map(this::toVehicleResponse)
                        .toList())
                .page(vehiclePage.getNumber())
                .size(vehiclePage.getSize())
                .totalElements(vehiclePage.getTotalElements())
                .totalPages(vehiclePage.getTotalPages())
                .last(vehiclePage.isLast())
                .build();
    }

    /**
     {@inheritDoc}
     */
    @Cacheable(value = RedisConfig.CACHE_VEHICLE_BY_ID, key = "#id")
    public VehicleResponse getVehicleById(Long id) {
        log.debug("Fetching vehicle {} from database - cache miss", id);
        
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CustomMessages.VALIDATION_VEHICLE_NOT_FOUND));
        return toVehicleResponse(vehicle);
    }

    /**
     {@inheritDoc}
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_VEHICLES, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_VEHICLE_BY_ID, key = "#id")
    })
    public VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request) {
        log.debug("Updating vehicle {}", id);
        
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CustomMessages.VALIDATION_VEHICLE_NOT_FOUND));

        if (request.make() != null) {
            vehicle.setMake(request.make());
        }
        if (request.model() != null) {
            vehicle.setModel(request.model());
        }
        if (request.year() != null) {
            vehicle.setYear(request.year());
        }
        if (request.active() != null) {
            vehicle.setActive(request.active());
        }

        Vehicle updated = vehicleRepository.save(vehicle);
        return toVehicleResponse(updated);
    }

    /**
     {@inheritDoc}
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_VEHICLES, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_VEHICLE_BY_ID, key = "#id")
    })
    public void deleteVehicle(Long id) {
        log.debug("Deleting vehicle {}", id);
        
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CustomMessages.VALIDATION_VEHICLE_NOT_FOUND));

        if (assignmentRepository.existsByVehicleIdAndIsActiveTrue(id)) {
            throw new BusinessValidationException(CustomMessages.VALIDATION_CANNOT_DELETE_ASSIGNED_VEHICLE);
        }

        vehicleRepository.delete(vehicle);
    }

    private VehicleResponse toVehicleResponse(Vehicle vehicle) {
        VehicleAssignment activeAssignment = assignmentRepository
                .findByVehicleIdAndIsActiveTrue(vehicle.getId())
                .orElse(null);

        DriverSummaryResponse currentDriver = null;
        if (activeAssignment != null && activeAssignment.getDriver() != null) {
            var driver = activeAssignment.getDriver();
            currentDriver = DriverSummaryResponse.builder()
                    .id(driver.getId())
                    .fullName(driver.getFullName())
                    .licenseNumber(driver.getLicenseNumber())
                    .build();
        }

        return VehicleResponse.builder()
                .id(vehicle.getId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .active(vehicle.getActive())
                .currentDriver(currentDriver)
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
