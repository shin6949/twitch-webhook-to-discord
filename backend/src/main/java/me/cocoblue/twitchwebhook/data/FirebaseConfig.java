package me.cocoblue.twitchwebhook.data;

import org.springframework.beans.factory.annotation.Value;

public class FirebaseConfig {
    @Value("${FIREBASE_API_KEY:null}")
    private String apiKey;
    @Value("${FIREBASE_AUTH_DOMAIN:null}")
    private String authDomain;
    @Value("${FIREBASE_PROJECT_ID:null}")
    private String projectId;
}
