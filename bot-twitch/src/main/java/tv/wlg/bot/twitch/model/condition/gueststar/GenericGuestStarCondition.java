package tv.wlg.bot.twitch.model.condition.gueststar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class GenericGuestStarCondition {
    private String broadcaster_user_id;
    private String moderator_user_id;
}
