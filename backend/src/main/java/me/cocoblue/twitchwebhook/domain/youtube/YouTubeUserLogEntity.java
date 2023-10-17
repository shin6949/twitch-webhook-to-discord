package me.cocoblue.twitchwebhook.domain.youtube;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "youtube_user_log")
public class YouTubeUserLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name="form_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_USER_LOG_FORM_ID"), nullable = false)
    private YouTubeSubscriptionFormEntity youTubeSubscriptionFormEntity;

    @ManyToOne()
    @JoinColumn(name="log_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_USER_LOG_LOG_ID"), nullable = false)
    private YouTubeNotificationLogEntity logId;

    // 잘 전송되었는지
    @Column(name = "status", nullable = false, columnDefinition = "BIT", length = 1)
    private boolean status;
}
