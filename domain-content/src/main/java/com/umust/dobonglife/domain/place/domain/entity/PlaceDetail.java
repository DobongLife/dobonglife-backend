package com.umust.dobonglife.domain.place.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_detail_id")
    private Long id;

    @Column(length = 100)
    private String subName;
    @Column(length = 255)
    private String address;
    @Column(length = 50)
    private String operatingHour;
    @Column(length = 30)
    private String contact;
    @Column(length = 255)
    private String content;

    @Builder
    private PlaceDetail(String subName, String address, String operatingHour, String contact, String content) {
        this.subName = subName;
        this.address = address;
        this.operatingHour = operatingHour;
        this.contact = contact;
        this.content = content;
    }
}
