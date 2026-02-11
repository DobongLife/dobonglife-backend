package com.umust.dobonglife.domain.business.domain.repository;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByUserId(Long userId);
    @Modifying
    @Query("UPDATE Business b SET b.place = null WHERE b.id = :businessId")
    void nullifyPlaceById(@Param("businessId") Long businessId);
}
