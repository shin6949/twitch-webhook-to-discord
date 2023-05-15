package me.cocoblue.twitchwebhook.service.api;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.domain.push.PushSubscriptionFormRepository;
import me.cocoblue.twitchwebhook.dto.api.NotificationRegisterDTO;
import me.cocoblue.twitchwebhook.dto.api.NotificationTypeDTO;
import me.cocoblue.twitchwebhook.dto.api.UserSearchResultDTO;
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

@Log4j2
@Service
@RequiredArgsConstructor
public class RegisterPageAPIService {
    private final MessageSource messageSource;
    private final FirebaseInitializer firebaseInitializer;
    private final UserInfoService userInfoService;
    private final BroadcasterIdRepository broadcasterIdRepository;
    private final PushSubscriptionFormRepository pushSubscriptionFormRepository;
    private FirebaseMessaging fcm = FirebaseMessaging.getInstance();


    public List<NotificationTypeDTO> getNotificationTypeList(final Locale locale) {
        return Arrays.stream(TwitchSubscriptionType.values())
                .map(e -> new NotificationTypeDTO(e.getTwitchName(), messageSource.getMessage("api.register.types." + e.getTwitchName(), null, locale)))
                .collect(Collectors.toList());
    }

    public UserSearchResultDTO getUserByTwitchId(final String twitchId) {
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByLoginIdEquals(twitchId);
        if(broadcasterIdEntity.isPresent()) {
            final Duration duration = Duration.between(broadcasterIdEntity.get().getUpdatedAt(), LocalDateTime.now());
            // DB 안에 있는 데이터가 3일 이내이고, 프로필 이미지가 있으면 DB 값을 반환함.
            if(duration.toDays() < 3 && broadcasterIdEntity.get().getProfileUrl() != null) {
                return UserSearchResultDTO.builder()
                        .result(true)
                        .isLive(false)
                        .user(new User(broadcasterIdEntity.get()))
                        .build();
            }
        }
        final Optional<User> user = userInfoService.getUserInfoByLoginIdFromTwitch(twitchId);

        return UserSearchResultDTO.builder()
                .result(user.isPresent())
                .isLive(true)
                .user(user.orElse(null))
                .build();
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

    public void saveSubscription(final NotificationRegisterDTO notificationRegisterDTO) {
        log.info("Received DTO: {}", notificationRegisterDTO);

        Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByLoginIdEquals(notificationRegisterDTO.getTwitchId());
        if(broadcasterIdEntity.isEmpty()) {
            final Optional<User> user = userInfoService.getUserInfoByLoginIdFromTwitch(notificationRegisterDTO.getTwitchId());
            if(user.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            } else {
                broadcasterIdEntity = Optional.of(broadcasterIdRepository.save(user.get().toBroadcasterIdEntity()));
            }
        }

        log.info(broadcasterIdEntity);
        log.info(notificationRegisterDTO.toEntity(broadcasterIdEntity.get()));

        pushSubscriptionFormRepository.save(notificationRegisterDTO.toEntity(broadcasterIdEntity.get()));
    }
}
