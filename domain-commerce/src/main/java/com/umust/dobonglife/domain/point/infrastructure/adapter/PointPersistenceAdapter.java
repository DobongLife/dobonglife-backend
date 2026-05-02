package com.umust.dobonglife.domain.point.infrastructure.adapter;

import com.umust.dobonglife.domain.point.application.port.out.LoadPointPort;
import com.umust.dobonglife.domain.point.application.port.out.SavePointPort;
import com.umust.dobonglife.domain.point.domain.entity.Point;
import com.umust.dobonglife.domain.point.domain.entity.PointHistory;
import com.umust.dobonglife.domain.point.infrastructure.jpa.PointHistoryJpaRepository;
import com.umust.dobonglife.domain.point.infrastructure.jpa.PointJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PointPersistenceAdapter implements LoadPointPort, SavePointPort {

    private final PointJpaRepository pointJpaRepository;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Point> findByUserIdForUpdate(Long userId) {
        return pointJpaRepository.findByUserIdForUpdate(userId);
    }

    @Override
    public List<PointHistory> findByPointIdDesc(Long pointId, Long lastId, Pageable pageable) {
        return pointHistoryJpaRepository.findByPointIdDesc(pointId, lastId, pageable);
    }

    @Override
    public List<PointHistory> findByPointIdAsc(Long pointId, Long lastId, Pageable pageable) {
        return pointHistoryJpaRepository.findByPointIdAsc(pointId, lastId, pageable);
    }

    @Override
    public PointHistory saveHistory(PointHistory pointHistory) {
        return pointHistoryJpaRepository.save(pointHistory);
    }
}
