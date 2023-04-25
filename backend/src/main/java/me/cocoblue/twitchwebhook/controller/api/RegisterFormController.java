package me.cocoblue.twitchwebhook.controller.api;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.api.NotificationRegisterDTO;
import me.cocoblue.twitchwebhook.dto.api.NotificationTypeDTO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/register")
public class RegisterFormController {
    private final FirebaseMessaging fcm = FirebaseMessaging.getInstance();

    @PostMapping("/twitch/notification/register")
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
        List<NotificationTypeDTO> notificationTypeDTOList = new ArrayList<>();
        notificationTypeDTOList.add(new NotificationTypeDTO("channel.update", "채널 정보 변경"));
        notificationTypeDTOList.add(new NotificationTypeDTO("stream.online", "방송 시작"));
        notificationTypeDTOList.add(new NotificationTypeDTO("stream.offline", "방송 종료"));

        return notificationTypeDTOList;
    }

    @GetMapping("/twitch/id-search")
    public Map<String, Object> mockIdCheck(@RequestParam(name = "name") String name) {
        final Map<String, Object> result = new HashMap<>();
        result.put("result", name.equals("shin6949"));

        return result;
    }
}
