package com.umust.dobonglife.domain.business.service;


import com.umust.dobonglife.domain.business.controller.dto.request.BusinessNumberRequest;
import com.umust.dobonglife.domain.user.domain.constant.Role;
import com.umust.dobonglife.domain.user.domain.entity.User;
import com.umust.dobonglife.domain.user.domain.repository.UserRepository;
import com.umust.dobonglife.global.common.exception.BusinessException;
import com.umust.dobonglife.global.common.response.ErrorCode;
import com.umust.dobonglife.global.common.webclient.business.parser.BusinessStatusParser;
import com.umust.dobonglife.global.common.webclient.service.WebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umust.dobonglife.domain.business.controller.dto.request.BusinessRequest;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final UserRepository userRepository;
    private final WebClientService webClientService;
    private final BusinessStatusParser parser;

    private static final String VALID_CODE = "01";

    @Transactional
    public void checkBusinessStatus(BusinessNumberRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Map<String, Object> response = webClientService.getCompanyStatus(request.getBusinessNumber());

        String statusCode = parser.extractStatusCode(response);

        if (!VALID_CODE.equals(statusCode)) {
            throw new IllegalStateException("유효하지 않은 사업자 번호입니다.");
        }

        return;
        // user.setRole(Role.MANAGER);
    }

    @Transactional
    public void registerBusiness(BusinessRequest request, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));







    }


}
