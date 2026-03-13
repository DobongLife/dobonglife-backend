package com.umust.dobonglife.global.port;

import com.umust.dobonglife.global.common.constant.Category;

public interface BusinessPort {

    boolean checkBusiness(Long userId);

    Category getBusinessCategory(Long userId);
}
