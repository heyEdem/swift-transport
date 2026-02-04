package com.example.swifttransport.repository;

import com.example.swifttransport.entity.Driver;
import com.example.swifttransport.enums.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByIdAndDeletedFalse(Long id);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    Page<Driver> findAllByDeletedFalse(Pageable pageable);

    Page<Driver> findAllByStatusAndDeletedFalse(DriverStatus status, Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.deleted = false AND " +
           "(LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "d.phoneNumber LIKE CONCAT('%', :search, '%') OR " +
           "d.licenseNumber LIKE CONCAT('%', :search, '%'))")
    Page<Driver> searchDrivers(@Param("search") String search, Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.deleted = false AND d.status = :status AND " +
           "(LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "d.phoneNumber LIKE CONCAT('%', :search, '%') OR " +
           "d.licenseNumber LIKE CONCAT('%', :search, '%'))")
    Page<Driver> searchDriversByStatus(@Param("search") String search,
                                       @Param("status") DriverStatus status,
                                       Pageable pageable);
}
