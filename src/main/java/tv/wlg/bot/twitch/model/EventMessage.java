package tv.wlg.bot.twitch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMessage {
    private Metadata metadata;
    private Payload payload;
}
