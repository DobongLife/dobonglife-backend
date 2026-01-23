package com.umust.dobonglife.domain.schedule.domain.entity;

import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "festivals")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Festival extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "festival_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "place_name", nullable = false)
    private String placeName;
}
