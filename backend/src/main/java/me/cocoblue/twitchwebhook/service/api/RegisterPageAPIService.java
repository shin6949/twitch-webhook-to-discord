package me.cocoblue.twitchwebhook.service.api;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.dto.api.NotificationTypeDTO;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.service.FirebaseInitializer;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterPageAPIService {
    private final MessageSource messageSource;
    private final FirebaseInitializer firebaseInitializer;
    private final UserInfoService userInfoService;
    private final BroadcasterIdRepository broadcasterIdRepository;
    private FirebaseMessaging fcm = FirebaseMessaging.getInstance();


    public List<NotificationTypeDTO> getNotificationTypeList(final Locale locale) {
        return Arrays.stream(TwitchSubscriptionType.values())
                .map(e -> new NotificationTypeDTO(e.getTwitchName(), messageSource.getMessage("api.register.types." + e.getTwitchName(), null, locale)))
                .collect(Collectors.toList());
    }

    public Optional<User> getUserByTwitchId(final String twitchId) {
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByLoginIdEquals(twitchId);
        if(broadcasterIdEntity.isPresent()) {
            final Duration duration = Duration.between(broadcasterIdEntity.get().getUpdatedAt(), LocalDateTime.now());
            if(duration.toDays() < 30) {
                return Optional.of(new User(broadcasterIdEntity.get()));
            }
        }

        return userInfoService.getUserInfoByLoginIdFromTwitch(twitchId);
    }

    @Async
    public void sendTestNotification(final Locale locale, final String registrationToken) throws FirebaseMessagingException {
        if(FirebaseApp.getApps() == null || FirebaseApp.getApps().isEmpty()) {
            firebaseInitializer.init();
            fcm = FirebaseMessaging.getInstance();
        }

        final Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(messageSource.getMessage("api.register.notification-test.title", null, locale))
                        .setBody(messageSource.getMessage("api.register.notification-test.body", null, locale))
                        .build())
                .setToken(registrationToken)
                .build();

        fcm.send(msg);
    }
}
