package com.umust.dobonglife.domain.place.domain.repository;

import com.umust.dobonglife.domain.place.domain.entity.PlaceLike;
import com.umust.dobonglife.domain.place.domain.repository.custom.PlaceLikeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long>, PlaceLikeRepositoryCustom {
}
