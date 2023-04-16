package me.cocoblue.fcmsample.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Log4j2
public class FirebaseInitializer {
    @Value("${FIREBASE_SERVICE_ACCOUNT_BASE64}")
    private String base64ServiceAccount;

    private final String PATH = "google-service-account.json";

    @PostConstruct
    public void init() {
        try {
            final byte[] decodedBytes = Base64.getDecoder().decode(base64ServiceAccount);
            final String json = new String(decodedBytes, StandardCharsets.UTF_8);

            writeJsonToFile(json);

            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(PATH)))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeJsonToFile(String json) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(PATH)) {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        }
    }
}