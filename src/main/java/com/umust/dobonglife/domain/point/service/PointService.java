package com.umust.dobonglife.domain.point.service;

import com.umust.dobonglife.domain.point.controller.dto.response.PointResponse;
import com.umust.dobonglife.domain.point.domain.repository.PointRepository;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import com.umust.dobonglife.global.common.response.slice.Cursor;
import com.umust.dobonglife.global.common.response.slice.SliceResponse;
import com.umust.dobonglife.global.common.response.slice.SortOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 포인트 계산 결과 필드를 User 엔티티에 두는 이유
 * 비록 포인트가 갱신될때마다 필드로 갱신해줘야 되서 쿼리가 1개 늘어나긴 하지만,
 * 대부분의 서비스는 쓰기보다 조회의 경우가 더 많다.
 * 따라서 포인트 내역이 매우 많을 때는 sum으로 집계하는 것보다
 * User 엔티티에서 그냥 칼럼 하나로 조회하는 게 더 효율적이다.
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public SliceResponse<PointResponse> getPoints(
            Long userId,
            int size,
            String cursor,
            String order
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Cursor parsedCursor = Cursor.from(cursor);
        SortOrder parsedOrder = SortOrder.from(order);

        return pointRepository.findPointsByCursor(
                userId,
                size,
                parsedCursor,
                parsedOrder
        );
    }
}
