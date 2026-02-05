package com.example.swifttransport.service;

import com.example.swifttransport.config.RedisConfig;
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
import static com.example.swifttransport.util.CustomMessages.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService implements DriverServiceInterface {

    private final DriverRepository driverRepository;
    private final VehicleAssignmentRepository assignmentRepository;
    private final DriverMapper driverMapper;

    @Transactional
    @CacheEvict(value = RedisConfig.CACHE_DRIVERS, allEntries = true)
    public DriverResponse createDriver(CreateDriverRequest request) {
        if (driverRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new BusinessValidationException(VALIDATION_LICENSE_ALREADY_EXISTS);
        }

        Driver driver = driverMapper.toEntity(request);
        Driver saved = driverRepository.save(driver);
        return driverMapper.toResponse(saved);
    }

    @Cacheable(
        value = RedisConfig.CACHE_DRIVERS,
        key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize + ':status:' + #status + ':search:' + #search + ':deleted:' + #includeDeleted"
    )
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

    @Cacheable(value = RedisConfig.CACHE_DRIVER_BY_ID, key = "#id")
    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException(DRIVER_NOT_FOUND));
        return driverMapper.toResponse(driver);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_DRIVERS, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_DRIVER_BY_ID, key = "#id")
    })
    public DriverResponse updateDriver(Long id, UpdateDriverRequest request) {
        Driver driver = driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_DRIVER, id));

        driverMapper.updateEntityFromRequest(request, driver);
        Driver updated = driverRepository.save(driver);
        return driverMapper.toResponse(updated);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = RedisConfig.CACHE_DRIVERS, allEntries = true),
        @CacheEvict(value = RedisConfig.CACHE_DRIVER_BY_ID, key = "#id")
    })
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_DRIVER, id));

        if (assignmentRepository.existsByDriverIdAndIsActiveTrue(id)) {
            throw new BusinessValidationException(VALIDATION_CANNOT_DELETE_ASSIGNED_DRIVER);
        }

        driver.setDeleted(true);
        driverRepository.save(driver);
    }
}
