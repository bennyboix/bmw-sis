package com.sisregistration.bmwsis.repository;

import com.sisregistration.bmwsis.entity.EnrollmentPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EnrollmentPeriodRepository extends JpaRepository<EnrollmentPeriod, Long> {
    Optional<EnrollmentPeriod> findByIsActiveTrue();
    
    @Query("SELECT ep FROM EnrollmentPeriod ep WHERE ep.isActive = true AND CURRENT_TIMESTAMP >= ep.startDate AND CURRENT_TIMESTAMP <= ep.endDate")
    Optional<EnrollmentPeriod> findActiveEnrollmentPeriod();
} 