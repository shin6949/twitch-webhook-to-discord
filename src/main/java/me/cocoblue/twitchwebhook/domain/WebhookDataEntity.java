package me.cocoblue.twitchwebhook.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "webhook_data")
@Builder
public class WebhookDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String name;

    @Column(length = 500)
    @NotNull
    private String webhookUrl;

    @Column
    private String meno;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="owner_id", foreignKey = @ForeignKey(name="FK_WEBHOOK_DATA_OWNER_ID"))
    @NotNull
    private BroadcasterIdEntity ownerId;
}
