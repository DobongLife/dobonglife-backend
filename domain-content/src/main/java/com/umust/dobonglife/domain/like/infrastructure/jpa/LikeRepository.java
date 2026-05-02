package com.umust.dobonglife.domain.like.infrastructure.jpa;

import com.umust.dobonglife.domain.like.domain.entity.Like;
import com.umust.dobonglife.domain.like.infrastructure.jpa.custom.LikeRepositoryCustom;
import com.umust.dobonglife.global.common.constant.TargetType;
import com.umust.dobonglife.global.common.model.BaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long>, LikeRepositoryCustom {

    Optional<Like> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    @Query("SELECT l.targetId FROM Like l WHERE l.userId = :userId AND l.targetType = :targetType")
    Set<Long> findTargetIdsByUserIdAndTargetType(@Param("userId") Long userId, @Param("targetType") TargetType targetType);

    void deleteAllByUserId(Long userId);

    @Modifying
    @Query("UPDATE Like l SET l.status = :newStatus WHERE l.userId = :userId AND l.status = :currentStatus")
    void updateStatusByUserId(@Param("userId") Long userId,
                              @Param("currentStatus") BaseStatus currentStatus,
                              @Param("newStatus") BaseStatus newStatus);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.userId = :userId AND l.status = :status")
    void deleteByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BaseStatus status);
}
