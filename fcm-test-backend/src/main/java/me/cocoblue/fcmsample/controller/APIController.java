package me.cocoblue.fcmsample.controller;

import me.cocoblue.fcmsample.dto.NotificationTypeDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class APIController {
    @PostMapping("/twitch/notification/register")
    public boolean mockRegister() {
        return true;
    }

    @GetMapping("/twitch/notification/types")
    public List<NotificationTypeDTO> mockTypes() {
        List<NotificationTypeDTO> notificationTypeDTOList = new ArrayList<>();
        notificationTypeDTOList.add(new NotificationTypeDTO("channel.update", "채널 정보 변경"));
        notificationTypeDTOList.add(new NotificationTypeDTO("stream.online", "방송 시작"));
        notificationTypeDTOList.add(new NotificationTypeDTO("stream.offline", "방송 종료"));

        return notificationTypeDTOList;
    }
}
