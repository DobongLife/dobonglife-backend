package com.umust.dobonglife.domain.point.domain.entity;

import com.umust.dobonglife.domain.point.exception.PointErrorCode;
import com.umust.dobonglife.domain.point.exception.PointException;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "points")
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    private Long userId;

    private Long balance;

    private Long totalCount;

    public void deduct(Long amount) {
        if (this.balance < amount) {
            throw new PointException(PointErrorCode.INVALID_POINT);
        }
        this.balance -= amount;
    }

    public void refund(Long amount) {
        this.balance += amount;
    }
}
