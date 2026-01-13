package com.umust.dobonglife.domain.business.controller;

import com.umust.dobonglife.domain.business.controller.dto.request.BusinessNumberRequest;
import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;
import com.umust.dobonglife.domain.business.service.BusinessService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    public BaseResponse<Void> registerBusiness(@CurrentUserId Long userId,
            @RequestBody BusinessRequest request) {
        businessService.registerBusiness(request, userId);
        return BaseResponse.ok(null);
    }
}
