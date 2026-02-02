package com.example.swifttransport.service;

import com.example.swifttransport.dto.request.CreateDriverRequest;
import com.example.swifttransport.dto.request.UpdateDriverRequest;
import com.example.swifttransport.dto.response.DriverListResponse;
import com.example.swifttransport.dto.response.DriverResponse;
import com.example.swifttransport.entity.Driver;
import com.example.swifttransport.enums.DriverStatus;
import com.example.swifttransport.exception.BusinessValidationException;
import com.example.swifttransport.exception.ResourceNotFoundException;
import com.example.swifttransport.mapper.DriverMapper;
import com.example.swifttransport.repository.DriverRepository;
import com.example.swifttransport.repository.VehicleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleAssignmentRepository assignmentRepository;
    private final DriverMapper driverMapper;

    @Transactional
    public DriverResponse createDriver(CreateDriverRequest request) {
        if (driverRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new BusinessValidationException(
                "A driver with this license number already exists"
            );
        }

        Driver driver = driverMapper.toEntity(request);
        Driver saved = driverRepository.save(driver);
        return driverMapper.toResponse(saved);
    }

    public DriverListResponse getDrivers(Pageable pageable,
                                         DriverStatus status,
                                         String search,
                                         boolean includeDeleted) {
        Page<Driver> driverPage;

        if (search != null && !search.isBlank()) {
            if (status != null) {
                driverPage = driverRepository.searchDriversByStatus(search, status, pageable);
            } else {
                driverPage = driverRepository.searchDrivers(search, pageable);
            }
        } else if (status != null) {
            driverPage = driverRepository.findAllByStatusAndDeletedFalse(status, pageable);
        } else if (includeDeleted) {
            driverPage = driverRepository.findAll(pageable);
        } else {
            driverPage = driverRepository.findAllByDeletedFalse(pageable);
        }

        return DriverListResponse.builder()
                .content(driverMapper.toResponseList(driverPage.getContent()))
                .page(driverPage.getNumber())
                .size(driverPage.getSize())
                .totalElements(driverPage.getTotalElements())
                .totalPages(driverPage.getTotalPages())
                .last(driverPage.isLast())
                .build();
    }

    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", id));
        return driverMapper.toResponse(driver);
    }

    @Transactional
    public DriverResponse updateDriver(Long id, UpdateDriverRequest request) {
        Driver driver = driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", id));

        driverMapper.updateEntityFromRequest(request, driver);
        Driver updated = driverRepository.save(driver);
        return driverMapper.toResponse(updated);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", id));

        if (assignmentRepository.existsByDriverIdAndIsActiveTrue(id)) {
            throw new BusinessValidationException(
                "Cannot delete driver with active vehicle assignment. Please unassign vehicle first."
            );
        }

        driver.setDeleted(true);
        driverRepository.save(driver);
    }
}
