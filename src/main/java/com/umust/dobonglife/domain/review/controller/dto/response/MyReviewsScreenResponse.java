package com.umust.dobonglife.domain.review.controller.dto.response;

import java.util.List;

public record MyReviewsScreenResponse(long reviewNum,
                                      List<ReviewItem> reviewList) {
}
