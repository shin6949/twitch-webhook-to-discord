package me.cocoblue.fcmsample.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Configuration
public class FirebaseMessagingSwInitializer {
    @Value("${FIREBASE_CONFIG_BASE64}")
    private String base64FirebaseConfig;

    @PostConstruct
    public void init() {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64FirebaseConfig);
            String firebaseConfig = new String(decodedBytes, StandardCharsets.UTF_8);

            writeFirebaseMessagingSwToFile(firebaseConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFirebaseMessagingSwToFile(String firebaseConfig) throws IOException {
        String builder = "importScripts(\"https://www.gstatic.com/firebasejs/9.19.1/firebase-app-compat.js\");\n" +
                "importScripts(\"https://www.gstatic.com/firebasejs/9.19.1/firebase-messaging-compat.js\");\n" +
                "const firebaseConfig = {" +
                firebaseConfig +
                "};\n\nconst messaging = firebase.messaging();\n";

        ClassPathResource resource = new ClassPathResource("static/firebase-messaging-sw.js");
        try {
            Path path = Paths.get(resource.getURI());
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toString(), false));

            bufferedWriter.write(builder);
            bufferedWriter.newLine();

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

    }
}
