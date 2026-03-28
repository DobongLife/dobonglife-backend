package com.umust.dobonglife.domain.like.domain.entity;

import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "targetType", "targetId"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private TargetType targetType;
    private Long targetId;

    public static Like of(Long userId, TargetType targetType, Long targetId) {
        Like like = new Like();
        like.userId = userId;
        like.targetType = targetType;
        like.targetId = targetId;
        return like;
    }
}
