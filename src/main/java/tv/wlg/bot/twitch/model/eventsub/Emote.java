package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Emote {
    private String id;
    private String emote_set_id;
    private String owner_id;
    private String[] format;
}
