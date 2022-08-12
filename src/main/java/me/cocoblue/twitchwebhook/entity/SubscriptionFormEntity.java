package me.cocoblue.twitchwebhook.entity;

import com.sun.istack.NotNull;
import lombok.*;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.data.SubscriptionType;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Entity(name = "subscription_form")
public class SubscriptionFormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_SUBSCRIPTION_FORM_BROADCASTER_ID"))
    @NotNull
    private BroadcasterIdEntity broadcasterIdEntity;
    @Column(length = 100)
    @NotNull
    private String username;
    @Column(length = 600)
    @NotNull
    private String avatarUrl;
    @Column(length = 2000)
    @NotNull
    private String content;
    @NotNull
    private String colorHex;
    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @NotNull
    private SubscriptionType subscriptionType;
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private LanguageIsoData languageIsoData;
    @Column(length = 500)
    @NotNull
    private String webhookUrl;

    public int getDecimalColor() {
        return Integer.parseInt(getColorHex(),16);
    }
}
