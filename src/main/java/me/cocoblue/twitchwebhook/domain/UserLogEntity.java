package me.cocoblue.twitchwebhook.domain;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Entity(name = "user_log")
public class UserLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누구 대상의 로그인지
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="log_owner_id", foreignKey = @ForeignKey(name="FK_USER_LOG_LOG_OWNER"))
    @NotNull
    private BroadcasterIdEntity logOwner;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="log_id", foreignKey = @ForeignKey(name="FK_USER_LOG_LOG_ID"))
    @NotNull
    private NotificationLogEntity logId;

    // 잘 전송되었는지
    @Column(name = "status", nullable = false, columnDefinition = "false")
    private boolean status;

    // return code
    @Column(name = "result")
    private String result;
}
