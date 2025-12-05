package com.umust.dobonglife.domain.schedule.infrastucture;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.schedule.domain.entity.QSchedule;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.domain.repository.custom.ScheduleRepositoryCustom;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Schedule> findTodaySchedules(Long userId, LocalDate date) {

        QSchedule schedule = QSchedule.schedule;

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        return queryFactory
                .selectFrom(schedule)
                .where(
                        schedule.user.id.eq(userId)
                                .and(schedule.startTime.loe(endOfDay))
                                .and(schedule.endTime.goe(startOfDay))
                )
                .orderBy(schedule.startTime.asc())
                .fetch();
    }
}

