package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Subscription {
    private String id;
    private String status;
    private String type;
    private String version;

    private Condition condition;
    private Transport transport;

    private String created_at;
    private int cost;
}
