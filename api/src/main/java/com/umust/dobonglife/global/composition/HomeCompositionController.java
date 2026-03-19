package com.umust.dobonglife.global.composition;

import com.umust.dobonglife.global.common.constant.PageSizeType;
import com.umust.dobonglife.global.common.response.BaseResponse;
import com.umust.dobonglife.global.composition.dto.response.HomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeCompositionController {

    private final HomeFacade homeFacade;

    @GetMapping
    public ResponseEntity<BaseResponse<HomeResponse>> getHome(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = PageSizeType.PROMOTION) int size) {
        return ResponseEntity.ok(BaseResponse.ok(homeFacade.getHome(lastId, size)));
    }
}
