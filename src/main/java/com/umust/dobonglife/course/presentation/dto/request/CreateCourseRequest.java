package com.umust.dobonglife.course.presentation.dto.request;

import com.umust.dobonglife.course.domain.constant.CourseLevel;
import com.umust.dobonglife.course.domain.constant.CourseTheme;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateCourseRequest(
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
        String title,

        @Size(max = 200, message = "부제목은 200자를 초과할 수 없습니다")
        String subTitle,

        @NotNull(message = "카테고리는 필수입니다")
        List<CourseTheme> themes,

        @NotNull(message = "소요시간은 필수입니다")
        @Positive(message = "소요시간은 0보다 커야 합니다")
        @Max(value = 24, message = "소요시간은 24시간을 초과할 수 없습니다")
        Double duration,

        @NotNull(message = "레벨은 필수입니다")
        CourseLevel level,

        @Size(max = 5, message = "태그는 최대 5개까지 등록할 수 있습니다")
        List<@NotBlank @Size(max = 20) String> tags,

        @NotEmpty(message = "이미지는 최소 1개 이상 필수입니다")
        @Size(min = 1, max = 5, message = "이미지는 1개 이상 5개 이하로 등록해야 합니다")
        List<@NotBlank @Pattern(regexp = "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$") String> imageUrls,

        @NotBlank(message = "내용은 필수입니다")
        @Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다")
        String content,

        @NotBlank(message = "만남 장소는 필수입니다")
        @Size(max = 200, message = "만남 장소는 200자를 초과할 수 없습니다")
        String meetingPlace,

        @NotBlank(message = "연락처는 필수입니다")
        @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$",
                message = "올바른 전화번호 형식이 아닙니다 (예: 010-1234-5678)")
        String contact,

        @Size(max = 20, message = "비용은 20자를 초과할 수 없습니다")
        String cost,

        @NotNull(message = "최대 인원은 필수입니다")
        @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다")
        @Max(value = 100, message = "최대 인원은 100명을 초과할 수 없습니다")
        Integer maxNum,

        @NotBlank(message = "나이 제한 정보는 필수입니다")
        @Size(max = 30, message = "나이 제한은 30자를 초과할 수 없습니다")
        String ageLimit,

        @Size(max = 200, message = "취소 정책은 200자를 초과할 수 없습니다")
        String cancelPolicy,

        @Size(max = 200, message = "날씨 정책은 200자를 초과할 수 없습니다")
        String weatherPolicy,

        @Size(max = 10, message = "하이라이트는 최대 10개까지 등록할 수 있습니다")
        List<@NotBlank @Size(max = 100) String> highlights,

        @Size(max = 10, message = "불포함 사항은 최대 10개까지 등록할 수 있습니다")
        List<@NotBlank @Size(max = 100) String> exclusions,

        @Size(max = 10, message = "포함 사항은 최대 10개까지 등록할 수 있습니다")
        List<@NotBlank @Size(max = 100) String> inclusions,
        @Valid
        @Size(max = 20, message = "일정은 최대 20개까지 등록할 수 있습니다")
        List<CoursePlanRequest> plans
) {
    public CreateCourseRequest {
        // null인 List를 빈 List로 변환
        if (tags == null) {
            tags = List.of();
        }
        if (highlights == null) {
            highlights = List.of();
        }
        if (exclusions == null) {
            exclusions = List.of();
        }
        if (inclusions == null) {
            inclusions = List.of();
        }
    }
}
