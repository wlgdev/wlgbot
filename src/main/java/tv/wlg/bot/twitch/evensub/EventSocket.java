package tv.wlg.bot.twitch.evensub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tv.wlg.bot.datastore.model.RefreshToken;
import tv.wlg.bot.twitch.model.AccessToken;
import tv.wlg.bot.twitch.model.EventMessage;
import tv.wlg.bot.twitch.model.eventsub.EventType;
import tv.wlg.bot.twitch.token.RefreshTokenRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class EventSocket {
    private static final Logger log = LoggerFactory.getLogger(EventSocket.class);

    private static final String TWITCH_EVENTSUB_URL = "wss://eventsub.wss.twitch.tv/ws?keepalive_timeout_seconds=30";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Session session;
    private AccessToken accessToken;
    private RefreshToken refreshToken;

    public static EventSocket createSocket(RefreshToken refreshToken) {
        try {
            return new EventSocket(refreshToken);
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public EventSocket(RefreshToken refreshToken) throws URISyntaxException, DeploymentException, IOException {
        this.refreshToken = refreshToken;
        this.accessToken = new RefreshTokenRequest().refreshAccessToken(refreshToken.getRefreshToken());

        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        session = webSocketContainer.connectToServer(this, new URI(TWITCH_EVENTSUB_URL));
        System.out.println("connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws JsonProcessingException {
        log.debug("NEW MESSAGE(session: {}): {}", session.getId(), message);
        EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);

        if (eventMessage.getMetadata().getMessage_type().equalsIgnoreCase("session_welcome")) {
            log.debug("SUBSCRIBE TO CHAT MESSAGES");
            new SubscribeRequest().createSubscription(
                    accessToken.getAccess_token(),
                    "3toh7ur3q0vlzbqtovdgg4zfnc6kp3",
                    eventMessage.getPayload().getSession().getId(),
                    refreshToken.getUserId(),
                    EventType.CHANNEL_CHAT_MESSAGE_RECEIVED
            );
            return;
        }
        if (eventMessage.getMetadata().getMessage_type().equalsIgnoreCase("session_keepalive")) {
            log.debug("KEEP ALIVE CHECK: {}", eventMessage.getMetadata().getMessage_timestamp());
            return;
        }

        String subsciptionType = eventMessage.getMetadata().getSubscription_type();
        if (subsciptionType != null && subsciptionType.equalsIgnoreCase("channel.chat.message")) {
            log.info("CHAT MESSAGE IS:  {}:  {}", eventMessage.getPayload().getEvent().getChatter_user_login(), eventMessage.getPayload().getEvent().getMessage().getText());
        }
    }

    @OnMessage
    public void onPongMessage(PongMessage pongMessage) {
        log.info("PONG MESSAGE(session: {}): {}", session.getId(), pongMessage);
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("SESSION OPENED: {}", session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("SESSION CLOSED: {}, {}", session.getId(), closeReason);
    }
}
