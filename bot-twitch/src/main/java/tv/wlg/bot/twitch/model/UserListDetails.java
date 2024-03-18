package tv.wlg.bot.twitch.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListDetails {
    private UserDetails[] data;

    public UserDetails getFirst() {
        return data[0];
    }
}
