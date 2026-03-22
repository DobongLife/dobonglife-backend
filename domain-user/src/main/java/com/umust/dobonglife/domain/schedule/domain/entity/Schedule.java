package com.umust.dobonglife.domain.schedule.domain.entity;

import com.umust.dobonglife.domain.schedule.domain.constant.Color;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter @Setter
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
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "memo", nullable = true)
    private String memo;

    @Column(name = "place_name", nullable = true)
    private String placeName;

    @Column(name = "is_event", nullable = false)
    private Boolean isEvent;

    @Column(name = "is_all_day", nullable = false)
    private Boolean isAllDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void update(String title, LocalDateTime startTime, LocalDateTime endTime,
                       String memo, Boolean isAllDay, Color color, String placeName) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
        this.isAllDay = isAllDay;
        this.color = color;
        this.placeName = placeName;
    }
}
