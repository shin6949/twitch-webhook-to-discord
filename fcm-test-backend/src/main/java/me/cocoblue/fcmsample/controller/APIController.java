package me.cocoblue.fcmsample.controller;

import lombok.extern.log4j.Log4j2;
import me.cocoblue.fcmsample.dto.NotificationTypeDTO;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Log4j2
@RestController
@RequestMapping("/api")
public class APIController {
    @PostMapping("/twitch/notification/register")
    public Map<String, String> mockRegister() {
        final Map<String, String> result = new HashMap<>();
        result.put("result", "true");

        return result;
    }

    @GetMapping("/twitch/notification/types")
    public List<NotificationTypeDTO> mockTypes() {
        List<NotificationTypeDTO> notificationTypeDTOList = new ArrayList<>();
        notificationTypeDTOList.add(new NotificationTypeDTO("channel.update", "채널 정보 변경"));
        notificationTypeDTOList.add(new NotificationTypeDTO("stream.online", "방송 시작"));
        notificationTypeDTOList.add(new NotificationTypeDTO("stream.offline", "방송 종료"));

        return notificationTypeDTOList;
    }

    @GetMapping("/twitch/id-search")
    public Map<String, Object> mockIdCheck(@RequestParam(name = "name") String name) {
        final Map<String, Object> result = new HashMap<>();
        result.put("result", name.equals("shin6949"));

        return result;
    }
}
