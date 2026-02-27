package com.umust.dobonglife.domain.business.domain.repository;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    boolean existsByUserId(@Param("userId") Long userId);

    Optional<Business> findByUserId(Long userId);

    @Query(value = "SELECT p.category FROM businesses b JOIN places p ON b.place_id = p.place_id WHERE b.user_id = :userId", nativeQuery = true)
    Optional<String> findCategoryByUserId(@Param("userId") Long userId);
}
