package com.umust.dobonglife.domain.business.domain.repository;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Long> {
}
