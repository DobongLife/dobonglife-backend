package com.umust.dobonglife.global.importer;

import com.opencsv.CSVReader;
import com.umust.dobonglife.domain.place.domain.constant.Amenity;
import com.umust.dobonglife.domain.course.domain.constant.CourseTheme;
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

            // 헤더 인덱스 매핑
            String[] header = rows.get(0);
            Map<String, Integer> idx = indexMap(header);

            int inserted = 0;
            int updated = 0;

            for (int r = 1; r < rows.size(); r++) {
                String[] row = rows.get(r);

                String name = get(row, idx, "name");
                if (name == null || name.isBlank()) continue;

                String subName = defaultIfBlank(get(row, idx, "subName"), "");
                String content = defaultIfBlank(get(row, idx, "content"), "");
                String address = defaultIfBlank(get(row, idx, "address"), "");
                String contact = defaultIfBlank(get(row, idx, "contact"), "정보없음");
                String operatingHour = defaultIfBlank(get(row, idx, "operatingHour"), "정보없음");
                String category = defaultIfBlank(get(row, idx, "category"), "명소");
                List<Amenity> amenities = parseAmenities(get(row, idx, "amenities"));
                List<String> imageUrls = parseUrlList(get(row, idx, "imageUrls"));
                String thumbnailUrl = defaultIfBlank(get(row, idx, "thumbnailUrl"), "");
                Double latitude = parseDoubleOrNull(get(row, idx, "latitude"));
                Double longitude = parseDoubleOrNull(get(row, idx, "longitude"));
                List<CourseTheme> themes = parseThemes(get(row, idx, "themes"));

                Place place = placeRepository.findByName(name)
                        .map(existing -> {
                            existing.setSubName(subName);
                            existing.setContent(content);
                            existing.setAddress(address);
                            existing.setContact(contact);
                            existing.setOperatingHour(operatingHour);
                            existing.setAmenities(amenities);
                            existing.setImageUrls(imageUrls);
                            existing.setThumbnailUrl(thumbnailUrl);
                            existing.setLatitude(latitude);
                            existing.setLongitude(longitude);
                            existing.setThemes(themes);
                            existing.setCategory(category);

                            return existing;
                        })
                        .orElseGet(() -> Place.builder()
                                .name(name)
                                .subName(subName)
                                .content(content)
                                .address(address)
                                .contact(contact)
                                .operatingHour(operatingHour)
                                .amenities(amenities)
                                .imageUrls(imageUrls)
                                .thumbnailUrl(thumbnailUrl)
                                .latitude(latitude)
                                .longitude(longitude)
                                .themes(themes)
                                .category(category)
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

    private Map<String, Integer> indexMap(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            String key = header[i].trim().replace("\uFEFF", ""); // BOM 제거
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

    /**
     * imageUrls: 단일 URL도 OK.
     * 여러 개인 경우: "url1|url2" 또는 "url1;url2" 지원.
     */
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

            // 1) enum 이름 그대로 시도
            try {
                result.add(Amenity.valueOf(t));
                continue;
            } catch (IllegalArgumentException ignore) { }

            // 2) 한글 → enum 매핑
            Amenity mapped = AMENITY_KR_MAP.get(t);
            if (mapped != null) result.add(mapped);
            else log.warn("[PlaceCsvImporter] 알 수 없는 편의시설 값 skip: '{}'", t);
        }
        return result;
    }

    // themes 파서 추가 (amenities랑 동일 스타일)
    private List<CourseTheme> parseThemes(String v) {
        if (v == null || v.isBlank()) return new ArrayList<>();

        // "맛집탐방|문화체험" or "FOOD_TRIP|CULTURE"
        String normalized = v.replace(" / ", "|")
                .replace("/", "|")
                .replace(",", "|")
                .replace(" ", ""); // 공백 제거 (맛집 탐방 같은 입력 대비)

        String[] tokens = normalized.split("\\|");

        List<CourseTheme> result = new ArrayList<>();
        for (String t : tokens) {
            if (t == null || t.isBlank()) continue;

            // 1) enum 이름 그대로 시도
            try {
                result.add(CourseTheme.valueOf(t));
                continue;
            } catch (IllegalArgumentException ignore) { }

            // 2) 한글 → enum 매핑
            CourseTheme mapped = THEME_KR_MAP.get(t);
            if (mapped != null) {
                result.add(mapped);
            } else {
                log.warn("[PlaceCsvImporter] 알 수 없는 테마 값 skip: '{}'", t);
            }
        }
        return result;
    }
}
