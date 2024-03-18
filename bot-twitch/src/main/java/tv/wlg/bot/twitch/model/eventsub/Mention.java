package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mention {
    private String user_id;
    private String user_login;
    private String user_name;
}
