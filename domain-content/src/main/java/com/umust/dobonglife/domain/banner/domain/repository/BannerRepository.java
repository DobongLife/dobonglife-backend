package com.umust.dobonglife.domain.banner.domain.repository;

import com.umust.dobonglife.domain.banner.domain.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByIsActiveTrueOrderByPriorityAsc();
}
