package me.cocoblue.twitchwebhook.controller.api;

import lombok.RequiredArgsConstructor;
import me.cocoblue.twitchwebhook.dto.api.uuid.TokenUpdateResultDTO;
import me.cocoblue.twitchwebhook.dto.api.uuid.UUIDRequestDTO;
import me.cocoblue.twitchwebhook.dto.api.uuid.UUIDResponse;
import me.cocoblue.twitchwebhook.service.api.UUIDService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/uuid")
@RequiredArgsConstructor
public class UUIDController {
    private final UUIDService uuidService;

    @PostMapping("/create")
    public UUIDResponse createUUID(final UUIDRequestDTO requestDTO) {
        return uuidService.createUUID(requestDTO);
    }

    @PostMapping("/update")
    public TokenUpdateResultDTO updateFcmToken(final UUIDRequestDTO requestDTO) {
        return uuidService.updateFcmToken(requestDTO);
    }

    @PostMapping("/verify")
    public TokenUpdateResultDTO verifyUUID(final UUIDRequestDTO requestDTO) {
        return uuidService.verifyUUID(requestDTO);
    }
}
