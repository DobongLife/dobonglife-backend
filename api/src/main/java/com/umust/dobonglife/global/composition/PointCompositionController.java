package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.annotation.CurrentUserId;
import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.composition.dto.response.PointPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/composition/point")
public class PointCompositionController {

    private final PointPromotionFacade pointPromotionFacade;

    @GetMapping
    public ResponseEntity<BaseResponse<PointPageResponse>> getPointPage(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PROMOTION_AD) int size) {
        return ResponseEntity.ok(BaseResponse.ok(pointPromotionFacade.getPointPage(userId, lastId, size)));
    }
}
