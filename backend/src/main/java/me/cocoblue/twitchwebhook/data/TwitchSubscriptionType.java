package me.cocoblue.twitchwebhook.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum TwitchSubscriptionType {
    STREAM_ONLINE("stream.online", "방송 온라인", "Stream Online"),
    STREAM_OFFLINE("stream.offline", "방송 오프라인", "Stream Offline"),
    CHANNEL_UPDATE("channel.update", "채널 업데이트", "Channel Update");

    private final String twitchName;
    private final String koreanName;
    private final String englishName;

    private static final Map<String, TwitchSubscriptionType> descriptions = Collections.unmodifiableMap(Stream.of(values()) .collect(Collectors.toMap(TwitchSubscriptionType::getTwitchName, Function.identity())));

    public static TwitchSubscriptionType find(String typeName) {
        return Optional.ofNullable(descriptions.get(typeName)).orElseThrow();
    }

}
