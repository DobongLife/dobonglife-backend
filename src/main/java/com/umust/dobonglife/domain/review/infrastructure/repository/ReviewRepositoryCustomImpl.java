package com.umust.dobonglife.domain.review.infrastructure.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umust.dobonglife.domain.review.domain.repository.custom.ReviewRepositoryCustom;
import com.umust.dobonglife.domain.review.service.dto.ReviewItemProjection;
import com.umust.dobonglife.domain.review.service.dto.ReviewItemProjectionImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.umust.dobonglife.domain.course.domain.entity.QCourse.course;
import static com.umust.dobonglife.domain.place.model.QPlace.place;
import static com.umust.dobonglife.domain.review.domain.entity.QReview.review;
import static com.umust.dobonglife.domain.review.domain.entity.QReviewLike.reviewLike;

@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewItemProjection> findReviewItemsByUserId(Long userId, Pageable pageable) {
        return null;
//        List<ReviewItemProjectionImpl> content = queryFactory
//                .select(Projections.constructor(ReviewItemProjectionImpl.class,
//                        review.id,
//                        course.title,
//                        place.name,
//                        review.rating,
//                        Expressions.stringTemplate("SUBSTRING({0}, 1, 100)", review.content),
//                        Expressions.dateTemplate(LocalDate.class, "CAST({0} AS date)", review.createdAt),
//                        reviewLike.id.countDistinct().intValue().coalesce(0),
//                        Expressions.constant(0),
//                        Expressions.constant("")
//                ))
//                .from(review)
//                .leftJoin(course).on(review.courseId.eq(course.id))
//                .leftJoin(place).on(review.placeId.eq(place.id))
//                .leftJoin(reviewLike).on(reviewLike.id.eq(review.id))
//                .where(review.userId.eq(userId))
//                .groupBy(
//                        review.id,
//                        course.title,
//                        place.name,
//                        review.rating,
//                        review.content,
//                        review.createdAt
//                )
//                .orderBy(review.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        // 조회된 후기가 없으면 빈 페이지 반환
//        if (content.isEmpty()) {
//            return Page.empty(pageable);
//        }
//
//        List<Long> reviewIds = content.stream()
//                .map(ReviewItemProjection::getReviewId)
//                .collect(Collectors.toList());
//
//        Map<Long, ImageInfo> imageInfoMap = fetchImageInfoBatch(reviewIds);
//
//        List<ReviewItemProjection> result = content.stream()
//                .map(item -> {
//                    ImageInfo imageInfo = imageInfoMap.getOrDefault(
//                            item.getReviewId(),
//                            new ImageInfo(0, null)
//                    );
//                    return new ReviewItemProjectionImpl(
//                            item.getReviewId(),
//                            item.getCourseName(),
//                            item.getPlaceName(),
//                            item.getRating(),
//                            item.getContentSummary(),
//                            item.getWrittenDate(),
//                            item.getLikeCount(),
//                            imageInfo.imageCount,
//                            imageInfo.thumbnailUrl
//                    );
//                })
//                .collect(Collectors.toList());
//
//        // Count 쿼리
//        JPAQuery<Long> countQuery = queryFactory
//                .select(review.count())
//                .from(review)
//                .where(review.userId.eq(userId));
//
//        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }


    private Map<Long, ImageInfo> fetchImageInfoBatch(List<Long> reviewIds) {
        return null;
        // TODO: 이미지 @Element 어떻게 처리할지 고민하기
//        QReviewImage reviewImage = QReviewImage.reviewImage;
//
//        List<Tuple> results = queryFactory
//                .select(
//                        reviewImage.reviewId,
//                        reviewImage.imageUrl.count(),
//                        reviewImage.imageUrl.min()  // 첫 번째 이미지를 썸네일로
//                )
//                .from(reviewImage)
//                .where(reviewImage.reviewId.in(reviewIds))
//                .groupBy(reviewImage.reviewId)
//                .fetch();
//
//        return results.stream()
//                .collect(Collectors.toMap(
//                        tuple -> tuple.get(reviewImage.reviewId),
//                        tuple -> new ImageInfo(
//                                tuple.get(reviewImage.imageUrl.count()).intValue(),
//                                tuple.get(reviewImage.imageUrl.min())
//                        )
//                ));
    }

    private record ImageInfo(int imageCount, String thumbnailUrl) {
    }
}
