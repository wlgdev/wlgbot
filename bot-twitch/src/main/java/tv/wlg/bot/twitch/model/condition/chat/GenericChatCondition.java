package tv.wlg.bot.twitch.model.condition.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract class GenericChatCondition {
    private String broadcaster_user_id;
    private String user_id;
}
