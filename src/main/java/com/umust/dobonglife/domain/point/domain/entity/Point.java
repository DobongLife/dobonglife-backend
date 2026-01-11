package com.umust.dobonglife.domain.point.domain.entity;

import com.umust.dobonglife.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "points")
@Getter
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
    private Long amount;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Point(PointType type, User user, Long amount, String title) {
        this.type = type;
        this.user = user;
        this.amount = amount;
        this.title = title;
        this.createdAt = LocalDateTime.now();
    }
}