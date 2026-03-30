package com.umust.dobonglife.domain.like.application.port.out;

import com.umust.dobonglife.domain.like.domain.entity.Like;

public interface SaveLikePort {

    Like save(Like like);

    void delete(Like like);
}
