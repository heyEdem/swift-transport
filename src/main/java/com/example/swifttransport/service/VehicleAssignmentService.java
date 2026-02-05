package com.example.swifttransport.service;

import com.example.swifttransport.config.RedisConfig;
import com.example.swifttransport.dto.request.AssignVehicleRequest;
import com.example.swifttransport.dto.response.AssignmentListResponse;
import com.example.swifttransport.dto.response.AssignmentResponse;
import com.example.swifttransport.entity.Driver;
import com.example.swifttransport.entity.User;
import com.example.swifttransport.entity.Vehicle;
import com.example.swifttransport.entity.VehicleAssignment;
import com.example.swifttransport.enums.DriverStatus;
import com.example.swifttransport.exception.BusinessValidationException;
import com.example.swifttransport.exception.ResourceNotFoundException;
import com.example.swifttransport.mapper.VehicleAssignmentMapper;
import com.example.swifttransport.repository.DriverRepository;
import com.example.swifttransport.repository.UserRepository;
import com.example.swifttransport.repository.VehicleAssignmentRepository;
import com.example.swifttransport.repository.VehicleRepository;
import static com.example.swifttransport.util.CustomMessages.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleAssignmentService {

    private final VehicleAssignmentRepository assignmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleAssignmentMapper assignmentMapper;

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_ASSIGNMENTS, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_VEHICLES, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_VEHICLE_BY_ID, key = "#request.vehicleId"),
        @CacheEvict(value = RedisConfig.CACHE_DRIVER_BY_ID, key = "#request.driverId")
    })
    public AssignmentResponse assignVehicle(AssignVehicleRequest request) {
        Driver driver = driverRepository.findByIdAndDeletedFalse(request.driverId())
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_DRIVER, request.driverId()));

        if (driver.getStatus() != DriverStatus.ACTIVE) {
            throw new BusinessValidationException(VALIDATION_DRIVER_MUST_BE_ACTIVE);
        }

        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_VEHICLE, request.vehicleId()));

        if (!vehicle.getActive()) {
            throw new BusinessValidationException(VALIDATION_VEHICLE_NOT_ACTIVE);
        }

        if (assignmentRepository.existsByDriverIdAndIsActiveTrue(request.driverId())) {
            throw new BusinessValidationException(VALIDATION_DRIVER_ALREADY_ASSIGNED);
        }

        if (assignmentRepository.existsByVehicleIdAndIsActiveTrue(request.vehicleId())) {
            throw new BusinessValidationException(VALIDATION_VEHICLE_ALREADY_ASSIGNED);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User assignedBy = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_PREFIX + username));

        VehicleAssignment assignment = VehicleAssignment.builder()
            .driver(driver)
            .vehicle(vehicle)
            .assignedBy(assignedBy)
            .isActive(true)
            .build();

        VehicleAssignment saved = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(saved);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_ASSIGNMENTS, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_VEHICLES, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_VEHICLE_BY_ID, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_DRIVER_BY_ID, key = "#driverId")
    })
    public AssignmentResponse unassignVehicle(Long driverId) {
        VehicleAssignment assignment = assignmentRepository.findByDriverIdAndIsActiveTrue(driverId)
            .orElseThrow(() -> new ResourceNotFoundException(
                NO_ACTIVE_ASSIGNMENT_PREFIX + driverId));

        assignment.setIsActive(false);
        assignment.setUnassignedAt(java.time.LocalDateTime.now());

        VehicleAssignment saved = assignmentRepository.save(assignment);
        return assignmentMapper.toResponse(saved);
    }

    @Cacheable(
        value = RedisConfig.CACHE_ASSIGNMENTS,
        key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize + ':active:' + #activeOnly + ':driver:' + #driverId + ':vehicle:' + #vehicleId"
    )
    public AssignmentListResponse getAssignments(Pageable pageable, boolean activeOnly, Long driverId, Long vehicleId) {
        Page<VehicleAssignment> assignmentPage;

        if (driverId != null) {
            assignmentPage = assignmentRepository.findAllByDriverId(driverId, pageable);
        } else if (vehicleId != null) {
            assignmentPage = assignmentRepository.findAllByVehicleId(vehicleId, pageable);
        } else if (activeOnly) {
            assignmentPage = assignmentRepository.findAllByIsActiveTrue(pageable);
        } else {
            assignmentPage = assignmentRepository.findAll(pageable);
        }

        return AssignmentListResponse.builder()
            .content(assignmentMapper.toResponseList(assignmentPage.getContent()))
            .page(assignmentPage.getNumber())
            .size(assignmentPage.getSize())
            .totalElements(assignmentPage.getTotalElements())
            .totalPages(assignmentPage.getTotalPages())
            .last(assignmentPage.isLast())
            .build();
    }
}
