package com.umust.dobonglife.domain.schedule.domain.repository;

import com.umust.dobonglife.domain.schedule.domain.entity.Festival;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
    boolean existsByTitleAndStartDateTimeAndPlaceName(String title, LocalDateTime startDateTime, String placeName);
    List<Festival> findByEndDateTimeGreaterThanEqualOrderByStartDateTimeAsc(
            LocalDateTime from,
            Pageable pageable
    );
}
