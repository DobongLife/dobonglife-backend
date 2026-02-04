package com.umust.dobonglife.domain.mypage.controller;

import com.umust.dobonglife.domain.mypage.controller.dto.response.MyLikeResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPageSummaryManagerResponse;
import com.umust.dobonglife.domain.mypage.controller.dto.response.MyPageSummaryResponse;
import com.umust.dobonglife.domain.mypage.service.MyPageService;
import com.umust.dobonglife.global.auth.resolver.CurrentUserId;
import com.umust.dobonglife.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/my")
@Tag(name = "마이페이지 API", description = "마이페이지 관련 API")
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "마이 페이지 조회", description = "마이 페이지 view")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping
    public BaseResponse<MyPageSummaryResponse> viewMyPage(@CurrentUserId Long userId,
                                                          @RequestParam(defaultValue = "3") int size){
        MyPageSummaryResponse response = myPageService.getMyPageSummary(userId, size);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "찜 조회", description = "찜한 코스와 장소 조회")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @GetMapping("/like")
    public BaseResponse<MyLikeResponse> viewMyLike(@CurrentUserId Long userId,
                                                   @RequestParam(required = false, defaultValue = "3") Long lastId,
                                                   @RequestParam(defaultValue = "3") int size){
        MyLikeResponse response = myPageService.getMyLike(userId, size, lastId);
        return BaseResponse.ok(response);
    }

    @Operation(summary = "사업자 마이 페이지 조회", description = "사업자 마이 페이지 view")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공하였습니다."
    )
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager")
    public BaseResponse<MyPageSummaryManagerResponse> viewMyPageManager(@CurrentUserId Long userId,
                                                                        @RequestParam(defaultValue = "3") int size){
        return BaseResponse.ok(myPageService.getMyPageSummaryManager(userId, size));
    }
}
