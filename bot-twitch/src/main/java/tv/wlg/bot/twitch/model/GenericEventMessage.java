package tv.wlg.bot.twitch.model;

import lombok.Getter;
import lombok.Setter;
import tv.wlg.bot.twitch.model.eventsub.Metadata;

@Getter
@Setter
public abstract class GenericEventMessage {
    private Metadata metadata;
}
