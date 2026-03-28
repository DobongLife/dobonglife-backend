package com.umust.dobonglife.domain.business.application.port.out;

import com.umust.dobonglife.domain.business.domain.entity.Business;
import com.umust.dobonglife.global.common.constant.Category;

import java.util.Optional;

public interface LoadBusinessPort {

    boolean existsByUserId(Long userId);

    Optional<Business> findByUserId(Long userId);

    Optional<Category> findCategoryByUserId(Long userId);
}
