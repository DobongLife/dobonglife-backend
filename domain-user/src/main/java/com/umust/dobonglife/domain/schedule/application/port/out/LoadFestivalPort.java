package com.umust.dobonglife.domain.schedule.application.port.out;

import com.umust.dobonglife.domain.schedule.domain.entity.Festival;

import java.time.LocalDateTime;
import java.util.List;

public interface LoadFestivalPort {

    List<Festival> findUpcoming(LocalDateTime from, int limit);
}
