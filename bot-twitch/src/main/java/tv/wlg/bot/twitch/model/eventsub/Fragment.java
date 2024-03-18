package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fragment {
    private String type;
    private String text;

    private Emote emote;
    private Mention mention;
    private Cheermote cheermote;
}
