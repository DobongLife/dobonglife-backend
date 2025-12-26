package com.umust.dobonglife.domain.review.presentation.dto.response;

public record MyReviewActivity(int totalReviews, // TODO: Integer? int?
                               int totalLikes,
                               int totalHelps,
                               double averageRating) {
}
