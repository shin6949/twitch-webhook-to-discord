package me.cocoblue.twitchwebhook.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.api.pushmanage.NotificationCardDTO;
import me.cocoblue.twitchwebhook.dto.api.pushmanage.NotificationDeleteResultDTO;
import me.cocoblue.twitchwebhook.service.api.PushManagePageAPIService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Log4j2
@RestController
@RequestMapping("/api/push-manage")
@RequiredArgsConstructor
public class PushManagePageController {
    private final PushManagePageAPIService pushManagePageAPIService;

    @GetMapping("/get")
    public List<NotificationCardDTO> getNotificationList(final HttpServletRequest request,
                                             @RequestParam(name = "token") final String registrationToken) {
        final Locale locale = new Locale(request.getHeader("Accept-Language"));
        log.debug("registrationToken: " + registrationToken);
        return pushManagePageAPIService.getNotifcationList(registrationToken, locale);
    }

    @DeleteMapping("/delete")
    public NotificationDeleteResultDTO deleteNotification(final HttpServletRequest request,
                                                          @RequestParam(name = "token") final String registrationToken,
                                                          @RequestParam(name = "id") final long id) {
        final Locale locale = new Locale(request.getHeader("Accept-Language"));

        return pushManagePageAPIService.deleteNotification(locale, registrationToken, id);
    }
}
