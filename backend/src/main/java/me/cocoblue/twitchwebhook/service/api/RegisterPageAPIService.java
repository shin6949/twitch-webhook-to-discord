package me.cocoblue.twitchwebhook.service.api;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.dto.api.NotificationTypeDTO;
import me.cocoblue.twitchwebhook.service.FirebaseInitializer;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterPageAPIService {
    private final MessageSource messageSource;
    private final FirebaseInitializer firebaseInitializer;
    private FirebaseMessaging fcm;


    public List<NotificationTypeDTO> getNotificationTypeList(final Locale locale) {
        return Arrays.stream(TwitchSubscriptionType.values())
                .map(e -> new NotificationTypeDTO(e.getTwitchName(), messageSource.getMessage("api.register.types." + e.getTwitchName(), null, locale)))
                .collect(Collectors.toList());
    }

    @Async
    public void sendTestNotification(final Locale locale, final String registrationToken) throws FirebaseMessagingException {
        if(FirebaseApp.getApps().isEmpty()) {
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
