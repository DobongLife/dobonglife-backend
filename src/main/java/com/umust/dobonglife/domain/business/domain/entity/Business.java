package com.umust.dobonglife.domain.business.domain.entity;

import com.umust.dobonglife.domain.place.domain.constant.PlaceCategory;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "businesses")
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

    @Column(nullable = false)
    private String businessNumber;

    @Column(nullable = true)
    private String email;

    @Column(nullable = false)
    private String managerName;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAuthenticated = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="place_id", unique = true)
    private Place place;
}
