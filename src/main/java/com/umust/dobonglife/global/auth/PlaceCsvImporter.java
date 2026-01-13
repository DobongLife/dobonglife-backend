//package com.umust.dobonglife.global.auth;
//
//import com.opencsv.CSVReader;
//import com.umust.dobonglife.domain.place.domain.constant.Amenity;
//import com.umust.dobonglife.domain.place.domain.entity.Place;
//
//import com.umust.dobonglife.domain.place.domain.repository.PlaceRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class PlaceCsvImporter implements CommandLineRunner {
//
//    private final PlaceRepository placeRepository;
//
//    // 한글 → enum 매핑
//    private static final Map<String, Amenity> AMENITY_KR_MAP = Map.of(
//            "주차장", Amenity.PARKING,
//            "화장실", Amenity.TOILET,
//            "분수대", Amenity.FOUNTAIN,
//            "벤치", Amenity.BENCH
//    );
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//        importPlaces("import/places.csv");
//    }
//
//    private void importPlaces(String classpath) throws Exception {
//        ClassPathResource resource = new ClassPathResource(classpath);
//        log.info("들어왔당께");
//
//        try (CSVReader reader = new CSVReader(
//                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
//        )) {
//            List<String[]> rows = reader.readAll();
//            if (rows.isEmpty()) return;
//
//            // 헤더 인덱스 매핑
//            String[] header = rows.get(0);
//            Map<String, Integer> idx = indexMap(header);
//
//            int inserted = 0;
//            int updated = 0;
//
//            for (int r = 1; r < rows.size(); r++) {
//                String[] row = rows.get(r);
//
//                String name = get(row, idx, "name");
//                if (name == null || name.isBlank()) continue;
//
//                String subName = defaultIfBlank(get(row, idx, "subName"), "");
//                String content = defaultIfBlank(get(row, idx, "content"), "");
//                String address = defaultIfBlank(get(row, idx, "address"), "");
//                String contact = defaultIfBlank(get(row, idx, "contact"), "정보없음");
//                String operatingHour = defaultIfBlank(get(row, idx, "operatingHour"), "정보없음");
//
//                List<Amenity> amenities = parseAmenities(get(row, idx, "amenities"));
//                List<String> imageUrls = parseList(get(row, idx, "imageUrls"));
//
//                Place place = placeRepository.findByName(name)
//                        .map(existing -> {
//                            existing.setSubName(subName);
//                            existing.setContent(content);
//                            existing.setAddress(address);
//                            existing.setContact(contact);
//                            existing.setOperatingHour(operatingHour);
//
//                            // ElementCollection은 "통째로 교체"가 제일 안전
//                            existing.setAmenities(amenities);
//                            existing.setImageUrls(imageUrls);
//                            return existing;
//                        })
//                        .orElseGet(() -> Place.builder()
//                                .name(name)
//                                .subName(subName)
//                                .content(content)
//                                .address(address)
//                                .contact(contact)
//                                .operatingHour(operatingHour)
//                                .amenities(amenities)
//                                .imageUrls(imageUrls)
//                                .build()
//                        );
//
//                boolean isNew = (place.getId() == null);
//                placeRepository.save(place);
//
//                if (isNew) inserted++;
//                else updated++;
//            }
//
//            System.out.print("[PlaceCsvImporter] inserted=" + inserted + ", updated=" + updated);
//        }
//    }
//
//    private Map<String, Integer> indexMap(String[] header) {
//        Map<String, Integer> map = new HashMap<>();
//        for (int i = 0; i < header.length; i++) {
//            String key = header[i].trim().replace("\uFEFF", ""); // BOM 제거
//            map.put(key, i);
//        }
//        return map;
//    }
//
//    private String get(String[] row, Map<String, Integer> idx, String key) {
//        Integer i = idx.get(key);
//        if (i == null || i >= row.length) return null;
//        String v = row[i];
//        return v == null ? null : v.trim();
//    }
//
//    private String defaultIfBlank(String v, String def) {
//        if (v == null || v.isBlank()) return def;
//        return v;
//    }
//
//    private List<String> parseList(String v) {
//        if (v == null || v.isBlank()) return new ArrayList<>();
//        return Arrays.stream(v.split("\\|"))
//                .map(String::trim)
//                .filter(s -> !s.isBlank())
//                .collect(Collectors.toCollection(ArrayList::new));
//    }
//
//    private List<Amenity> parseAmenities(String v) {
//        if (v == null || v.isBlank()) return new ArrayList<>();
//
//        // 1) enum 이름으로 들어온 경우: "PARKING|TOILET"
//        // 2) 한글로 들어온 경우: "주차장 / 화장실" 또는 "주차장|화장실"
//        String normalized = v.replace("/", "|").replace(" ", "");
//        String[] tokens = normalized.split("\\|");
//
//        List<Amenity> result = new ArrayList<>();
//        for (String t : tokens) {
//            if (t.isBlank()) continue;
//
//            // 1️enum 이름 그대로 시도
//            try {
//                result.add(Amenity.valueOf(t));
//                continue;
//            } catch (IllegalArgumentException ignore) {
//                // 다음 단계
//            }
//
//            // 2️한글 → enum 매핑 시도
//            Amenity mapped = AMENITY_KR_MAP.get(t);
//            if (mapped != null) {
//                result.add(mapped);
//            } else {
//                // 알 수 없는 값은 skip + 로그만 남김
//                log.warn("[PlaceCsvImporter] 알 수 없는 편의시설 값 skip: '{}'", t);
//            }
//        }
//        return result;
//    }
//}
