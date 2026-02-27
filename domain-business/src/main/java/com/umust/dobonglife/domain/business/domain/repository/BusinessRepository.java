package com.umust.dobonglife.domain.business.domain.repository;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    boolean existsByUserId(@Param("userId") Long userId);
}
