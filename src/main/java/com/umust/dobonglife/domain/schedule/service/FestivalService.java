package com.umust.dobonglife.domain.schedule.service;

import com.umust.dobonglife.domain.schedule.controller.dto.response.UpcomingFestivalResponse;
import com.umust.dobonglife.domain.schedule.domain.entity.Festival;
import com.umust.dobonglife.domain.schedule.domain.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalService {

    private final FestivalRepository festivalRepository;

    @Transactional(readOnly = true)
    public List<UpcomingFestivalResponse> getUpcomingTop3() {

        // 오늘 00:00 기준 (Asia/Seoul)
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        LocalDateTime todayStart = LocalDate.now(zoneId).atStartOfDay();

        Pageable top3 = PageRequest.of(0, 3);

        List<Festival> festivals =
                festivalRepository.findByEndDateTimeGreaterThanEqualOrderByStartDateTimeAsc(
                        todayStart,
                        top3
                );

        return festivals.stream()
                .map(f -> UpcomingFestivalResponse.builder()
                        .festivalId(f.getId())
                        .title(f.getTitle())
                        .placeName(f.getPlaceName())
                        .startTime(f.getStartDateTime())
                        .endTime(f.getEndDateTime())
                        .url(f.getUrl())
                        .category(f.getCategory())
                        .build())
                .toList();
    }
}

