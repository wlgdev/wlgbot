package tv.wlg.bot.twitch.token;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import tv.wlg.bot.twitch.model.UserListDetails;

public class UserDetailsRequest {
    private static final String USER_DETAILS_REQUEST_URL = "https://api.twitch.tv/helix/users";
    RestTemplate restTemplate = new RestTemplate();

    public UserListDetails getUserDetails(String accessToken) {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Client-Id", "3toh7ur3q0vlzbqtovdgg4zfnc6kp3");

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(USER_DETAILS_REQUEST_URL, HttpMethod.GET, requestEntity, UserListDetails.class).getBody();
    }
}
