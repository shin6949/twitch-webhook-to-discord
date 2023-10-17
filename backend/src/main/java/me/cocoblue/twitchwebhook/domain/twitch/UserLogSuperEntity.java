package me.cocoblue.twitchwebhook.domain.twitch;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@MappedSuperclass
@SuperBuilder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserLogSuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name="log_id", foreignKey = @ForeignKey(name="FK_USER_LOG_LOG_ID"), nullable = false)
    private NotificationLogEntity logId;

    // 잘 전송되었는지
    @Column(name = "status", nullable = false, columnDefinition = "BIT", length = 1)
    private boolean status;
}
