package com.umust.dobonglife.domain.business.infrastructure.jpa;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.global.common.constant.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BusinessJpaRepository extends JpaRepository<Business, Long> {

    boolean existsByUserId(@Param("userId") Long userId);

    Optional<Business> findByUserId(Long userId);

    @Query("SELECT b.category FROM Business b WHERE b.userId = :userId")
    Optional<Category> findCategoryByUserId(@Param("userId") Long userId);
}
