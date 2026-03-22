package com.umust.dobonglife.domain.schedule.infrastructure.jpa.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Schedule> findMonthlyOverlaps(Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive) {
        QSchedule schedule = QSchedule.schedule;

        return queryFactory
                .selectFrom(schedule)
                .where(
                        schedule.userId.eq(userId),
                        schedule.startTime.lt(endExclusive),
                        schedule.endTime.goe(startInclusive)
                )
                .orderBy(schedule.startTime.asc(), schedule.id.asc())
                .fetch();
    }
}
