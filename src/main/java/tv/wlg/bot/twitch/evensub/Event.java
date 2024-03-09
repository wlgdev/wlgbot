package tv.wlg.bot.twitch.evensub;

public enum Event {
    CHANNEL_CHAT_MESSAGE_RECEIVED() {
        @Override
        public String asEventName() {
            return "channel.chat.message";
        }
    };

    public abstract String asEventName();
}
