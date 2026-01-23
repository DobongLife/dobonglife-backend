package com.umust.dobonglife.global.importer;

import com.umust.dobonglife.domain.schedule.domain.entity.Festival;
import com.umust.dobonglife.domain.schedule.domain.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class FestivalCsvImporter implements ApplicationRunner {

    private final FestivalRepository festivalRepository;
    private final ResourceLoader resourceLoader;

    @Value("${festival.import.path:classpath:import/festivals.csv}")
    private String resourcePath;

    // "2026-01-19 9:00" 같은 단자리 시간(H) 허용
    private static final DateTimeFormatter DATE_TIME_FMT =
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd ")
                    .appendPattern("H:mm")
                    .toFormatter();

    private static final int BATCH_SIZE = 300;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        Resource resource = resourceLoader.getResource(resourcePath);
        if (!resource.exists()) {
            log.warn("[FestivalCsvImporter] CSV not found: {}", resourcePath);
            return;
        }

        int inserted = 0;
        int skipped = 0;
        int failed = 0;

        List<Festival> buffer = new ArrayList<>(BATCH_SIZE);

        try (BOMInputStream bomIn = BOMInputStream.builder()
                .setInputStream(resource.getInputStream())
                .get();
             InputStreamReader reader = new InputStreamReader(bomIn, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord r : parser) {
                try {
                    String title = r.get("title");
                    String startRaw = r.get("startDateTime");
                    String endRaw = r.get("endDateTime");
                    String url = r.get("url");
                    String placeName = r.get("placeName");

                    // 필수값 방어 (CSV가 더럽게 들어오는 경우 대비)
                    if (isBlank(title) || isBlank(startRaw) || isBlank(endRaw) || isBlank(url) || isBlank(placeName)) {
                        failed++;
                        log.warn("[FestivalCsvImporter] skip(row={}): required field missing", r.getRecordNumber());
                        continue;
                    }

                    LocalDateTime start = LocalDateTime.parse(startRaw, DATE_TIME_FMT);
                    LocalDateTime end = LocalDateTime.parse(endRaw, DATE_TIME_FMT);

                    // 중복 방지 (동일 title+start+place)
                    if (festivalRepository.existsByTitleAndStartDateTimeAndPlaceName(title, start, placeName)) {
                        skipped++;
                        continue;
                    }

                    buffer.add(Festival.builder()
                            .title(title)
                            .startDateTime(start)
                            .endDateTime(end)
                            .url(url)
                            .placeName(placeName)
                            .build());

                    if (buffer.size() >= BATCH_SIZE) {
                        festivalRepository.saveAll(buffer);
                        inserted += buffer.size();
                        buffer.clear();
                    }
                } catch (Exception e) {
                    failed++;
                    log.warn("[FestivalCsvImporter] fail(row={}): {}", r.getRecordNumber(), e.getMessage());
                }
            }
        }

        if (!buffer.isEmpty()) {
            festivalRepository.saveAll(buffer);
            inserted += buffer.size();
            buffer.clear();
        }

        log.info("[FestivalCsvImporter] done. inserted={}, skipped={}, failed={}, resource={}",
                inserted, skipped, failed, resourcePath);
    }

    private String stripBom(String s) {
        if (s == null) return null;
        return s.replace("\uFEFF", "").trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
