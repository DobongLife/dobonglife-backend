package com.umust.dobonglife.domain.banner.infrastructure.jpa;

import com.umust.dobonglife.domain.banner.domain.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerJpaRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByIsActiveTrueOrderByPriorityAsc();
}
