package tv.wlg.bot.twitch.model.eventsub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {
    private String broadcaster_user_id;
    private String broadcaster_user_login;
    private String broadcaster_user_name;
    private String chatter_user_id;
    private String chatter_user_login;
    private String chatter_user_name;
    private Badge[] badges;
    private String color;

    private String message_id;
    private String message_type;
    private Message message;

    private Cheer cheer;
    private Reply reply;

    private String channel_points_custom_reward_id;
}
