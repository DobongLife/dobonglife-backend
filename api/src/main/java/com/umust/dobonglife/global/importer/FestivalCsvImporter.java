package com.umust.dobonglife.global.importer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TODO: domain-schedule 모듈 마이그레이션 후 Festival/FestivalRepository 의존성 복원
 * 기존 구현은 src/main/java/.../global/importer/FestivalCsvImporter.java 참고
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test & !local")
public class FestivalCsvImporter implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("[FestivalCsvImporter] 비활성 상태 - domain-schedule 모듈 마이그레이션 후 활성화");
    }
}
