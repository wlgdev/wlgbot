package tv.wlg.bot.twitch.model.condition.status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StreamOfflineCondition {
    private String broadcaster_user_id;
}
