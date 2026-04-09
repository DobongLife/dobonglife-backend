package com.umust.dobonglife.domain.course.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@Entity
@Table(name = "course_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_image_id")
    private Long id;

    @Column(length = 255)
    private String imageUrl;

    private Short sortOrder;

    @Builder
    private CourseImage(String imageUrl, Short sortOrder) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public static List<CourseImage> ofUrls(List<String> imageUrls) {
        return IntStream.range(0, imageUrls.size())
                .mapToObj(i -> new CourseImage(imageUrls.get(i), (short) i))
                .toList();
    }
}

