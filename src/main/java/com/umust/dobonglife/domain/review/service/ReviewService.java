package com.umust.dobonglife.domain.review.service;

import com.umust.dobonglife.domain.place.model.Amenity;
import com.umust.dobonglife.domain.place.model.Place;
import com.umust.dobonglife.domain.place.repository.PlaceRepository;
import com.umust.dobonglife.domain.review.dto.request.ReviewRegisterRequest;
import com.umust.dobonglife.domain.review.model.Review;
import com.umust.dobonglife.domain.review.model.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final PlaceRepository placeRepository;

    public void registerReview(ReviewRegisterRequest request) {
        Review review = Review.builder()
                // .reviewImages()
                .title(request.getTitle())
                .rating(request.getRating())
                .templates(request.getTemplates()
                        .stream()
                        .map(Template::toEnum)
                        .toList())
                .build();

        if(request.getPlaceId()!=null) {
            Place place = placeRepository.findById(request.getPlaceId())
                    .orElse(null);
            review.setPlace(place);
        }

        if(request.getCourseId()!=null) {
            Place place = placeRepository.findById(request.getPlaceId())
                    .orElse(null);
            review.setPlace(place);
        }

        reviewR
    }



}
