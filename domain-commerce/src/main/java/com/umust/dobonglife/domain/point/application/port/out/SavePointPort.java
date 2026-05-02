package com.umust.dobonglife.domain.point.application.port.out;

import com.umust.dobonglife.domain.point.domain.entity.PointHistory;

public interface SavePointPort {

    PointHistory saveHistory(PointHistory pointHistory);
}
