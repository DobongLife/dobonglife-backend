package com.umust.dobonglife.global.importer;

import com.opencsv.CSVReader;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
import com.umust.dobonglife.domain.place.domain.constant.PlaceCategory;
import com.umust.dobonglife.domain.place.domain.entity.Place;
import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test & !local")
public class PlaceCsvImporter implements CommandLineRunner {

    private final PlaceRepository placeRepository;
    private final ResourceLoader resourceLoader;

    private static final Map<String, Amenity> AMENITY_KR_MAP = Map.of(
            "주차장", Amenity.PARKING,
            "화장실", Amenity.TOILET,
            "분수대", Amenity.FOUNTAIN,
            "벤치", Amenity.BENCH
    );

    private static final Map<String, CourseTheme> THEME_KR_MAP = Map.of(
            "맛집탐방", CourseTheme.RESTAURANT,
            "문화체험", CourseTheme.CULTURE,
            "역사여행", CourseTheme.HISTORY,
            "가족나들이", CourseTheme.FAMILY,
            "액티비티", CourseTheme.ACTIVITY,
            "자연힐링", CourseTheme.NATURE
    );

    private static final Map<String, PlaceCategory> PLACE_CATEGORY_KR_MAP = Map.ofEntries(
            Map.entry("음식점", PlaceCategory.RESTAURANT),
            Map.entry("카페", PlaceCategory.CAFE),
            Map.entry("쇼핑", PlaceCategory.SHOPPING),
            Map.entry("의료/IT", PlaceCategory.MEDICAL),
            Map.entry("뷰티", PlaceCategory.BEAUTY),
            Map.entry("피트니스", PlaceCategory.FITNESS),
            Map.entry("명소", PlaceCategory.LANDMARK),
            Map.entry("기타", PlaceCategory.ETC)
    );

    @Value("${place.import.path:classpath:import/places.csv}")
    private String importPath;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        importPlaces(importPath);
    }

    private void importPlaces(String classpath) throws Exception {
        Resource resource = resourceLoader.getResource(classpath);
        log.info("[PlaceCsvImporter] start import: {}", classpath);

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        )) {
            List<String[]> rows = reader.readAll();
            if (rows.isEmpty()) return;

            String[] header = rows.get(0);
            Map<String, Integer> idx = indexMap(header);

            int inserted = 0;
            int updated = 0;

            for (int r = 1; r < rows.size(); r++) {
                String[] row = rows.get(r);

                String name = get(row, idx, "name");
                if (name == null || name.isBlank()) continue;

                // 여기서는 "raw"로 받아두고
                String subNameRaw = get(row, idx, "subName");
                String contentRaw = get(row, idx, "content");
                String addressRaw = get(row, idx, "address");
                String contactRaw = get(row, idx, "contact");
                String operatingHourRaw = get(row, idx, "operatingHour");

                String categoryRaw = get(row, idx, "category");         // 빈 값이면 업데이트 안 함(기존 유지)
                String amenitiesRaw = get(row, idx, "amenities");       // 빈 값이면 업데이트 안 함(기존 유지)
                String imageUrlsRaw = get(row, idx, "imageUrls");       // 빈 값이면 업데이트 안 함(기존 유지)
                String thumbnailUrlRaw = get(row, idx, "thumbnailUrl"); // 빈 값이면 업데이트 안 함(기존 유지)
                String latitudeRaw = get(row, idx, "latitude");         // 빈 값이면 업데이트 안 함(기존 유지)
                String longitudeRaw = get(row, idx, "longitude");       // 빈 값이면 업데이트 안 함(기존 유지)
                String themesRaw = get(row, idx, "themes");             // 빈 값이면 업데이트 안 함(기존 유지)

                // 새 엔티티 생성 시에는 기본값을 채워주는 게 일반적으로 안전
                String subNameNew = defaultIfBlank(subNameRaw, "");
                String contentNew = defaultIfBlank(contentRaw, "");
                String addressNew = defaultIfBlank(addressRaw, "");
                String contactNew = defaultIfBlank(contactRaw, "정보없음");
                String operatingHourNew = defaultIfBlank(operatingHourRaw, "정보없음");

                PlaceCategory categoryNew = parsePlaceCategory(defaultIfBlank(categoryRaw, "명소"));
                List<Amenity> amenitiesNew = parseAmenities(defaultIfBlank(amenitiesRaw, ""));
                List<String> imageUrlsNew = parseUrlList(defaultIfBlank(imageUrlsRaw, ""));
                String thumbnailUrlNew = defaultIfBlank(thumbnailUrlRaw, "");
                Double latitudeNew = parseDoubleOrNull(defaultIfBlank(latitudeRaw, ""));
                Double longitudeNew = parseDoubleOrNull(defaultIfBlank(longitudeRaw, ""));
                List<CourseTheme> themesNew = parseThemes(defaultIfBlank(themesRaw, ""));

                Place place = placeRepository.findByName(name)
                        .map(existing -> {
                            // ✅ 기존 엔티티 업데이트는 "CSV 값이 있을 때만" set 한다.

                            applyIfPresent(subNameRaw, existing::setSubName);
                            applyIfPresent(contentRaw, existing::setContent);
                            applyIfPresent(addressRaw, existing::setAddress);

                            // contact/operatingHour도 빈 값이면 유지. (원하면 빈 값이면 '정보없음'으로 강제도 가능)
                            applyIfPresent(contactRaw, existing::setContact);
                            applyIfPresent(operatingHourRaw, existing::setOperatingHour);

                            // 리스트 계열: 빈 값이면 업데이트 안 함, 값 있으면 파싱해서 업데이트
                            applyIfNotEmpty(amenitiesRaw, v -> existing.setAmenities(parseAmenities(v)));
                            applyIfNotEmpty(imageUrlsRaw, v -> existing.setImageUrls(parseUrlList(v)));
                            applyIfPresent(thumbnailUrlRaw, existing::setThumbnailUrl);

                            // 위경도: 값이 있을 때만 파싱해서 set
                            applyIfNotEmpty(latitudeRaw, v -> existing.setLatitude(parseDoubleOrNull(v)));
                            applyIfNotEmpty(longitudeRaw, v -> existing.setLongitude(parseDoubleOrNull(v)));

                            // 테마/카테고리
                            applyIfNotEmpty(themesRaw, v -> existing.setThemes(parseThemes(v)));
                            applyIfPresent(categoryRaw, v -> existing.setCategory(parsePlaceCategory(v)));

                            return existing;
                        })
                        .orElseGet(() -> Place.builder()
                                .name(name)
                                .subName(subNameNew)
                                .content(contentNew)
                                .address(addressNew)
                                .contact(contactNew)
                                .operatingHour(operatingHourNew)
                                .amenities(amenitiesNew)
                                .imageUrls(imageUrlsNew)
                                .thumbnailUrl(thumbnailUrlNew)
                                .latitude(latitudeNew)
                                .longitude(longitudeNew)
                                .themes(themesNew)
                                .category(categoryNew)
                                .build()
                        );

                boolean isNew = (place.getId() == null);
                placeRepository.save(place);

                if (isNew) inserted++;
                else updated++;
            }

            log.info("[PlaceCsvImporter] done. inserted={}, updated={}", inserted, updated);
        }
    }

    // =========================
    // ✅ "빈 값이면 기존 값 유지" 유틸
    // =========================

    private void applyIfPresent(String raw, Consumer<String> setter) {
        if (raw == null) return;
        String v = raw.trim();
        if (v.isBlank()) return;
        setter.accept(v);
    }

    private void applyIfNotEmpty(String raw, Consumer<String> setter) {
        // 리스트/숫자도 결국 문자열 기반이라 동일하게 처리
        applyIfPresent(raw, setter);
    }

    // =========================
    // 기존 유틸/파서
    // =========================

    private PlaceCategory parsePlaceCategory(String raw) {
        if (raw == null || raw.isBlank()) return PlaceCategory.LANDMARK;

        String v = raw.trim();

        try {
            return PlaceCategory.valueOf(v.toUpperCase());
        } catch (IllegalArgumentException ignore) { }

        PlaceCategory mapped = PLACE_CATEGORY_KR_MAP.get(v);
        if (mapped != null) return mapped;

        for (PlaceCategory c : PlaceCategory.values()) {
            if (c.getValue().equals(v)) return c;
        }

        log.warn("[PlaceCsvImporter] 알 수 없는 category 값 '{}', ETC로 저장", v);
        return PlaceCategory.ETC;
    }

    private Map<String, Integer> indexMap(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            String key = header[i].trim().replace("\uFEFF", "");
            map.put(key, i);
        }
        return map;
    }

    private String get(String[] row, Map<String, Integer> idx, String key) {
        Integer i = idx.get(key);
        if (i == null || i >= row.length) return null;
        String v = row[i];
        return v == null ? null : v.trim();
    }

    private String defaultIfBlank(String v, String def) {
        if (v == null || v.isBlank()) return def;
        return v.trim();
    }

    private List<String> parseUrlList(String v) {
        if (v == null || v.isBlank()) return new ArrayList<>();

        String normalized = v.trim();
        String[] tokens = normalized.split("[|;]");

        return Arrays.stream(tokens)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Double parseDoubleOrNull(String v) {
        if (v == null || v.isBlank()) return null;
        try {
            return Double.parseDouble(v.trim());
        } catch (NumberFormatException e) {
            log.warn("[PlaceCsvImporter] 위경도 파싱 실패: '{}'", v);
            return null;
        }
    }

    private List<Amenity> parseAmenities(String v) {
        if (v == null || v.isBlank()) return new ArrayList<>();

        String normalized = v.replace(" / ", "|")
                .replace("/", "|")
                .replace(",", "|")
                .replace(" ", "");

        String[] tokens = normalized.split("\\|");

        List<Amenity> result = new ArrayList<>();
        for (String t : tokens) {
            if (t == null || t.isBlank()) continue;

            try {
                result.add(Amenity.valueOf(t));
                continue;
            } catch (IllegalArgumentException ignore) { }

            Amenity mapped = AMENITY_KR_MAP.get(t);
            if (mapped != null) result.add(mapped);
            else log.warn("[PlaceCsvImporter] 알 수 없는 편의시설 값 skip: '{}'", t);
        }
        return result;
    }

    private List<CourseTheme> parseThemes(String v) {
        if (v == null || v.isBlank()) return new ArrayList<>();

        String normalized = v.replace(" / ", "|")
                .replace("/", "|")
                .replace(",", "|")
                .replace(" ", "");

        String[] tokens = normalized.split("\\|");

        List<CourseTheme> result = new ArrayList<>();
        for (String t : tokens) {
            if (t == null || t.isBlank()) continue;

            try {
                result.add(CourseTheme.valueOf(t));
                continue;
            } catch (IllegalArgumentException ignore) { }

            CourseTheme mapped = THEME_KR_MAP.get(t);
            if (mapped != null) result.add(mapped);
            else log.warn("[PlaceCsvImporter] 알 수 없는 테마 값 skip: '{}'", t);
        }
        return result;
    }
}
