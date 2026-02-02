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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleAssignmentRepository assignmentRepository;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverService driverService;

    private Driver driver;
    private DriverResponse driverResponse;

    @BeforeEach
    void setUp() {
        driver = Driver.builder()
                .id(1L)
                .fullName("Kwame Mensah")
                .phoneNumber("+233244111111")
                .licenseNumber("DL001234567")
                .status(DriverStatus.ACTIVE)
                .deleted(false)
                .build();

        driverResponse = DriverResponse.builder()
                .id(1L)
                .fullName("Kwame Mensah")
                .phoneNumber("+233244111111")
                .licenseNumber("DL001234567")
                .status(DriverStatus.ACTIVE)
                .build();
    }

    @Test
    void createDriver_Success() {
        CreateDriverRequest request = CreateDriverRequest.builder()
                .fullName("Kwame Mensah")
                .phoneNumber("+233244111111")
                .licenseNumber("DL001234567")
                .status(DriverStatus.ACTIVE)
                .build();

        when(driverRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(driverMapper.toEntity(any(CreateDriverRequest.class))).thenReturn(driver);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);
        when(driverMapper.toResponse(any(Driver.class))).thenReturn(driverResponse);

        DriverResponse result = driverService.createDriver(request);

        assertThat(result).isNotNull();
        assertThat(result.fullName()).isEqualTo("Kwame Mensah");
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void createDriver_DuplicateLicense_ThrowsException() {
        CreateDriverRequest request = CreateDriverRequest.builder()
                .licenseNumber("DL001234567")
                .build();

        when(driverRepository.existsByLicenseNumber("DL001234567")).thenReturn(true);

        assertThatThrownBy(() -> driverService.createDriver(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("license number already exists");
    }

    @Test
    void getDriverById_Success() {
        when(driverRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(driver));
        when(driverMapper.toResponse(driver)).thenReturn(driverResponse);

        DriverResponse result = driverService.getDriverById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getDriverById_NotFound_ThrowsException() {
        when(driverRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverService.getDriverById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Driver not found");
    }

    @Test
    void getDrivers_ReturnsPaginatedList() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Driver> driverPage = new PageImpl<>(Collections.singletonList(driver));

        when(driverRepository.findAllByDeletedFalse(pageable)).thenReturn(driverPage);
        when(driverMapper.toResponseList(anyList())).thenReturn(Collections.singletonList(driverResponse));

        DriverListResponse result = driverService.getDrivers(pageable, null, null, false);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void deleteDriver_Success() {
        when(driverRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(driver));
        when(assignmentRepository.existsByDriverIdAndIsActiveTrue(1L)).thenReturn(false);

        driverService.deleteDriver(1L);

        verify(driverRepository).save(any(Driver.class));
        assertThat(driver.getDeleted()).isTrue();
    }

    @Test
    void deleteDriver_WithActiveAssignment_ThrowsException() {
        when(driverRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(driver));
        when(assignmentRepository.existsByDriverIdAndIsActiveTrue(1L)).thenReturn(true);

        assertThatThrownBy(() -> driverService.deleteDriver(1L))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("active vehicle assignment");
    }
}
