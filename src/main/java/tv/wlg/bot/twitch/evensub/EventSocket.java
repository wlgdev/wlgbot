package tv.wlg.bot.twitch.evensub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import tv.wlg.bot.twitch.model.EventMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
@Controller
public class EventSocket {
    private static final Logger log = LoggerFactory.getLogger(EventSocket.class);

    private static final String TWITCH_EVENTSUB_URL = "wss://eventsub.wss.twitch.tv/ws?keepalive_timeout_seconds=30";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Session session;

    public static EventSocket createSocket() {
        try {
            return new EventSocket();
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public EventSocket() throws URISyntaxException, DeploymentException, IOException {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        session = webSocketContainer.connectToServer(this, new URI(TWITCH_EVENTSUB_URL));
        System.out.println("connected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws JsonProcessingException {
        log.info("NEW MESSAGE(session: {}): {}", session.getId(), message);

        if (!message.contains("session_welcome")) {
            return;
        }
        EventMessage eventMessage = objectMapper.readValue(message, EventMessage.class);
        log.info("EVENT MESSAGE IS: {}", eventMessage);

        new SubscribeRequest().createSubscription(
                "zud3pwsgj6awf9vr1gqtrbtwim1y8o",
                "gp762nuuoqcoxypju8c569th9wz7q5",
                eventMessage.getPayload().getSession().getId(),
                "42987636",
                Event.CHANNEL_CHAT_MESSAGE_RECEIVED
        );

        //for access token (temporary)
        //https://twitchtokengenerator.com/
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
