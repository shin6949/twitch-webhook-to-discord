package me.cocoblue.fcmsample.controller;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import me.cocoblue.fcmsample.service.FirebaseInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FCMController {

    private final FirebaseInitializer firebaseInitializer;
    private final FirebaseMessaging fcm = FirebaseMessaging.getInstance();

//    @GetMapping("/v1")
//    public String v1() {
//        firebaseInitializer.init();
//        return "test";
//    }

    @PostMapping("/topics/{topic}")
    public ResponseEntity<String> postToTopic(@RequestBody String message, @PathVariable("topic") String topic) throws FirebaseMessagingException {
        Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("This is Topic Scope Notification")
                        .setBody(message)
                        .build())
                .setTopic(topic)
                .build();

        String id = fcm.send(msg);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(id);
    }

    @PostMapping("/clients/{registrationToken}")
    public ResponseEntity<String> postToClient(@RequestBody String message, @PathVariable("registrationToken") String registrationToken) throws FirebaseMessagingException {

        Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("This is client scope notification")
                        .setBody(message)
                        .build())
                .setToken(registrationToken)
                .build();

        String id = fcm.send(msg);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(id);
    }

    @PostMapping("/subscriptions/{topic}")
    public ResponseEntity<Void> createSubscription(@PathVariable("topic") String topic, @RequestBody List<String> registrationTokens) throws FirebaseMessagingException {
        System.out.println("Subscriptions Request Received");
        System.out.println("topic: " + topic);
        System.out.println("registrationTokens: " + registrationTokens);

        fcm.subscribeToTopic(registrationTokens, topic);
        return ResponseEntity.ok().build();
    }
}