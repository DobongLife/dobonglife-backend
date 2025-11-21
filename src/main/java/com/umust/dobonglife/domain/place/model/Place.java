package com.umust.dobonglife.domain.place.model;

import com.umust.dobonglife.domain.auth.model.Provider;
import com.umust.dobonglife.domain.user.model.Role;
import com.umust.dobonglife.global.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", nullable = false)
    private Long id;

    @Column
    private String name;

    @Column
    private String content;

//    @Column
//    @Enumerated(EnumType.STRING)
//    private Amentity amentity;

    @Column
    private String address;

    @Column
    private String contact;

    @Column
    private String image;

    @Column
    private LocalDateTime operatingHour;
}
