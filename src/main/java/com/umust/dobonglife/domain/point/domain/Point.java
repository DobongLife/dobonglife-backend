package com.umust.dobonglife.domain.point.domain;

import com.umust.dobonglife.domain.point.domain.PointType;
import com.umust.dobonglife.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "points")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "points_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false)
    private PointType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "reason", nullable = false)
    private String reason;
}