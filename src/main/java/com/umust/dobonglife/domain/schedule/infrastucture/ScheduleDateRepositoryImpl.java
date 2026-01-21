package com.umust.dobonglife.domain.schedule.infrastucture;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.schedule.domain.entity.QSchedule;
import com.umust.dobonglife.domain.schedule.domain.entity.ScheduleDate;
import com.umust.dobonglife.domain.schedule.domain.repository.custom.ScheduleDateRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleDateRepositoryImpl implements ScheduleDateRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ScheduleDate> findTodayScheduleDates(Long userId, LocalDate date) {

        QScheduleDate scheduleDate = QScheduleDate.scheduleDate;
        QSchedule schedule = QSchedule.schedule;

        // 단방향이므로 ScheduleDate -> Schedule 조인
        return queryFactory
                .selectFrom(scheduleDate)
                .join(scheduleDate.schedule, schedule)
                .where(
                        schedule.user.id.eq(userId),
                        scheduleDate.date.eq(date)
                )
                // 종일(null) 먼저/나중 정렬은 취향인데, 보통 종일이 위로 오게 처리 많이 함
                .orderBy(
                        scheduleDate.startTime.asc().nullsFirst(),
                        scheduleDate.endTime.asc().nullsLast()
                )
                .fetch();
    }

    @Override
    public List<ScheduleDate> findMonthlyScheduleDates(Long userId,
                                                       LocalDate startDateInclusive,
                                                       LocalDate endDateExclusive) {

        QScheduleDate scheduleDate = QScheduleDate.scheduleDate;
        QSchedule schedule = QSchedule.schedule;

        // [start, end) 형태로 정확히 맞추는 게 좋음
        return queryFactory
                .selectFrom(scheduleDate)
                .join(scheduleDate.schedule, schedule)
                .where(
                        schedule.user.id.eq(userId),
                        scheduleDate.date.goe(startDateInclusive),
                        scheduleDate.date.lt(endDateExclusive)
                )
                .orderBy(
                        scheduleDate.date.asc(),
                        scheduleDate.startTime.asc().nullsFirst()
                )
                .fetch();
    }
}
