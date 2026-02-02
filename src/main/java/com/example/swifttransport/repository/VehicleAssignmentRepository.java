package com.example.swifttransport.repository;

import com.example.swifttransport.entity.VehicleAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleAssignmentRepository extends JpaRepository<VehicleAssignment, Long> {

    Optional<VehicleAssignment> findByDriverIdAndIsActiveTrue(Long driverId);

    Optional<VehicleAssignment> findByVehicleIdAndIsActiveTrue(Long vehicleId);

    boolean existsByDriverIdAndIsActiveTrue(Long driverId);

    boolean existsByVehicleIdAndIsActiveTrue(Long vehicleId);

    Page<VehicleAssignment> findAllByIsActiveTrue(Pageable pageable);

    Page<VehicleAssignment> findAllByDriverId(Long driverId, Pageable pageable);

    Page<VehicleAssignment> findAllByVehicleId(Long vehicleId, Pageable pageable);

    @Query("SELECT va FROM VehicleAssignment va WHERE va.isActive = true AND va.driver.id = :driverId")
    Optional<VehicleAssignment> findActiveAssignmentByDriver(@Param("driverId") Long driverId);

    @Query("SELECT va FROM VehicleAssignment va WHERE va.isActive = true AND va.vehicle.id = :vehicleId")
    Optional<VehicleAssignment> findActiveAssignmentByVehicle(@Param("vehicleId") Long vehicleId);
}
