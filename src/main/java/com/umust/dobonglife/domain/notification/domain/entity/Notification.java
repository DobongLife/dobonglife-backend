package com.umust.dobonglife.domain.notification.domain.entity;

import com.umust.dobonglife.domain.notification.domain.constant.NotificationType;
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

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    private String relatedUrl;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Notification(Long userId, NotificationType type, String title, String content, String relatedUrl) {
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.content = content;
        this.relatedUrl = relatedUrl;
    }

    public static Notification create(Long userId, NotificationType type, String title, String content, String relatedUrl) {
        validateRequiredFields(userId, content);

        return new Notification(userId, type, title, content, relatedUrl);
    }

    private static void validateRequiredFields(Long userId, String content) {
        if (userId == null || userId <= 0) {
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