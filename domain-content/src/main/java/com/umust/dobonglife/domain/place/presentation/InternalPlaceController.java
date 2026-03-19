package com.umust.dobonglife.domain.place.presentation;

import com.umust.dobonglife.domain.like.application.LikeService;
import com.umust.dobonglife.domain.place.application.PlaceService;
import com.umust.dobonglife.domain.place.application.dto.PlaceSummaryResponse;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.port.dto.content.PlaceSummaryInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/internal/place")
@RequiredArgsConstructor
public class InternalPlaceController {

    private final PlaceService placeService;
    private final LikeService likeService;

    @GetMapping("/active")
    public List<PlaceSummaryInfo> getAllActivePlaces(@RequestParam Long userId) {
        List<PlaceSummaryResponse> places = placeService.getAllActivePlaces().stream()
                .map(PlaceSummaryResponse::from)
                .toList();

        Set<Long> likedPlaceIds = likeService.getLikedTargetIds(userId, TargetType.PLACE);

        return places.stream()
                .map(p -> new PlaceSummaryInfo(
                        p.placeId(), p.placeName(), p.category(), p.thumbnailUrl(),
                        p.averageRating(), p.reviewCount(),
                        likedPlaceIds.contains(p.placeId()),
                        p.latitude(), p.longitude(),
                        p.themes(), p.status().name()))
                .toList();
    }
}
