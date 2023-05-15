package me.cocoblue.twitchwebhook.controller.api;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.dto.api.NotificationRegisterDTO;
import me.cocoblue.twitchwebhook.dto.api.NotificationTypeDTO;
import me.cocoblue.twitchwebhook.dto.api.UserSearchResultDTO;
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
    public Map<String, String> register(final HttpServletRequest request, @RequestBody NotificationRegisterDTO notificationRegisterDTO) throws FirebaseMessagingException {
        log.info("Register Called");

        final Locale locale = new Locale(request.getHeader("Accept-Language"));
        log.info("locale: {}", locale.getLanguage());
        if(locale.getLanguage().startsWith("ko")) {
            log.info("It's Korean");
            notificationRegisterDTO.setLanguage(LanguageIsoData.Korean);
        } else {
            log.info("It's English");
            notificationRegisterDTO.setLanguage(LanguageIsoData.English);
        }

        log.info("Received Data: {}", notificationRegisterDTO);
        registerPageAPIService.saveSubscription(notificationRegisterDTO);

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
    public UserSearchResultDTO idCheck(@RequestParam(name = "name") final String name) {
        return registerPageAPIService.getUserByTwitchId(name);
    }
}
