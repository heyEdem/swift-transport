package com.example.swifttransport.mapper;

import com.example.swifttransport.dto.response.AssignmentResponse;
import com.example.swifttransport.dto.response.DriverSummaryResponse;
import com.example.swifttransport.dto.response.VehicleSummaryResponse;
import com.example.swifttransport.entity.VehicleAssignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleAssignmentMapper {

    @Mapping(target = "assignedBy", source = "assignedBy.username")
    AssignmentResponse toResponse(VehicleAssignment assignment);

    List<AssignmentResponse> toResponseList(List<VehicleAssignment> assignments);

    default DriverSummaryResponse mapDriver(VehicleAssignment assignment) {
        if (assignment == null || assignment.getDriver() == null) {
            return null;
        }
        return DriverSummaryResponse.builder()
                .id(assignment.getDriver().getId())
                .fullName(assignment.getDriver().getFullName())
                .licenseNumber(assignment.getDriver().getLicenseNumber())
                .build();
    }

    default VehicleSummaryResponse mapVehicle(VehicleAssignment assignment) {
        if (assignment == null || assignment.getVehicle() == null) {
            return null;
        }
        return VehicleSummaryResponse.builder()
                .id(assignment.getVehicle().getId())
                .registrationNumber(assignment.getVehicle().getRegistrationNumber())
                .build();
    }
}
