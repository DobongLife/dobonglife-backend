package com.umust.dobonglife.domain.user.domain.entity;

import com.umust.dobonglife.domain.user.domain.vo.Provider;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String name;

    @Column(length = 255, nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(length = 200, nullable = true)
    private String providerId;

    private Integer deleteCount;

    private boolean isBlocked;

    @Column(nullable = true)
    private LocalDateTime blockedAt;

    @Column(length = 255, nullable = true)
    private String fcmToken;

}
