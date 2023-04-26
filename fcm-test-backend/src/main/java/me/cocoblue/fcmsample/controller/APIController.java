package me.cocoblue.fcmsample.controller;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.fcmsample.dto.NotificationRegisterDTO;
import me.cocoblue.fcmsample.dto.NotificationTypeDTO;
import me.cocoblue.fcmsample.service.FirebaseInitializer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class APIController {
    // 사용하지 않는 것 같지만, 이 객체가 없으면 오류가 발생함.
    private final FirebaseInitializer firebaseInitializer;
    private final FirebaseMessaging fcm;

//    @PostConstruct
//    public void firebaseInit() {
//        firebaseInitializer.init();
//    }

    @PostMapping("/twitch/notification/register")
    public Map<String, String> mockRegister(@RequestBody NotificationRegisterDTO notificationRegisterDTO) throws FirebaseMessagingException  {
        log.info("Register Called");
        log.info("notificationRegisterDTO");

        if(FirebaseApp.getApps().isEmpty()) {
            firebaseInitializer.init();
            FirebaseMessaging.getInstance();
        }

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
