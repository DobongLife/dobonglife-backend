package com.umust.dobonglife.domain.banners.domain.repository;

import com.umust.dobonglife.domain.banners.domain.entity.Banner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("SELECT c FROM Banner c " +
            "WHERE (:lastId IS NULL OR c.id < :lastId) " +
            "ORDER BY c.id DESC")
    Slice<Banner> findBannersNoOffset(Long lastId, Pageable pageable);
}
