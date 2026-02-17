package com.umust.dobonglife.domain.course.domain.entity;

import com.umust.dobonglife.domain.course.domain.constant.CourseLevel;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.course.domain.vo.CourseBasicInfo;
import com.umust.dobonglife.domain.course.domain.vo.CourseReviewStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CourseEntityTest {

    private CourseBasicInfo createBasicInfo() {
        return CourseBasicInfo.builder()
                .title("도봉구 역사 탐방")
                .subTitle("숨겨진 역사를 찾아서")
                .duration(180L)
                .level(CourseLevel.BEGINNER)
                .build();
    }

    private Course createCourse(double averageRating, long reviewCount) {
        return Course.builder()
                .userId(1L)
                .basicInfo(createBasicInfo())
                .reviewStats(new CourseReviewStats(averageRating, reviewCount))
                .themes(List.of(CourseTheme.HISTORY))
                .tags(List.of("역사", "문화"))
                .build();
    }

    @Nested
    @DisplayName("applyNewReview - 새 리뷰 반영")
    class ApplyNewReview {

        @Test
        @DisplayName("리뷰 0개 상태에서 첫 리뷰 추가")
        void firstReview() {
            Course course = createCourse(0.0, 0);

            course.applyNewReview(4.0);

            assertThat(course.getReviewCount()).isEqualTo(1L);
            assertThat(course.getAverageRating()).isCloseTo(4.0, within(0.001));
        }

        @Test
        @DisplayName("기존 리뷰가 있을 때 새 리뷰 추가")
        void addToExisting() {
            // 평균 4.0, 리뷰 2개 → 총점 8.0 + 새 리뷰 5.0 → 평균 13.0/3 = 4.333...
            Course course = createCourse(4.0, 2);

            course.applyNewReview(5.0);

            assertThat(course.getReviewCount()).isEqualTo(3L);
            assertThat(course.getAverageRating()).isCloseTo(13.0 / 3, within(0.001));
        }

        @Test
        @DisplayName("연속으로 리뷰 추가")
        void consecutiveReviews() {
            Course course = createCourse(0.0, 0);

            course.applyNewReview(3.0);
            course.applyNewReview(5.0);

            assertThat(course.getReviewCount()).isEqualTo(2L);
            assertThat(course.getAverageRating()).isCloseTo(4.0, within(0.001));
        }
    }

    @Nested
    @DisplayName("deleteReview - 리뷰 삭제 반영")
    class DeleteReview {

        @Test
        @DisplayName("리뷰 여러 개 중 1개 삭제")
        void deleteOneOfMany() {
            // 평균 4.0, 리뷰 3개 → 총점 12.0 - 삭제될 리뷰 3.0 → (12.0-3.0)/2 = 4.5
            Course course = createCourse(4.0, 3);

            course.deleteReview(3.0);

            assertThat(course.getReviewCount()).isEqualTo(2L);
            assertThat(course.getAverageRating()).isCloseTo(4.5, within(0.001));
        }

        @Test
        @DisplayName("마지막 리뷰 삭제 시 count가 0이 되고 평균도 0.0")
        void deleteLastReview() {
            Course course = createCourse(4.0, 1);

            course.deleteReview(4.0);

            assertThat(course.getReviewCount()).isEqualTo(0L);
            assertThat(course.getAverageRating()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("리뷰 0개에서 삭제 시 음수 방지")
        void deleteFromZero() {
            Course course = createCourse(0.0, 0);

            course.deleteReview(3.0);

            assertThat(course.getReviewCount()).isEqualTo(0L);
            assertThat(course.getAverageRating()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("updateRating - 평점 업데이트")
    class UpdateRating {

        @Test
        @DisplayName("리뷰 2개 상태에서 평점 업데이트")
        void updateRatingCalculation() {
            // updateRating: totalScore = averageRating - averageRating + newRating = newRating
            // newAverage = newRating / reviewCount
            Course course = createCourse(4.0, 2);

            course.updateRating(5.0);

            assertThat(course.getAverageRating()).isCloseTo(5.0 / 2, within(0.001));
            assertThat(course.getReviewCount()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("validateCourse - 생성 유효성 검증")
    class ValidateCourse {

        @Test
        @DisplayName("basicInfo가 null이면 IllegalArgumentException 발생")
        void nullBasicInfoThrows() {
            assertThatThrownBy(() ->
                    Course.builder()
                            .userId(1L)
                            .basicInfo(null)
                            .build()
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("기본 정보는 필수입니다");
        }
    }

    @Nested
    @DisplayName("빌더 기본값 및 편의 메서드")
    class BuilderAndConvenience {

        @Test
        @DisplayName("reviewStats null이면 기본값 (0.0, 0L) 초기화")
        void defaultReviewStats() {
            Course course = Course.builder()
                    .userId(1L)
                    .basicInfo(createBasicInfo())
                    .build();

            assertThat(course.getAverageRating()).isEqualTo(0.0);
            assertThat(course.getReviewCount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("themes, tags, imageUrls null이면 빈 리스트 초기화")
        void defaultCollections() {
            Course course = Course.builder()
                    .userId(1L)
                    .basicInfo(createBasicInfo())
                    .build();

            assertThat(course.getThemes()).isEmpty();
            assertThat(course.getTags()).isEmpty();
            assertThat(course.getImageUrls()).isEmpty();
        }

        @Test
        @DisplayName("description 양방향 관계 설정")
        void descriptionBidirectional() {
            CourseDescription description = CourseDescription.builder()
                    .content("상세 설명")
                    .highlights(List.of("하이라이트1"))
                    .build();

            Course course = Course.builder()
                    .userId(1L)
                    .basicInfo(createBasicInfo())
                    .description(description)
                    .build();

            assertThat(course.getDescription()).isNotNull();
            assertThat(course.getDescription().getCourse()).isSameAs(course);
        }

        @Test
        @DisplayName("updateBasicInfo로 기본 정보 갱신")
        void updateBasicInfo() {
            Course course = createCourse(0.0, 0);
            CourseBasicInfo newInfo = CourseBasicInfo.builder()
                    .title("새 제목")
                    .subTitle("새 부제목")
                    .duration(120L)
                    .level(CourseLevel.ADVANCED)
                    .build();

            course.updateBasicInfo(newInfo);

            assertThat(course.getTitle()).isEqualTo("새 제목");
            assertThat(course.getSubTitle()).isEqualTo("새 부제목");
            assertThat(course.getLevel()).isEqualTo(CourseLevel.ADVANCED);
        }

        @Test
        @DisplayName("Getter 편의 메서드 동작 확인")
        void getterConvenience() {
            Course course = createCourse(3.5, 10);

            assertThat(course.getTitle()).isEqualTo("도봉구 역사 탐방");
            assertThat(course.getSubTitle()).isEqualTo("숨겨진 역사를 찾아서");
            assertThat(course.getLevel()).isEqualTo(CourseLevel.BEGINNER);
            assertThat(course.getAverageRating()).isEqualTo(3.5);
            assertThat(course.getReviewCount()).isEqualTo(10L);
        }
    }
}
