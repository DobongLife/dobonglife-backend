package com.umust.dobonglife.global.importer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TODO: domain-place, domain-course 모듈 마이그레이션 후 Place/CourseTheme 의존성 복원
 * 기존 구현은 src/main/java/.../global/importer/PlaceCsvImporter.java 참고
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test & !local")
public class PlaceCsvImporter implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("[PlaceCsvImporter] 비활성 상태 - domain-place 모듈 마이그레이션 후 활성화");
    }
}
