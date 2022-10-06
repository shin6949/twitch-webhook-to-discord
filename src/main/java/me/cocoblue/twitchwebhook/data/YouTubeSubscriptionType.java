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
public enum YouTubeSubscriptionType {
    VIDEO_UPLOAD("VIDEO_UPLOAD"),
    VIDEO_UPDATE("VIDEO_UPDATE"),
    VIDEO_DELETED("VIDEO_DELETED"),
    LIVE_START("LIVE_START");

    private final String name;

    private static final Map<String, YouTubeSubscriptionType> descriptions = Collections.unmodifiableMap(Stream.of(values()) .collect(Collectors.toMap(YouTubeSubscriptionType::getName, Function.identity())));

    public static YouTubeSubscriptionType find(String typeName) {
        return Optional.ofNullable(descriptions.get(typeName)).orElseThrow();
    }
}
