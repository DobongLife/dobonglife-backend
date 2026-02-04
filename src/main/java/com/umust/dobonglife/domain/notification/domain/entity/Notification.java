package com.umust.dobonglife.domain.notification.domain.entity;

import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
import com.umust.dobonglife.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    private Long relatedUrlId;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Notification(User user, NotificationType type, String title, String content, Long relatedUrlId) {
        this.user = user;
        this.title = title;
        this.type = type;
        this.content = content;
        this.relatedUrlId = relatedUrlId;
    }

    public static Notification create(User user, NotificationType type, String title, String content, Long relatedUrlId) {
        validateRequiredFields(user, content);

        return new Notification(user, type, title, content, relatedUrlId);
    }

    private static void validateRequiredFields(User user, String content) {
        if (user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("알림을 받을 사용자 ID는 필수입니다.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("알림 내용은 필수입니다.");
        }
    }

    public void markAsRead() {
        this.isRead = true;
    }
}