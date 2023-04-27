package me.cocoblue.twitchwebhook.controller.api;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.api.NotificationRegisterDTO;
import me.cocoblue.twitchwebhook.dto.api.NotificationTypeDTO;
import me.cocoblue.twitchwebhook.service.api.RegisterPageAPIService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Log4j2
@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterFormController {
    private final RegisterPageAPIService registerPageAPIService;

    @PostMapping("/twitch/notification/submit")
    public Map<String, String> mockRegister(final HttpServletRequest request, @RequestBody final NotificationRegisterDTO notificationRegisterDTO) throws FirebaseMessagingException {
        log.info("Register Called");
        final Locale locale = new Locale(request.getHeader("Accept-Language"));

        final Map<String, String> result = new HashMap<>();
        result.put("result", "true");

        registerPageAPIService.sendTestNotification(locale, notificationRegisterDTO.getRegistrationToken());

        return result;
    }


    @GetMapping("/twitch/notification/types")
    public List<NotificationTypeDTO> getTypes(final HttpServletRequest request) {
        final Locale locale = new Locale(request.getHeader("Accept-Language"));
        return registerPageAPIService.getNotificationTypeList(locale);
    }

    @GetMapping("/twitch/id-search")
    public Map<String, Object> mockIdCheck(@RequestParam(name = "name") final String name) {
        final Map<String, Object> result = new HashMap<>();
        result.put("result", name.equals("shin6949"));

        return result;
    }
}
