package com.umust.dobonglife.content.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.place.application.PlaceService;
import com.umust.dobonglife.domain.place.application.dto.PlaceSummaryResponse;
import com.umust.dobonglife.domain.place.exception.PlaceErrorCode;
import com.umust.dobonglife.domain.place.exception.PlaceException;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.infra.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceListFacade {

    private final PlaceService placeService;
    private final LikeService likeService;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    private static final String PLACE_CACHE_KEY = "places:active";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    public List<PlaceSummaryResponse> getAllPlaces(Long userId) {
        List<PlaceSummaryResponse> places = getCachedPlaces();
        Set<Long> likedPlaceIds = likeService.getLikedTargetIds(userId, TargetType.PLACE);
        return places.stream()
                .map(p -> likedPlaceIds.contains(p.placeId()) ? p.withLiked(true) : p)
                .toList();
    }

    private List<PlaceSummaryResponse> getCachedPlaces() {
        return redisService.getValues(PLACE_CACHE_KEY)
                .map(this::deserialize)
                .orElseGet(this::loadAndCache);
    }

    private List<PlaceSummaryResponse> loadAndCache() {
        List<PlaceSummaryResponse> places = placeService.getAllActivePlaces().stream()
                .map(PlaceSummaryResponse::from).toList();
        redisService.setValues(PLACE_CACHE_KEY, serialize(places), CACHE_TTL);
        return places;
    }

    private String serialize(List<PlaceSummaryResponse> places) {
        try { return objectMapper.writeValueAsString(places); }
        catch (JsonProcessingException e) { throw new PlaceException(PlaceErrorCode.PLACE_CACHE_ERROR); }
    }

    private List<PlaceSummaryResponse> deserialize(String json) {
        try { return objectMapper.readValue(json, new TypeReference<>() {}); }
        catch (JsonProcessingException e) {
            log.warn("장소 캐시 역직렬화 실패, 재로드", e);
            redisService.delete(PLACE_CACHE_KEY);
            return loadAndCache();
        }
    }
}
