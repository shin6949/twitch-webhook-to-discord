package me.cocoblue.fcmsample.controller;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.fcmsample.dto.MessageRequest;
import me.cocoblue.fcmsample.service.FirebaseInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageAPIController {
    private final FirebaseInitializer firebaseInitializer;
    private final FirebaseMessaging fcm = FirebaseMessaging.getInstance();

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageRequest request) throws FirebaseMessagingException {
        log.info("Received Message request: {}", request);
        if(request.getRegistrationToken() == null || request.getRegistrationToken().isEmpty()) {
            log.error("Registration token isn't provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration token is required");
        }

//        firebaseInitializer.init();

        // MessageRequest에서 제목, 내용, 등록 토큰을 가져옵니다.
        String title = request.getTitle();
        String content = request.getContent();
        String registrationToken = request.getRegistrationToken();

        // Firebase를 사용하여 메시지를 전송하는 코드를 작성합니다.
        Message msg = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .setToken(registrationToken)
                .build();

        String id = fcm.send(msg);

        // 메시지 전송이 성공했다면 HTTP 상태 코드 200과 함께 응답합니다.
        return ResponseEntity.status(HttpStatus.OK).body("Message sent successfully");
    }
}
