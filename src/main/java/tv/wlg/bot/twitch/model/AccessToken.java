package tv.wlg.bot.twitch.model;

import lombok.Getter;
import lombok.Setter;
import tv.wlg.bot.twitch.token.RefreshTokenRequest;

@Getter
@Setter
public class AccessToken {
    private String access_token;
    private int expires_in;

    private String token_type;

    private String refresh_token;
    private String[] scope;

    private long created_at = System.currentTimeMillis();
    public String getAccess_token() {
        if (isExpired()) {
            AccessToken refreshedToken = new RefreshTokenRequest().refreshAccessToken(refresh_token);
            this.access_token = refreshedToken.getAccess_token();
            this.expires_in = refreshedToken.getExpires_in();
            this.created_at = System.currentTimeMillis();
        }

        return access_token;
    }

    private boolean isExpired() {
        //if token created later than expires_in_sec - 10sec
        return (System.currentTimeMillis() - created_at) > (expires_in * 1000L - 10000);
    }
}
