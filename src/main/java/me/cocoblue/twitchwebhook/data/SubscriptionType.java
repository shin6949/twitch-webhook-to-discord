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
public enum SubscriptionType {
    STREAM_ONLINE("stream.online"),
    STREAM_OFFLINE("stream.offline"),
    CHANNEL_UPDATE("channel.update");

    private final String twitchName;

    private static final Map<String, SubscriptionType> descriptions = Collections.unmodifiableMap(Stream.of(values()) .collect(Collectors.toMap(SubscriptionType::getTwitchName, Function.identity())));

    public static SubscriptionType find(String typeName) {
        return Optional.ofNullable(descriptions.get(typeName)).orElseThrow();
    }

}
