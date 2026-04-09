package com.umust.dobonglife.domain.point.domain.entity;

import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_histories")
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Long id;

    private Long pointId;
    private Long amount;
    private Long afterBalance;
    private Boolean isUsed;

    @Column(length = 50)
    private String title;

    @Builder
    private PointHistory(Long pointId, Long amount, Long afterBalance, Boolean isUsed, String title) {
        this.pointId = pointId;
        this.amount = amount;
        this.afterBalance = afterBalance;
        this.isUsed = isUsed;
        this.title = title;
    }

    public static PointHistory ofDeduction(Long pointId, Long amount, Long afterBalance, String title) {
        return PointHistory.builder()
                .pointId(pointId)
                .amount(amount)
                .afterBalance(afterBalance)
                .isUsed(true)
                .title(title)
                .build();
    }

    public static PointHistory ofRefund(Long pointId, Long amount, Long afterBalance, String title) {
        return PointHistory.builder()
                .pointId(pointId)
                .amount(amount)
                .afterBalance(afterBalance)
                .isUsed(false)
                .title(title)
                .build();
    }
}
