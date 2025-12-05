package com.umust.dobonglife.domain.place.model.repository;

import com.umust.dobonglife.domain.place.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Long, Place> {
}
