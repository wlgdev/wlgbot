package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Metadata {
    private String message_id;
    private String message_type;
    private String message_timestamp;

    private String subscription_type;
    private String subscription_version;
}
