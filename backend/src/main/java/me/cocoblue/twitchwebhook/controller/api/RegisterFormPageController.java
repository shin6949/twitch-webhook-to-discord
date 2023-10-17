package me.cocoblue.twitchwebhook.controller.api;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.data.LanguageIsoData;
import me.cocoblue.twitchwebhook.dto.api.register.NotificationRegisterDTO;
import me.cocoblue.twitchwebhook.dto.api.register.NotificationTypeDTO;
import me.cocoblue.twitchwebhook.dto.api.register.UserSearchResultDTO;
import me.cocoblue.twitchwebhook.service.api.RegisterPageAPIService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterFormPageController {
    private final RegisterPageAPIService registerPageAPIService;

    @PostMapping("/twitch/notification/submit")
    public Map<String, String> register(final HttpServletRequest request, @RequestBody NotificationRegisterDTO notificationRegisterDTO) throws FirebaseMessagingException {
        log.info("API Register Called");

        final Locale locale = new Locale(request.getHeader("Accept-Language"));

        if(locale.getLanguage().startsWith("ko")) {
            notificationRegisterDTO.setLanguage(LanguageIsoData.Korean);
        } else {
            notificationRegisterDTO.setLanguage(LanguageIsoData.English);
        }

        log.debug("Received Data: {}", notificationRegisterDTO);
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
