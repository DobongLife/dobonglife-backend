package com.umust.dobonglife.domain.course.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.course.domain.entity.QCourse;
import com.umust.dobonglife.domain.course.domain.repository.custom.CourseRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Course> findAllRandomOrder(Pageable pageable) {
        QCourse course = QCourse.course;

        // 전체 ID와 총 개수 조회 (성능 최적화)
        List<Long> allIds = queryFactory
                .select(course.id)
                .from(course)
                .fetch();

        long totalCount = allIds.size();

        // 전체 데이터가 없으면 빈 페이지 반환
        if (totalCount == 0) {
            return Page.empty(pageable);
        }

        // 애플리케이션 메모리에서 무작위 ID 선택 (요청 사이즈 보장)
        List<Long> selectedIds = selectRandomIds(allIds, pageable);

        // 선택된 ID를 기준으로 실제 Course 데이터 조회
        List<Course> content = queryFactory
                .selectFrom(course)
                .where(course.id.in(selectedIds))
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount);
    }

    private List<Long> selectRandomIds(List<Long> allIds, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        // 총 데이터 수가 요청 사이즈 이하이면 전체 ID 반환
        if (allIds.size() <= pageSize) {
            return allIds;
        }

        // ID 목록을 무작위로 섞음
        Collections.shuffle(allIds);

        // 요청 사이즈만큼 잘라내서 반환
        return allIds.subList(0, pageSize);
    }
}
