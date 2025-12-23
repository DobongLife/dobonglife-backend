package com.umust.dobonglife.domain.business.domain.entity;

import com.umust.dobonglife.domain.place.domain.constant.Amenity;
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
    private String mangerName;

    @Column(name = "business_catergory", nullable = false)
    private String businessCategory;

    @ElementCollection(targetClass = Amenity.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "amenities", joinColumns = @JoinColumn(name = "place_id"))
    @Enumerated(EnumType.STRING)
    private List<String> businessService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    private User user;
}
