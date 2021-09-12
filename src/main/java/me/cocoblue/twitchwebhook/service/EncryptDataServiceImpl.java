package me.cocoblue.twitchwebhook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class EncryptDataServiceImpl implements EncryptDataService {
    // hash 암호화 key
    @Value("{twitch.hub.secret}")
    private String key;

    @Override
    public String encryptString(String message) {
        try {
            // hash 알고리즘과 암호화 key 적용
            String algorithm = "HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.getBytes(), algorithm));

            // messages를 암호화 적용 후 byte 배열 형태의 결과 리턴
            byte[] hash = mac.doFinal(message.getBytes());
            return byteToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return "";
    }

    // byte[]의 값을 16진수 형태의 문자로 변환하는 함수
    private String byteToString(byte[] hash) {
        StringBuilder buffer = new StringBuilder();

        for (int b : hash) {
            int d = b;
            d += (d < 0) ? 256 : 0;
            if (d < 16) {
                buffer.append("0");
            }
            buffer.append(Integer.toString(d, 16));
        }
        return buffer.toString();
    }
}
