package tv.wlg.bot.twitch.evensub;

import tv.wlg.bot.datastore.model.RefreshToken;
import tv.wlg.bot.twitch.model.AccessToken;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler {
    private static final List<String> smileTriggers = new ArrayList<>() {{
        add(":D");
        add("KEK");
        add("LUL");
        add("LOL");
        add("axaxa"); //eng
        add("ахаха"); //cyrillic

        add("ddx");
        add("peka");
        add("rolf");
        add("monka");
        add("BOOBA");
        add("xddShrug");
        add("peepoGiggles");

        add("wlgLook");
        add("wlgSmile");
        add("wlgRolling");
    }};

    private final int numberOfMessages = 8;
    private final int timeLimit = 5 * 1000; // $numberOfMessages in 5sec = create marker
    private final int cooldown = 60 * 1000; // 1 marker per 60 seconds

    private long lastTriggerExexecudedAt = System.currentTimeMillis() - cooldown; //last trigger time
    private List<Long> messagesList = new ArrayList<>();

    private final CreateMarkerRequest createMarkerRequest = new CreateMarkerRequest();

    public void handleMessage(String userName, String messageText, RefreshToken refreshToken, AccessToken accessToken) {
        if (userName.equalsIgnoreCase("hlebu6ek") || userName.equalsIgnoreCase("foxlex")) {
            if (messageText.equalsIgnoreCase("!marker")) {
                createMarker(refreshToken, accessToken);
            }

            return;
        }

        long now = System.currentTimeMillis();
        if ((now - lastTriggerExexecudedAt) < cooldown) {
            return;
        }

        boolean isTrigger = true;
        for (String word : messageText.toLowerCase().split(" ")) {
            boolean isContains = false;
            for (String smile : smileTriggers) {
                if (word.contains(smile.toLowerCase())) {
                    isContains = true;
                    break;
                }
            }

            if (!isContains) {
                isTrigger = false;
                break;
            }
        }

        if (!isTrigger) {
            return;
        }

        for (int i = messagesList.size() - 1; i >= 0; i--) {
            if ((now - messagesList.get(i)) >= timeLimit ) {
                messagesList.remove(i);
            }
        }
        messagesList.add(now);

        if (messagesList.size() >= numberOfMessages) {
            lastTriggerExexecudedAt = now;
            createMarker(refreshToken, accessToken);
        }
    }

    private void createMarker(RefreshToken refreshToken, AccessToken accessToken) {
        createMarkerRequest.createMarker(refreshToken, accessToken);
        System.out.println("CREATE MARKER");
    }
}
