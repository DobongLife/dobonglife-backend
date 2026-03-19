package com.umust.dobonglife.domain.review.application.dto;

import com.umust.dobonglife.global.common.constant.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateReviewRequest(
        @NotNull
        TargetType targetType,

        @NotNull
        Long targetId,

        @NotNull
        Double rating,

        @NotBlank @Size(max = 255)
        String content,

        List<String> imageUrls
) {}
