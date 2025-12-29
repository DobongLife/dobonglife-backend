package com.umust.dobonglife.domain.point.domain.entity;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.model.BaseEntity;
import com.umust.dobonglife.global.common.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "points")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id", nullable = false)
    private Long id;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "reason", nullable = false)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_used")
    private boolean isUsed;

    public void markUsed() {
        if (this.isUsed) {
            throw new BusinessException(ErrorCode.POINT_ALREADY_USED);
        }
        this.isUsed = true;
    }
}
