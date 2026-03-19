package com.umust.dobonglife.domain.place.domain.repository;

import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByIdAndStatus(Long id, BaseStatus status);

    List<Place> findAllByIdIn(List<Long> ids);

    List<Place> findAllByStatus(BaseStatus status);
}
