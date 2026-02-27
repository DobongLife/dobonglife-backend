package com.umust.dobonglife.domain.business.application;

import com.umust.dobonglife.domain.business.domain.repository.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessService {
    private final BusinessRepository businessRepository;

    public boolean checkBusiness(Long userId){
        return businessRepository.existsByUserId(userId);
    }
}
