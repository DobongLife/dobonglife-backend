//package com.umust.dobonglife.domain.point.controller;
//
//import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
//import com.umust.dobonglife.domain.point.service.PointService;
//import com.umust.dobonglife.global.common.resolver.CurrentUserId;
//import com.umust.dobonglife.global.common.response.BaseResponse;
//import com.umust.dobonglife.global.common.response.slice.SliceResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/point")
//@RequiredArgsConstructor
//public class PointController {
//
//    private final PointService pointService;
//
//    @GetMapping("/points")
//    public BaseResponse<SliceResponse<PointResponse>> getMyPoints(
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(required = false) String cursor,
//            @CurrentUserId Long userId
//    ) {
//        return BaseResponse.ok(
//                pointService.getMyPoint(userId, size, cursor)
//        );
//    }
//}
