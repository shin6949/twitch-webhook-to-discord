package me.cocoblue.twitchwebhook.entity;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "broadcaster_id")
@Builder
public class BroadcasterIdEntity {
    @Id
    @NotNull
    private Long id;
    @Column(length = 300)
    @NotNull
    private String loginId;
    @Column(length = 300)
    @NotNull
    private String displayName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BroadcasterIdEntity that = (BroadcasterIdEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
