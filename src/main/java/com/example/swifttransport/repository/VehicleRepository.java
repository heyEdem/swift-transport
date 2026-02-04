package com.example.swifttransport.repository;

import com.example.swifttransport.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    Page<Vehicle> findByRegistrationNumberContainingOrMakeContainingOrModelContaining(
            String registrationNumber, String make, String model, Pageable pageable);

    Page<Vehicle> findAllByActiveTrue(Pageable pageable);
}
