package com.umust.dobonglife.domain.user.domain.entity;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.model.BaseEntity;
import com.umust.dobonglife.global.common.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column
    private String email;

    @Column
    private String name;

    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "DATE")
    private LocalDate birthday;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column
    private int balance;
}
