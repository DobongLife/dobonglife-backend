package com.umust.dobonglife.domain.schedule.infrastructure.adapter;

import com.umust.dobonglife.domain.schedule.application.port.out.LoadFestivalPort;
import com.umust.dobonglife.domain.schedule.domain.entity.Festival;
import com.umust.dobonglife.domain.schedule.infrastructure.jpa.FestivalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FestivalPersistenceAdapter implements LoadFestivalPort {

    private final FestivalJpaRepository festivalJpaRepository;

    @Override
    public List<Festival> findUpcoming(LocalDateTime from, int limit) {
        return festivalJpaRepository.findByEndDateTimeGreaterThanEqualOrderByStartDateTimeAsc(
                from, PageRequest.of(0, limit));
    }
}
