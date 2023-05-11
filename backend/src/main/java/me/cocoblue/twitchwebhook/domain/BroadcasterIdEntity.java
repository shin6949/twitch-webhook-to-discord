package me.cocoblue.twitchwebhook.domain;

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
@Entity(name = "broadcaster_id")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BroadcasterIdEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(length = 300, name = "login_id", nullable = false)
    private String loginId;
    @Column(length = 300, name = "display_name", nullable = false)
    private String displayName;
    @Column(length = 500, name = "profile_url")
    private String profileUrl;
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean equals(BroadcasterIdEntity broadcasterIdEntity) {
        if(!broadcasterIdEntity.getId().equals(this.id)) return false;
        if(!broadcasterIdEntity.getLoginId().equals(this.loginId)) return false;
        if(!broadcasterIdEntity.getProfileUrl().equals(this.profileUrl)) return false;
        return broadcasterIdEntity.getDisplayName().equals(this.displayName);
    }
}
