package me.cocoblue.twitchwebhook.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "stream_notify_form")
public class StreamNotifyFormEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="broadcaster_id", foreignKey = @ForeignKey(name="FK_STREAM_NOTIFY_FORM_BROADCASTER_ID"))
    private BroadcasterIdEntity broadcasterIdEntity;
    @Column(length = 100)
    private String username;
    @Column(length = 600)
    private String avatarUrl;
    @Column(length = 2000)
    private String content;
    private int color;
    @Column(length = 30)
    private String type;
    @Column(length = 500)
    private String webhookUrl;
}
