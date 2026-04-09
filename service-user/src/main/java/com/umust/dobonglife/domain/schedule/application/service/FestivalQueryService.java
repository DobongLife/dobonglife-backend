package com.umust.dobonglife.domain.schedule.application.service;

import com.umust.dobonglife.domain.schedule.application.dto.UpcomingFestivalDetail;
import com.umust.dobonglife.domain.schedule.application.port.in.FestivalUseCase;
import com.umust.dobonglife.domain.schedule.application.port.out.LoadFestivalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FestivalQueryService implements FestivalUseCase {

    private final LoadFestivalPort loadFestivalPort;

    @Override
    @Transactional(readOnly = true)
    public List<UpcomingFestivalDetail> getUpcomingTop3() {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        LocalDateTime todayStart = LocalDate.now(zoneId).atStartOfDay();

        return loadFestivalPort.findUpcoming(todayStart, 3).stream()
                .map(UpcomingFestivalDetail::from)
                .toList();
    }
}
