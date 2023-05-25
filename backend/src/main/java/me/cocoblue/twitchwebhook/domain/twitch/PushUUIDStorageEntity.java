package me.cocoblue.twitchwebhook.domain.twitch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "push_uuid_storage")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PushUUIDStorageEntity {
    @Id
    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(length = 300, name = "fcm_token", nullable = false, unique = true)
    private String fcmToken;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
