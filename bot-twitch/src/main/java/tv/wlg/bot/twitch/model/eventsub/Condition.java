package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Condition {
    private String from_broadcaster_user_id;
    private String to_broadcaster_user_id;

    private String broadcaster_user_id;
    private String moderator_user_id;
    private String user_id;
}
