package com.umust.dobonglife.domain.schedule.application.port.in;

import com.umust.dobonglife.domain.schedule.application.dto.UpcomingFestivalDetail;

import java.util.List;

public interface FestivalUseCase {

    List<UpcomingFestivalDetail> getUpcomingTop3();
}
