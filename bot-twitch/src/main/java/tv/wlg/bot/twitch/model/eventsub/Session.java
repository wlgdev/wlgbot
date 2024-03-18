package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Session {
    private String id;
    private String status;
    private String connected_at;
    private String keepalive_timeout_seconds;

    private String reconnect_url;
}
