package com.umust.dobonglife.global.composition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.global.port.content.PlacePort;
import com.umust.dobonglife.global.port.dto.content.PlaceSummaryInfo;
import com.umust.dobonglife.infra.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceListFacade {

    private final PlacePort placePort;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    private static final String PLACE_CACHE_KEY = "places:active";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    public List<PlaceSummaryInfo> getAllPlaces(Long userId) {
        return redisService.getValues(PLACE_CACHE_KEY)
                .map(this::deserialize)
                .orElseGet(() -> loadAndCache(userId));
    }

    private List<PlaceSummaryInfo> loadAndCache(Long userId) {
        List<PlaceSummaryInfo> places = placePort.getAllActivePlaces(userId);
        redisService.setValues(PLACE_CACHE_KEY, serialize(places), CACHE_TTL);
        return places;
    }

    private String serialize(List<PlaceSummaryInfo> places) {
        try {
            return objectMapper.writeValueAsString(places);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("장소 캐시 직렬화 실패", e);
        }
    }

    private List<PlaceSummaryInfo> deserialize(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.warn("장소 캐시 역직렬화 실패, 캐시를 삭제하고 재로드합니다.", e);
            redisService.delete(PLACE_CACHE_KEY);
            return placePort.getAllActivePlaces(null);
        }
    }
}
