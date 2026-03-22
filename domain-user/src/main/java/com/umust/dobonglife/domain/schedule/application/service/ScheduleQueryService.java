package com.umust.dobonglife.domain.schedule.application.service;

import com.umust.dobonglife.domain.schedule.application.dto.DailyScheduleDetail;
import com.umust.dobonglife.domain.schedule.application.dto.MonthlyScheduleResult;
import com.umust.dobonglife.domain.schedule.application.dto.ScheduleDetail;
import com.umust.dobonglife.domain.schedule.application.port.in.ScheduleQueryUseCase;
import com.umust.dobonglife.domain.schedule.application.port.out.LoadSchedulePort;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleQueryService implements ScheduleQueryUseCase {

    private final LoadSchedulePort loadSchedulePort;

    @Override
    public MonthlyScheduleResult getMonthlyScheduleList(Long userId, int year, int month) {

        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        LocalDateTime startInclusive = monthStart.atStartOfDay();
        LocalDateTime endExclusive = monthStart.plusMonths(1).atStartOfDay();

        List<Schedule> schedules = loadSchedulePort.findMonthlyOverlaps(userId, startInclusive, endExclusive);

        Map<LocalDate, List<ScheduleDetail>> bucket = new TreeMap<>();

        for (Schedule s : schedules) {
            LocalDate sStart = s.getStartTime().toLocalDate();
            LocalDate sEnd = s.getEndTime().toLocalDate();

            LocalDate from = sStart.isBefore(monthStart) ? monthStart : sStart;
            LocalDate to = sEnd.isAfter(monthEnd) ? monthEnd : sEnd;

            ScheduleDetail dto = ScheduleDetail.from(s);

            for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
                bucket.computeIfAbsent(d, k -> new ArrayList<>()).add(dto);
            }
        }

        for (List<ScheduleDetail> daySchedules : bucket.values()) {
            daySchedules.sort(
                    Comparator.comparing(ScheduleDetail::getStartTime)
                            .thenComparing(ScheduleDetail::getId)
            );
        }

        List<DailyScheduleDetail> result = new ArrayList<>(bucket.size());
        for (Map.Entry<LocalDate, List<ScheduleDetail>> e : bucket.entrySet()) {
            result.add(DailyScheduleDetail.of(e.getKey(), e.getValue()));
        }
        return MonthlyScheduleResult.from(result);
    }
}
