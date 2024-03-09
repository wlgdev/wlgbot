package tv.wlg.bot.twitch.model;

import lombok.Getter;
import lombok.Setter;
import tv.wlg.bot.twitch.model.eventsub.Metadata;
import tv.wlg.bot.twitch.model.eventsub.Payload;

@Getter
@Setter
public class EventMessage {
    private Metadata metadata;
    private Payload payload;
}
