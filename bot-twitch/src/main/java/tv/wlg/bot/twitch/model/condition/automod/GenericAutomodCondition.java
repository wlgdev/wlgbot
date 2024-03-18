package tv.wlg.bot.twitch.model.condition.automod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class GenericAutomodCondition {
    private String broadcaster_user_id;
    private String moderator_user_id;
}
