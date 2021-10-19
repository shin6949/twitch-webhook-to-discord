package me.cocoblue.twitchwebhook.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Body;
import me.cocoblue.twitchwebhook.dto.twitch.eventsub.Event;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Log4j2
class EventVoTests {
    @Test
    public void test() {
        final String time = "2021-10-06T04:32:47.613998673Z";

        Event event = new Event();
        event.setStartedAtString(time);
        log.info(event.getStartedAt());
    }

    @Test
    public void convertTest() throws JsonProcessingException {
        final String testString = "{\"subscription\":{\"id\":\"e9bcf0d8-94c8-42f9-8a9f-a5ef2e685c1c\",\"status\":\"webhook_callback_verification_pending\",\"type\":\"stream.online\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"268907830\"},\"transport\":{\"method\":\"webhook\",\"callback\":\"https://deaa-115-178-87-144.ngrok.io/webhook/stream/268907830/online\"},\"created_at\":\"2021-10-06T04:32:47.609289835Z\",\"cost\":1},\"challenge\":\"KhsgElmCLlA1U_IxZncxnnkhxTSWkNSWmsBriyMgQAM\"}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, Object> map = mapper.readValue(testString, Map.class);
        Body streamNotification = mapper.convertValue(map, Body.class);

        log.info(streamNotification);
    }
}
