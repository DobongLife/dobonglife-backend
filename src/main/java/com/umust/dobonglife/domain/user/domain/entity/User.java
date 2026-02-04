package com.umust.dobonglife.domain.user.domain.entity;

import com.umust.dobonglife.domain.auth.domain.constant.Provider;
import com.umust.dobonglife.domain.user.domain.constant.Role;

import com.umust.dobonglife.global.common.model.BaseEntity;
import com.umust.dobonglife.global.error.ErrorCode;
import com.umust.dobonglife.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter @Setter
@SQLDelete(sql = "UPDATE users SET status = 'INACTIVE' WHERE user_id = ?")
@SQLRestriction("status IN ('ACTIVE')")
public class User extends BaseEntity {

    private static final int PENALTY_THRESHOLD = 3;
    private static final long PENALTY_DAYS = 7;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column
    private String email;

    @Column
    private String name;

    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "DATE")
    private LocalDate birthday;

    @Column(length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(nullable = false)
    @Builder.Default
    private long balance = 0L;

    @Column(nullable = false)
    @Builder.Default
    private int deleteCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isBlocked = false;

    @Column(nullable = true)
    private LocalDateTime blockedAt;

    @Column(nullable = true)
    private String fcmToken;

    @Column(nullable = false)
    @Builder.Default
    private boolean isReceivedAlarm = true;

    public void handleDeletion() {
        this.deleteCount++;
        if (this.deleteCount >= PENALTY_THRESHOLD) {
            applyPenalty();
        }
    }

    private void applyPenalty() {
        this.isBlocked = true;
        this.blockedAt = LocalDateTime.now();
        this.deleteCount = 0;
    }

    public boolean canExchangeCoupon() {
        if (!this.isBlocked) {
            return true;
        }

        if (isPenaltyExpired()) {
            liftPenalty();
            return true;
        }

        return false;
    }

    private boolean isPenaltyExpired() {
        return LocalDateTime.now().isAfter(this.blockedAt.plusDays(PENALTY_DAYS));
    }

    private void liftPenalty() {
        this.isBlocked = false;
        this.blockedAt = null;
    }

    public void earnPoint(long amount) {
        if (amount < 0) {
            throw new BusinessException(ErrorCode.POINT_CANNOT_NEGATIVE);
        }
        this.balance += amount;
    }

    public void usePoint(long amount) {
        if (amount < 0) {
            throw new BusinessException(ErrorCode.POINT_CANNOT_NEGATIVE);
        }
        if (this.balance < amount) {
            throw new BusinessException(ErrorCode.INVALID_POINT);
        }
        this.balance -= amount;
    }

    public void updateNotificationEnabled(boolean enabled) {
        this.isReceivedAlarm = enabled;
    }
}
