package tv.wlg.bot.twitch.token;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tv.wlg.bot.config.Constants;
import tv.wlg.bot.twitch.model.AccessToken;

public class RefreshTokenRequest {
    private static final String REFRESH_TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    RestTemplate restTemplate = new RestTemplate();

    public AccessToken refreshAccessToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parametersBody = new LinkedMultiValueMap<>() {{
            add("client_id", Constants.clientId);
            add("client_secret", Constants.clientSecret);
            add("grant_type", "refresh_token");
            add("refresh_token", refreshToken);
        }};

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parametersBody, headers);
        return restTemplate.postForObject(REFRESH_TOKEN_URL, requestEntity, AccessToken.class);
    }
}
