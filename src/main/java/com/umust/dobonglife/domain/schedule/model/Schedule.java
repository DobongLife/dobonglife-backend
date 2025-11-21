package com.umust.dobonglife.domain.schedule.model;

import com.umust.dobonglife.domain.course.model.Course;
import com.umust.dobonglife.domain.place.model.Amenity;
import com.umust.dobonglife.domain.place.model.Place;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedules")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time", nullable = false)
    private String endTime;

    @Column(name = "memo", nullable = true)
    private String memo;

    @ElementCollection(targetClass = ScheduleType.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "amenities", joinColumns = @JoinColumn(name = "place_id"))
    @Enumerated(EnumType.STRING)
    private ScheduleType amenities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private User user;

}
