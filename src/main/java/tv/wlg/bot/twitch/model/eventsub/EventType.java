package tv.wlg.bot.twitch.model.eventsub;

public enum EventType {
    CHANNEL_CHAT_MESSAGE_RECEIVED() {
        @Override
        public String asEventName() {
            return "channel.chat.message";
        }
    };

    public abstract String asEventName();
}
