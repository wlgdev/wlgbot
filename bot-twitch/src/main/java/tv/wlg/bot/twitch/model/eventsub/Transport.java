package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transport {
    private String method;
    private String session_id;
    private String callback;
    private String secret;
}
