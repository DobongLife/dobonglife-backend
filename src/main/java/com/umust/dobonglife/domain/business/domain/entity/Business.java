package com.umust.dobonglife.domain.business.domain.entity;

import com.umust.dobonglife.domain.business.domain.constant.BusinessCategory;
import com.umust.dobonglife.domain.business.domain.constant.BusinessAmenity;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


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

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 100)
    private String phoneNumber;

    @Column(nullable = false)
    private String businessNumber;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String link;

    @Column(nullable = false)
    private String operatingHour;

    @Column(nullable = false)
    private String managerName;

    @Column(nullable = false)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_catergory", nullable = false)
    private BusinessCategory businessCategory;

    @ElementCollection(targetClass = BusinessAmenity.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "business_amenity", joinColumns = @JoinColumn(name = "business_id"))
    @Enumerated(EnumType.STRING)
    private List<BusinessAmenity> businessAmenity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="place_id", unique = true)
    private Place place;
}
