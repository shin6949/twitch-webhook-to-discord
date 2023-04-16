package me.cocoblue.twitchwebhook.domain;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "broadcaster_id")
@Builder
public class BroadcasterIdEntity {
    @Id
    @NotNull
    private Long id;
    @Column(length = 300)
    private String loginId;
    @Column(length = 300)
    private String displayName;

    public boolean equals(BroadcasterIdEntity broadcasterIdEntity) {
        if(!broadcasterIdEntity.getId().equals(this.id)) return false;
        if(!broadcasterIdEntity.getLoginId().equals(this.loginId)) return false;
        return broadcasterIdEntity.getDisplayName().equals(this.displayName);
    }
}
