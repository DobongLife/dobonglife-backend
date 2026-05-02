package com.umust.dobonglife.domain.place.presentation;

import com.umust.dobonglife.domain.like.application.port.in.GetLikeUseCase;
import com.umust.dobonglife.domain.place.application.port.in.GetPlaceUseCase;
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

    private final GetPlaceUseCase getPlaceUseCase;
    private final GetLikeUseCase getLikeUseCase;

    @GetMapping("/active")
    public List<PlaceSummaryInfo> getAllActivePlaces(@RequestParam Long userId) {
        List<PlaceSummaryResponse> places = getPlaceUseCase.getAllActivePlaces().stream()
                .map(PlaceSummaryResponse::from)
                .toList();

        Set<Long> likedPlaceIds = getLikeUseCase.getLikedTargetIds(userId, TargetType.PLACE);

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
