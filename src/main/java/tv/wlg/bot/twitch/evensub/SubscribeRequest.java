package tv.wlg.bot.twitch.evensub;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SubscribeRequest {
    private static final String SUBSCRIBE_REQUEST_URL = "https://api.twitch.tv/helix/eventsub/subscriptions";
    RestTemplate restTemplate = new RestTemplate();

    private static final String REQUEST_BODY_TEMPLATE = """
            {
                "type": "%s",
                "version": "1",
                "condition": {
                    "user_id": "%s",
                    "broadcaster_user_id": "%s"
                },
                "transport": {
                    "method": "websocket",
                    "session_id": "%s"
                }
            }
            """;

    public void createSubscription(String accessToken, String clientId, String sessionId, String userId, Event event) {
        String request = String.format(REQUEST_BODY_TEMPLATE, event.asEventName(), userId, "30814134", sessionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Client-Id", clientId);

        HttpEntity<String> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(SUBSCRIBE_REQUEST_URL, requestEntity, String.class);
        System.out.println("response: " + response);
    }
}
