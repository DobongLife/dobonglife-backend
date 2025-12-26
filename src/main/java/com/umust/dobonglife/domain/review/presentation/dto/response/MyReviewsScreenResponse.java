package com.umust.dobonglife.domain.review.presentation.dto.response;

import java.util.List;

public record MyReviewsScreenResponse(long reviewNum,
                                      List<ReviewItem> reviewList) {
}
