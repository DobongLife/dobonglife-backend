package com.umust.dobonglife.domain.business.domain.entity;

import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "business")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Business extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 100)
    private String phoneNumber;

    @Column(nullable = true)
    private String businessNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    private User user;
}
