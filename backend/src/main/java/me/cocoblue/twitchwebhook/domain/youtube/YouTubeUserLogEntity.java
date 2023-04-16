package me.cocoblue.twitchwebhook.domain.youtube;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "youtube_user_log")
public class YouTubeUserLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name="form_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_USER_LOG_FORM_ID"))
    @NotNull
    private YouTubeSubscriptionFormEntity youTubeSubscriptionFormEntity;

    @ManyToOne()
    @JoinColumn(name="log_id", foreignKey = @ForeignKey(name="FK_YOUTUBE_USER_LOG_LOG_ID"))
    @NotNull
    private YouTubeNotificationLogEntity logId;

    // 잘 전송되었는지
    @Column(name = "status", nullable = false, columnDefinition = "BIT", length = 1)
    private boolean status;
}
