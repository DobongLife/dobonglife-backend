package com.umust.dobonglife.domain.test.controller;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;
}
