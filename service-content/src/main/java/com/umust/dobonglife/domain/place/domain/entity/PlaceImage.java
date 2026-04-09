package com.umust.dobonglife.domain.place.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@Entity
@Table(name = "place_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_image_id")
    private Long id;

    @Column(length = 255)
    private String imageUrl;

    private Short sortOrder;

    @Builder
    private PlaceImage(String imageUrl, Short sortOrder) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public static List<PlaceImage> ofUrls(List<String> imageUrls) {
        return IntStream.range(0, imageUrls.size())
                .mapToObj(i -> new PlaceImage(imageUrls.get(i), (short) i))
                .toList();
    }
}

