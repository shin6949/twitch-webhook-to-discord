package me.cocoblue.twitchwebhook.controller.api;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    @PostMapping("/clients/{registrationToken}")
    public ResponseEntity<String> postToClient(@RequestBody String message, @PathVariable("registrationToken") String registrationToken) throws FirebaseMessagingException {
        if(FirebaseApp.getApps().isEmpty()) {
            firebaseInitializer.init();
            fcm = FirebaseMessaging.getInstance();
        }

        Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("This is test notification")
                        .setBody(message)
                        .build())
                .setToken(registrationToken)
                .build();

        String id = fcm.send(msg);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(id);
    }
}
