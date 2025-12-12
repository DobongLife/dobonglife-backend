package com.umust.dobonglife.domain.place.model.repository;

import com.umust.dobonglife.domain.place.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
=======

public interface PlaceRepository extends JpaRepository<Long, Place> {
>>>>>>> Stashed changes
}
