package com.umust.dobonglife.domain.business.controller;

import com.umust.dobonglife.domain.business.controller.dto.request.BusinessNumberRequest;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.global.common.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping("/check")
    public BaseResponse<Void> checkBusiness(@RequestBody BusinessNumberRequest request) {
        businessService.checkBusinessStatus(request, 1L);
        return BaseResponse.ok(null);
    }
}
