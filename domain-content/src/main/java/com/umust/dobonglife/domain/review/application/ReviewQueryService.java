package com.umust.dobonglife.domain.review.application;

import com.umust.dobonglife.domain.course.application.port.in.GetCourseUseCase;
import com.umust.dobonglife.domain.course.domain.entity.Course;
import com.umust.dobonglife.domain.place.application.port.in.GetPlaceUseCase;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.review.application.dto.MyReviewResponse;
import com.umust.dobonglife.domain.review.application.dto.ReviewSummaryResponse;
import com.umust.dobonglife.domain.review.application.port.in.GetReviewUseCase;
import com.umust.dobonglife.domain.review.application.port.out.LoadReviewPort;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.response.CursorResponse;
import com.umust.dobonglife.global.common.response.CursorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService implements GetReviewUseCase {

    private final LoadReviewPort loadReviewPort;
    private final GetPlaceUseCase getPlaceUseCase;
    private final GetCourseUseCase getCourseUseCase;

    @Override
    public CursorResponse<ReviewSummaryResponse> getReviews(TargetType targetType, Long targetId, Long lastId, int size) {
        List<ReviewSummaryResponse> content = loadReviewPort.findReviews(targetType, targetId, lastId, size);
        return CursorUtils.toCursorResponse(content, size);
    }

    @Override
    public CursorResponse<MyReviewResponse> getMyReviews(Long userId, TargetType targetType, Long lastId, int size) {
        List<MyReviewResponse> content = loadReviewPort.findMyReviews(userId, targetType, lastId, size);
        CursorResponse<MyReviewResponse> response = CursorUtils.toCursorResponse(content, size);

        List<Long> targetIds = response.getContent().stream()
                .map(MyReviewResponse::targetId)
                .distinct()
                .toList();

        if (targetType == TargetType.PLACE) {
            Map<Long, Place> placeMap = getPlaceUseCase.getPlacesInBatch(targetIds);
            return CursorUtils.convert(response, r -> {
                Place p = placeMap.get(r.targetId());
                return new MyReviewResponse(
                        r.reviewId(), r.targetId(),
                        p != null ? p.getName() : null,
                        p != null ? p.getThumbnailUrl() : null,
                        r.rating(), r.content(), r.thumbnailUrl(),
                        List.of(), r.updatedAt());
            });
        } else {
            Map<Long, Course> courseMap = getCourseUseCase.getCoursesInBatch(targetIds);
            return CursorUtils.convert(response, r -> {
                Course c = courseMap.get(r.targetId());
                return new MyReviewResponse(
                        r.reviewId(), r.targetId(),
                        c != null ? c.getTitle() : null,
                        c != null ? c.getThumbnailUrl() : null,
                        r.rating(), r.content(), r.thumbnailUrl(),
                        List.of(), r.updatedAt());
            });
        }
    }
}
