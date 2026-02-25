package com.umust.dobonglife.global.common.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Enumerated(EnumType.STRING)
    protected BaseStatus status;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = BaseStatus.ACTIVE;
        }
    }

    public void deactivate() { this.status = BaseStatus.INACTIVE; }
}
