package tv.wlg.bot.twitch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetails {
    private String id;
    private String login;
    private String display_name;
    private String type;
    private String broadcaster_type;
    private String description;
    private String profile_image_url;
    private String offline_image_url;
    private int view_count;
    private String email;
    private String created_at;
}
