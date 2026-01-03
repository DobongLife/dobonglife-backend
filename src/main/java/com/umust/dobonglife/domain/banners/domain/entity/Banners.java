package com.umust.dobonglife.domain.banners.domain.entity;

import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Banners extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private Integer priority;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder
    public Banners(String title, String description, String imageUrl, String link, Integer priority, Boolean isActive) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.link = link;
        this.priority = priority != null ? priority : 0;
        this.isActive = isActive != null ? isActive : true;
    }
}
