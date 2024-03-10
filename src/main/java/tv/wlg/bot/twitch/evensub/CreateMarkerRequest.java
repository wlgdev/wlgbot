package tv.wlg.bot.twitch.evensub;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import tv.wlg.bot.datastore.model.RefreshToken;
import tv.wlg.bot.twitch.model.AccessToken;

public class CreateMarkerRequest {
    private static final String CREATE_MARKER_REQUEST = "https://api.twitch.tv/helix/streams/markers";
    private RestTemplate restTemplate = new RestTemplate();

    private String REQUEST_BODY_TEMPLATE = """
            {
                "user_id": "%s",
                "description":"%s"
            }
            """;

    public void createMarker(RefreshToken refreshToken, AccessToken accessToken) {
        String request = String.format(REQUEST_BODY_TEMPLATE, refreshToken.getId(), "[BOT] Smiles Trigger");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken.getAccess_token());
        headers.set("Client-Id", "3toh7ur3q0vlzbqtovdgg4zfnc6kp3");

        HttpEntity<String> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(CREATE_MARKER_REQUEST, requestEntity, String.class);
    }
}
