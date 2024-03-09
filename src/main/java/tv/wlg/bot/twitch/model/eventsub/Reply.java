package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reply {
    private String parent_message_id;
    private String parent_message_body;
    private String parent_user_id;
    private String parent_user_name;
    private String parent_user_login;
    private String thread_message_id;
    private String thread_user_id;
    private String thread_user_name;
    private String thread_user_login;
}
