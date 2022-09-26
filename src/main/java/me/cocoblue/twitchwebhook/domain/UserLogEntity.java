package me.cocoblue.twitchwebhook.domain;

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
@Entity(name = "user_log")
public class UserLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name="form_id", foreignKey = @ForeignKey(name="FK_USER_LOG_FORM_ID"))
    @NotNull
    private SubscriptionFormEntity subscriptionFormEntity;

    @ManyToOne()
    @JoinColumn(name="log_id", foreignKey = @ForeignKey(name="FK_USER_LOG_LOG_ID"))
    @NotNull
    private NotificationLogEntity logId;

    // 잘 전송되었는지
    @Column(name = "status", nullable = false, columnDefinition = "BIT", length = 1)
    private boolean status;
}
