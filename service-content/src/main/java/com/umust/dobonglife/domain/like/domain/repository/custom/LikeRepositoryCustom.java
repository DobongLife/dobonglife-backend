package com.umust.dobonglife.domain.like.domain.repository.custom;

import com.umust.dobonglife.domain.like.application.dto.MyLikedCourseResponse;
import com.umust.dobonglife.domain.like.application.dto.MyLikedPlaceResponse;
import org.springframework.data.domain.Slice;

public interface LikeRepositoryCustom {

    Slice<MyLikedPlaceResponse> findMyLikedPlaces(Long userId, Long lastId, int size);

    Slice<MyLikedCourseResponse> findMyLikedCourses(Long userId, Long lastId, int size);
}
