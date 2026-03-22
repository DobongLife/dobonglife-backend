package com.umust.dobonglife.domain.point.domain.repository;

import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
