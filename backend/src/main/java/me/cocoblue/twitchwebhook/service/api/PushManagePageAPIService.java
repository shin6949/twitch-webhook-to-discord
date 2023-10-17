package me.cocoblue.twitchwebhook.service.api;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.domain.logdomain.PushUserLogViewEntity;
import me.cocoblue.twitchwebhook.domain.logdomain.PushUserLogViewRepository;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdEntity;
import me.cocoblue.twitchwebhook.domain.twitch.BroadcasterIdRepository;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormEntity;
import me.cocoblue.twitchwebhook.domain.twitch.PushSubscriptionFormRepository;
import me.cocoblue.twitchwebhook.dto.api.pushmanage.NotificationCardDTO;
import me.cocoblue.twitchwebhook.dto.api.pushmanage.NotificationDeleteResultDTO;
import me.cocoblue.twitchwebhook.dto.twitch.User;
import me.cocoblue.twitchwebhook.service.twitch.UserInfoService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class PushManagePageAPIService {
    private final PushSubscriptionFormRepository pushSubscriptionFormRepository;
    private final PushUserLogViewRepository pushUserLogViewRepository;
    private final BroadcasterIdRepository broadcasterIdRepository;
    private final UserInfoService userInfoService;
    private final MessageSource messageSource;

    public NotificationDeleteResultDTO deleteNotification(final Locale locale, final String token, final long formId) {
        final NotificationDeleteResultDTO result = new NotificationDeleteResultDTO();
        final Optional<PushSubscriptionFormEntity> pushSubscriptionFormEntity = pushSubscriptionFormRepository.getPushSubscriptionFormEntityById(formId);
        if(pushSubscriptionFormEntity.isEmpty()) {
            result.setResult(false);
            result.setMessage(messageSource.getMessage("api.push-manage.delete.invalid-request", null, locale));
            return result;
        }

        // 유효하지 않은 사용자가 요청한 경우 Token Filtering 으로 차단
        if(!pushSubscriptionFormEntity.get().getRegistrationToken().equals(token)) {
            result.setResult(false);
            result.setMessage(messageSource.getMessage("api.push-manage.delete.invalid-request", null, locale));
            return result;
        }

        pushSubscriptionFormRepository.deleteById(pushSubscriptionFormEntity.get().getId());
        result.setResult(true);
        result.setMessage(messageSource.getMessage("api.push-manage.delete.success", null, locale));
        return result;
    }

    public List<NotificationCardDTO> getNotifcationList(final String registrationToken, final Locale locale) {
        final List<PushSubscriptionFormEntity> pushSubscriptionFormEntities = pushSubscriptionFormRepository.getPushSubscriptionFormEntitiesByRegistrationToken(registrationToken);
        final List<NotificationCardDTO> result = pushSubscriptionFormEntities.stream()
                .map(item -> makeNotificationDTO(item, locale))
                .collect(Collectors.toList());
        log.debug("getNotificationList Result: {}", result);

        return result;
    }

    private User getUserByBroadcasterId(final long broadcasterId) {
        final Optional<BroadcasterIdEntity> broadcasterIdEntity = broadcasterIdRepository.getBroadcasterIdEntityByIdEquals(broadcasterId);
        if(broadcasterIdEntity.isPresent()) {
            final Duration duration = Duration.between(broadcasterIdEntity.get().getUpdatedAt(), LocalDateTime.now());
            // DB 안에 있는 데이터가 3일 이내이고, 프로필 이미지가 있으면 DB 값을 반환함.
            if(duration.toDays() < 3 && broadcasterIdEntity.get().getProfileUrl() != null) {
                return new User(broadcasterIdEntity.get());
            }
        }

        final Optional<User> result = userInfoService.getUserInfoByBroadcasterIdFromTwitch(String.valueOf(broadcasterId));
        return result.orElse(null);
    }

    private NotificationCardDTO makeNotificationDTO(final PushSubscriptionFormEntity pushSubscriptionFormEntity, final Locale locale) {
        final NotificationCardDTO result = new NotificationCardDTO();
        result.setFormId(pushSubscriptionFormEntity.getId());
        result.setNotificationType(messageSource.getMessage("api.register.types." + pushSubscriptionFormEntity.getTwitchSubscriptionType().getTwitchName(), null, locale));
        result.setIntervalMinute(pushSubscriptionFormEntity.getIntervalMinute());

        final int notificationCount = pushUserLogViewRepository.countByPushSubscriptionFormEntity(pushSubscriptionFormEntity);
        final Optional<PushUserLogViewEntity> pushUserLogViewEntity = pushUserLogViewRepository.findFirstByPushSubscriptionFormEntityOrderByReceivedTimeDesc(pushSubscriptionFormEntity);

        result.setLatestNotificationTime(pushUserLogViewEntity.map(PushUserLogViewEntity::getReceivedTime).orElse(null));
        result.setNotificationCount(notificationCount);

        final User user = getUserByBroadcasterId(pushSubscriptionFormEntity.getBroadcasterIdEntity().getId());
        if(user == null) {
            result.setLoginId(messageSource.getMessage("api.push-manage.deleted-user", null, locale));
            return result;
        }

        result.setLoginId(user.getLogin());
        result.setNickname(user.getDisplayName());
        result.setProfileImage(user.getProfileImageUrl());

        return result;
    }
}
