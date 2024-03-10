package tv.wlg.bot.twitch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessToken {
    private String access_token;
    private int expires_in;

    private String token_type;

    private String refresh_token;
    private String[] scope;
}
