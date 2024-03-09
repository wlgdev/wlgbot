package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payload {
    private Subscription subscription;
    private Session session;
    private Event event;
}
