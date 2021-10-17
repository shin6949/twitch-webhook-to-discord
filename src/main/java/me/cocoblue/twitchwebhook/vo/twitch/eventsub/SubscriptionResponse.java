package me.cocoblue.twitchwebhook.vo.twitch.eventsub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponse {
    @JsonProperty("total")
    private int total;
    @JsonProperty("data")
    private List<Subscription> subscriptionList;
    @JsonProperty("max_total_cost")
    private int macTotalCost;
    @JsonProperty("total_cost")
    private int totalCost;
    @JsonProperty("pagination")
    private Map<String, String> pagination;
}
