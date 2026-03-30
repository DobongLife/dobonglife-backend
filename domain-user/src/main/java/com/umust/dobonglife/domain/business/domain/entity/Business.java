package com.umust.dobonglife.domain.business.domain.entity;

import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Category category;

    @Column(length = 30)
    private String businessNumber;

    @Column(length = 30)
    private String managerName;

    @Column(length = 255)
    private String email;

    @Builder
    private Business(Long userId, Long placeId, Category category,
                     String businessNumber, String managerName, String email) {
        this.userId = userId;
        this.placeId = placeId;
        this.category = category;
        this.businessNumber = businessNumber;
        this.managerName = managerName;
        this.email = email;
    }

    public void updateInfo(String email, String managerName) {
        this.email = email;
        this.managerName = managerName;
    }

    public void updatePlace(Long placeId) {
        this.placeId = placeId;
    }
}
