package com.umust.dobonglife.domain.point.application.port.out;

import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoadPointPort {

    Optional<Point> findByUserId(Long userId);

    Optional<Point> findByUserIdForUpdate(Long userId);

    List<PointHistory> findByPointIdDesc(Long pointId, Long lastId, Pageable pageable);

    List<PointHistory> findByPointIdAsc(Long pointId, Long lastId, Pageable pageable);
}
