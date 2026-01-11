package com.umust.dobonglife.domain.banners.domain.repository;

import com.umust.dobonglife.domain.banners.domain.entity.Banner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findTop3ByOrderByPriorityAsc();

}
