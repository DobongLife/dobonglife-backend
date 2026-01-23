package com.umust.dobonglife.domain.schedule.infrastucture;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.schedule.domain.entity.QSchedule;
import com.umust.dobonglife.domain.schedule.domain.entity.Schedule;
import com.umust.dobonglife.domain.schedule.domain.repository.custom.ScheduleRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Schedule> findMonthlyOverlaps(Long userId, LocalDateTime startInclusive, LocalDateTime endExclusive) {
        QSchedule schedule = QSchedule.schedule;

        return queryFactory
                .selectFrom(schedule)
                .where(
                        schedule.user.id.eq(userId),
                        schedule.startTime.lt(endExclusive),
                        schedule.endTime.goe(startInclusive)
                )
                .orderBy(schedule.startTime.asc(), schedule.id.asc())
                .fetch();
    }
}

