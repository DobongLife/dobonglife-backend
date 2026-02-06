package com.umust.dobonglife.domain.app.domain.repository;

import com.umust.dobonglife.domain.app.domain.constant.PolicyType;
import com.umust.dobonglife.domain.app.domain.entity.AppPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppPolicyRepository extends JpaRepository<AppPolicy, Long> {
    Optional<AppPolicy> findFirstByPolicyTypeAndIsActiveTrueOrderByCreatedAtDesc(PolicyType type);
    List<AppPolicy> findAllByIsActiveTrue();
}
