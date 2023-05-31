package me.cocoblue.twitchwebhook.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.twitch.PushUUIDStorageEntity;
import me.cocoblue.twitchwebhook.domain.twitch.PushUUIDStorageRepository;
import me.cocoblue.twitchwebhook.dto.api.uuid.TokenUpdateResultDTO;
import me.cocoblue.twitchwebhook.dto.api.uuid.UUIDRequestDTO;
import me.cocoblue.twitchwebhook.dto.api.uuid.UUIDResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class UUIDService {
    private final PushUUIDStorageRepository pushUUIDStorageRepository;

    public UUIDResponse createUUID(final UUIDRequestDTO request) {
        // 이전 버전 사용 유저 등의 이슈로 Token이 DB에 등록되어 있지만 UUID가 저장되어 있지 않은 경우, DB 내의 UUID 반환
        final Optional<PushUUIDStorageEntity> tokenSearchResult = pushUUIDStorageRepository.getPushUUIDStorageEntityByFcmToken(request.getFcmToken());
        if(tokenSearchResult.isPresent()) {
            pushUUIDStorageRepository.save(tokenSearchResult.get());

            return new UUIDResponse(tokenSearchResult.get());
        }

        // 신규 등록
        final UUID issuedUUID = UUID.randomUUID();
        final PushUUIDStorageEntity result = PushUUIDStorageEntity.builder()
                .uuid(issuedUUID.toString())
                .fcmToken(request.getFcmToken())
                .build();

        pushUUIDStorageRepository.save(result);
        return new UUIDResponse(result);
    }

    public TokenUpdateResultDTO updateFcmToken(final UUIDRequestDTO request) {
        final Optional<PushUUIDStorageEntity> tokenSearchResult = pushUUIDStorageRepository.getPushUUIDStorageEntityByUuid(request.getUuid());
        if(tokenSearchResult.isEmpty()) {
            final UUIDResponse uuidRequestDTO = createUUID(request);

            return new TokenUpdateResultDTO(uuidRequestDTO, true);
        }

        tokenSearchResult.get().setFcmToken(request.getFcmToken());
        pushUUIDStorageRepository.save(tokenSearchResult.get());

        final UUIDResponse uuidResponse = new UUIDResponse(tokenSearchResult.get());
        return new TokenUpdateResultDTO(uuidResponse, false);
    }

    // 유효한 UUID 인지 검증.
    public TokenUpdateResultDTO verifyUUID(final UUIDRequestDTO request) {
        final Optional<PushUUIDStorageEntity> tokenSearchResult = pushUUIDStorageRepository.getPushUUIDStorageEntityByUuid(request.getUuid());
        if(tokenSearchResult.isEmpty()) {
            final UUIDResponse uuidRequestDTO = createUUID(request);

            return new TokenUpdateResultDTO(uuidRequestDTO, true);
        }

        tokenSearchResult.get().setUpdatedAt(LocalDateTime.now());
        pushUUIDStorageRepository.save(tokenSearchResult.get());

        final UUIDResponse uuidResponse = new UUIDResponse(tokenSearchResult.get());
        return new TokenUpdateResultDTO(uuidResponse, false);
    }
}
