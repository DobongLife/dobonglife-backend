package com.umust.dobonglife.domain.business.domain.entity;

import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "businesses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Business extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_id")
    private Long id;

    private Long userId;

    private Long placeId;

    @Column(length = 30)
    private String businessNumber;

    @Column(length = 30)
    private String managerName;

    @Column(length = 255)
    private String email;
}
