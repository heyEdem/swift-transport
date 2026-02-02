package com.example.swifttransport.service;

import com.example.swifttransport.dto.request.AssignVehicleRequest;
import com.example.swifttransport.dto.response.AssignmentResponse;
import com.example.swifttransport.entity.Driver;
import com.example.swifttransport.entity.User;
import com.example.swifttransport.entity.Vehicle;
import com.example.swifttransport.entity.VehicleAssignment;
import com.example.swifttransport.enums.DriverStatus;
import com.example.swifttransport.enums.UserRole;
import com.example.swifttransport.exception.BusinessValidationException;
import com.example.swifttransport.exception.ResourceNotFoundException;
import com.example.swifttransport.mapper.VehicleAssignmentMapper;
import com.example.swifttransport.repository.DriverRepository;
import com.example.swifttransport.repository.UserRepository;
import com.example.swifttransport.repository.VehicleAssignmentRepository;
import com.example.swifttransport.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleAssignmentServiceTest {

    @Mock
    private VehicleAssignmentRepository assignmentRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleAssignmentMapper assignmentMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VehicleAssignmentService assignmentService;

    private Driver driver;
    private Vehicle vehicle;
    private User user;
    private VehicleAssignment assignment;
    private AssignmentResponse assignmentResponse;

    @BeforeEach
    void setUp() {
        driver = Driver.builder()
                .id(1L)
                .fullName("Kwame Mensah")
                .status(DriverStatus.ACTIVE)
                .deleted(false)
                .build();

        vehicle = Vehicle.builder()
                .id(1L)
                .registrationNumber("GH-1234-20")
                .active(true)
                .build();

        user = User.builder()
                .id(1L)
                .username("admin@swift.com")
                .role(UserRole.ADMIN)
                .build();

        assignment = VehicleAssignment.builder()
                .id(1L)
                .driver(driver)
                .vehicle(vehicle)
                .assignedBy(user)
                .isActive(true)
                .build();

        assignmentResponse = AssignmentResponse.builder()
                .id(1L)
                .isActive(true)
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void assignVehicle_Success() {
        AssignVehicleRequest request = new AssignVehicleRequest(1L, 1L);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@swift.com");
        when(driverRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(assignmentRepository.existsByDriverIdAndIsActiveTrue(1L)).thenReturn(false);
        when(assignmentRepository.existsByVehicleIdAndIsActiveTrue(1L)).thenReturn(false);
        when(userRepository.findByUsername("admin@swift.com")).thenReturn(Optional.of(user));
        when(assignmentRepository.save(any(VehicleAssignment.class))).thenReturn(assignment);
        when(assignmentMapper.toResponse(any(VehicleAssignment.class))).thenReturn(assignmentResponse);

        AssignmentResponse result = assignmentService.assignVehicle(request);

        assertThat(result).isNotNull();
        verify(assignmentRepository).save(any(VehicleAssignment.class));
    }

    @Test
    void assignVehicle_DriverNotActive_ThrowsException() {
        driver.setStatus(DriverStatus.SUSPENDED);
        AssignVehicleRequest request = new AssignVehicleRequest(1L, 1L);

        when(driverRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> assignmentService.assignVehicle(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("Driver must be ACTIVE");
    }

    @Test
    void assignVehicle_DriverAlreadyAssigned_ThrowsException() {
        AssignVehicleRequest request = new AssignVehicleRequest(1L, 1L);

        when(driverRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(driver));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(assignmentRepository.existsByDriverIdAndIsActiveTrue(1L)).thenReturn(true);

        assertThatThrownBy(() -> assignmentService.assignVehicle(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("already has an active vehicle assignment");
    }

    @Test
    void unassignVehicle_Success() {
        when(assignmentRepository.findByDriverIdAndIsActiveTrue(1L)).thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(VehicleAssignment.class))).thenReturn(assignment);
        when(assignmentMapper.toResponse(any(VehicleAssignment.class))).thenReturn(assignmentResponse);

        AssignmentResponse result = assignmentService.unassignVehicle(1L);

        assertThat(result).isNotNull();
        assertThat(assignment.getIsActive()).isFalse();
    }

    @Test
    void unassignVehicle_NoActiveAssignment_ThrowsException() {
        when(assignmentRepository.findByDriverIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.unassignVehicle(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No active assignment found");
    }
}
