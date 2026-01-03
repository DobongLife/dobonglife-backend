package com.umust.dobonglife.domain.course.controller.dto.request;

import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema(description = "코스 생성 요청")
public class CreateCourseRequest {

        @Schema(
                description = "코스 제목",
                example = "도봉구 역사 탐방 코스",
                required = true,
                maxLength = 100
        )
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
        private String title;

        @Schema(
                description = "코스 부제목",
                example = "도봉구의 숨겨진 역사를 찾아서",
                required = false,
                nullable = true,
                maxLength = 200
        )
        @Size(max = 200, message = "부제목은 200자를 초과할 수 없습니다")
        private String subTitle;

        @Schema(
                description = "코스 테마 목록 (1~6개 선택)",
                example = "[\"HISTORY\", \"CULTURE\"]",
                required = true,
                minLength = 1,
                maxLength = 6,
                allowableValues = {"HISTORY", "CULTURE", "NATURE", "FOOD", "CAFE", "SHOPPING"}
        )
        @NotNull(message = "카테고리는 필수입니다")
        @Size(min = 1, max = 6, message = "테마는 1~6개를 선택해야 합니다")
        private List<CourseTheme> themes = new ArrayList<>();

        @Schema(
                description = "소요 시간 (긱 단위)",
                example = "180",
                required = true,
                minimum = "1",
                maximum = "1440"
        )
        @NotNull(message = "소요시간은 필수입니다")
        @Positive(message = "소요시간은 0보다 커야 합니다")
        @Max(value = 1440, message = "소요시간은 24시간(1440분)을 초과할 수 없습니다")
        private Long duration;

        @Schema(
                description = "난이도",
                example = "BEGINNER",
                required = true,
                allowableValues = {"BEGINNER", "INTERMEDIATE", "ADVANCED"}
        )
        @NotNull(message = "레벨은 필수입니다")
        private String level;

        @Schema(
                description = "태그 목록 (최대 5개, 각 태그는 20자 이하)",
                example = "[\"역사\", \"문화\", \"가족\"]",
                required = false,
                nullable = true,
                maxLength = 5
        )
        @Size(max = 5, message = "태그는 최대 5개까지 등록할 수 있습니다")
        private List<@NotBlank @Size(max = 20) String> tags = new ArrayList<>();

        @Schema(
                description = "코스 상세 내용 (최소 10자)",
                example = "도봉구의 역사적 장소를 둘러보는 코스입니다. 조선시대부터 현대까지의 흔적을 따라가며...",
                required = true,
                minLength = 10
        )
        @NotBlank(message = "내용은 필수입니다")
        @Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다")
        private String content;

        @Schema(
                description = "코스 하이라이트 (최대 10개, 각 항목은 100자 이하)",
                example = "[\"조선시대 유적 탐방\", \"전통 시장 체험\", \"도봉산 둘레길 산책\"]",
                required = false,
                nullable = true,
                maxLength = 10
        )
        @Size(max = 10, message = "하이라이트는 최대 10개까지 등록할 수 있습니다")
        private List<@NotBlank @Size(max = 100) String> highlights = new ArrayList<>();

        @Schema(
                description = "코스 일정 목록 (최대 6개)",
                required = true,
                maxLength = 6
        )
        @Valid
        @NotNull(message = "일정은 필수입니다")
        @Size(min = 1, max = 6, message = "일정은 최소 1개, 최대 6개까지 등록할 수 있습니다")
        private List<CoursePlanRequest> plans = new ArrayList<>();
}