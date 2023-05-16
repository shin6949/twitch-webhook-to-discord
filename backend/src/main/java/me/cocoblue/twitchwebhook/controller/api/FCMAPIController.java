package me.cocoblue.twitchwebhook.controller.api;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.api.register.NotificationTestMessageRequestDTO;
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
    private FirebaseMessaging fcm = FirebaseMessaging.getInstance();

    @GetMapping("/config")
    public ResponseEntity<String> getConfig() {
        return ResponseEntity.status(HttpStatus.OK).body("Firebase initialized");
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody final NotificationTestMessageRequestDTO request) throws FirebaseMessagingException {
        log.info("Received Message request: {}", request);
        if(request.getRegistrationToken() == null || request.getRegistrationToken().isEmpty()) {
            log.error("Registration token isn't provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration token is required");
        }

        if(FirebaseApp.getApps() == null) {
            firebaseInitializer.init();
            fcm = FirebaseMessaging.getInstance();
        }

        final Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getContent())
                        .build())
                .setToken(request.getRegistrationToken())
                .build();

        fcm.send(msg);

        return ResponseEntity.status(HttpStatus.OK).body("Message sent successfully");
    }
}
