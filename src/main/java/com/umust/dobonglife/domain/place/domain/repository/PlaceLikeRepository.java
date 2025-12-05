package com.umust.dobonglife.domain.place.domain.repository;

import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceLikeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import com.umust.dobonglife.global.common.model.BaseStatus;

import java.util.List;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long>, PlaceLikeRepositoryCustom {
    List<PlaceLike> findByUser_IdAndStatus(Long userId, BaseStatus status);
}
