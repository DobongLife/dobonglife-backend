package com.umust.dobonglife.domain.place.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "places")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "amenity", nullable = false)
    private Amenity amenity;

    @Column(name = "address", nullable = false)
    private String address;

    @ElementCollection
    @CollectionTable(name = "place_images", joinColumns = @JoinColumn(name = "place_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> placeImages = new ArrayList<>();

    @Column(name = "operating_hour", nullable = false)
    private String operatingHour;
}




