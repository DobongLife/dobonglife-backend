package com.umust.dobonglife.domain.course.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 코스 운영 정보
 * 집합 장소, 연락처, 비용, 최대 인원, 나이 제한
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseOperationInfo {

    @Column(nullable = false)
    private String meetingPlace;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = true)
    private String cost;

    @Column(nullable = false)
    private Integer maxNum;

    @Column(nullable = false)
    private String ageLimit;

    public CourseOperationInfo(String meetingPlace, String contact, String cost,
                               Integer maxNum, String ageLimit) {
        validateCost(cost);
        validateAgeLimit(ageLimit);

        this.meetingPlace = meetingPlace;
        this.contact = contact;
        this.cost = cost;
        this.maxNum = maxNum;
        this.ageLimit = ageLimit;
    }

    private void validateCost(String cost) {
        if (cost == null || cost.trim().isEmpty()) return;
        if (cost.trim().equals("무료")) return;

        try {
            int costValue = Integer.parseInt(cost.replaceAll("[^0-9]", ""));
            if (costValue < 0) {
                throw new IllegalArgumentException("비용은 0원 이상이어야 합니다");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("비용은 '무료' 또는 숫자여야 합니다");
        }
    }

    private void validateAgeLimit(String ageLimit) {
        if (ageLimit == null || ageLimit.trim().isEmpty()) {
            throw new IllegalArgumentException("나이 제한 정보는 필수입니다");
        }
        if (ageLimit.trim().equals("제한없음")) return;

        if (!ageLimit.matches("^만 [0-9]{1,2}세 이상$")) {
            throw new IllegalArgumentException("나이 제한은 '제한없음' 또는 '만 X세 이상' 형식이어야 합니다");
        }
    }
}
