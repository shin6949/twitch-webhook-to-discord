package me.cocoblue.twitchwebhook.controller.api;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.api.NotificationTestMessageRequestDTO;
import me.cocoblue.twitchwebhook.service.FirebaseInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequestMapping("/api/message")
@RestController
@RequiredArgsConstructor
public class FCMAPIController {
    private final FirebaseInitializer firebaseInitializer;
    private FirebaseMessaging fcm;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody NotificationTestMessageRequestDTO request) throws FirebaseMessagingException {
        log.info("Received Message request: {}", request);
        if(request.getRegistrationToken() == null || request.getRegistrationToken().isEmpty()) {
            log.error("Registration token isn't provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration token is required");
        }

        if(FirebaseApp.getApps().isEmpty()) {
            firebaseInitializer.init();
            fcm = FirebaseMessaging.getInstance();
        }

        // Firebase를 사용하여 메시지를 전송하는 코드를 작성합니다.
        final Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getContent())
                        .build())
                .setToken(request.getRegistrationToken())
                .build();

        fcm.send(msg);

        // 메시지 전송이 성공했다면 HTTP 상태 코드 200과 함께 응답합니다.
        return ResponseEntity.status(HttpStatus.OK).body("Message sent successfully");
    }
}
