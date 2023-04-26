package me.cocoblue.twitchwebhook.controller.api;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.TwitchSubscriptionType;
import me.cocoblue.twitchwebhook.dto.api.NotificationRegisterDTO;
import me.cocoblue.twitchwebhook.dto.api.NotificationTypeDTO;
import me.cocoblue.twitchwebhook.service.FirebaseInitializer;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterFormController {
    private final FirebaseInitializer firebaseInitializer;
    private final FirebaseMessaging fcm = FirebaseMessaging.getInstance();

    @PostMapping("/twitch/notification/submit")
    public Map<String, String> mockRegister(@RequestBody NotificationRegisterDTO notificationRegisterDTO) throws FirebaseMessagingException {
        log.info("Register Called");

        final Map<String, String> result = new HashMap<>();
        result.put("result", "true");

        final Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("This is client scope notification")
                        .setBody("Notification Register Success")
                        .build())
                .setToken(notificationRegisterDTO.getRegistrationToken())
                .build();

        fcm.send(msg);

        return result;
    }

    @GetMapping("/twitch/notification/types")
    public List<NotificationTypeDTO> mockTypes() {

        return Arrays.stream(TwitchSubscriptionType.values())
                .map(NotificationTypeDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/twitch/id-search")
    public Map<String, Object> mockIdCheck(@RequestParam(name = "name") String name) {
        final Map<String, Object> result = new HashMap<>();
        result.put("result", name.equals("shin6949"));

        return result;
    }
}
