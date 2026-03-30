package com.umust.dobonglife.domain.like.application;

import com.umust.dobonglife.domain.like.application.port.in.ToggleLikeUseCase;
import com.umust.dobonglife.domain.like.application.port.out.LoadLikePort;
import com.umust.dobonglife.domain.like.application.port.out.SaveLikePort;
import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.global.common.constant.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeCommandService implements ToggleLikeUseCase {

    private final LoadLikePort loadLikePort;
    private final SaveLikePort saveLikePort;

    @Override
    public boolean toggleLike(Long userId, TargetType targetType, Long targetId) {
        return loadLikePort.findByUserAndTarget(userId, targetType, targetId)
                .map(like -> {
                    saveLikePort.delete(like);
                    return false;
                })
                .orElseGet(() -> {
                    try {
                        saveLikePort.save(Like.of(userId, targetType, targetId));
                        return true;
                    } catch (DataIntegrityViolationException e) {
                        return true;
                    }
                });
    }
}
