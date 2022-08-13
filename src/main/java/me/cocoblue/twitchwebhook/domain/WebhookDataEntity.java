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

    @Column
    private String name;

    @Column(length = 500)
    @NotNull
    private String webhookUrl;

    @Column
    private String meno;
}
