package com.umust.dobonglife.commerce.adapter;

import com.umust.dobonglife.global.common.constant.Category;
import com.umust.dobonglife.global.port.BusinessPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * BusinessPort adapter for commerce-service.
 * In MSA, business data is in user-service, so this calls user-service via REST.
 * For now, delegates to the existing BusinessService if domain-user is available,
 * or calls user-service REST API.
 */
@Component
@RequiredArgsConstructor
public class BusinessPortAdapter implements BusinessPort {

    // TODO: Implement REST call to user-service for business operations
    // For now, this is a placeholder. In full MSA, business-related tables
    // would need to be accessible via user-service REST API.

    @Override
    public boolean checkBusiness(Long userId) {
        // Placeholder - needs user-service REST call
        return true;
    }

    @Override
    public Category getBusinessCategory(Long userId) {
        // Placeholder - needs user-service REST call
        return Category.RESTAURANT;
    }
}
