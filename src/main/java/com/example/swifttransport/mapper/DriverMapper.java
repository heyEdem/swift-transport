package com.example.swifttransport.mapper;

import com.example.swifttransport.dto.request.CreateDriverRequest;
import com.example.swifttransport.dto.request.UpdateDriverRequest;
import com.example.swifttransport.dto.response.DriverResponse;
import com.example.swifttransport.dto.response.VehicleSummaryResponse;
import com.example.swifttransport.entity.Driver;
import com.example.swifttransport.entity.VehicleAssignment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Driver toEntity(CreateDriverRequest request);

    @Mapping(target = "currentVehicle", source = "activeAssignment", qualifiedByName = "mapCurrentVehicle")
    DriverResponse toResponse(Driver driver);

    List<DriverResponse> toResponseList(List<Driver> drivers);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "licenseNumber", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateDriverRequest request, @MappingTarget Driver driver);

    @Named("mapCurrentVehicle")
    default VehicleSummaryResponse mapCurrentVehicle(VehicleAssignment assignment) {
        if (assignment == null || assignment.getVehicle() == null) {
            return null;
        }
        return VehicleSummaryResponse.builder()
                .id(assignment.getVehicle().getId())
                .registrationNumber(assignment.getVehicle().getRegistrationNumber())
                .build();
    }
}
