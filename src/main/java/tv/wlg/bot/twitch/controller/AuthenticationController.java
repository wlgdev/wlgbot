package tv.wlg.bot.twitch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tv.wlg.bot.datastore.model.RefreshToken;
import tv.wlg.bot.datastore.repository.RefreshTokenRepository;
import tv.wlg.bot.twitch.evensub.EventSocket;
import tv.wlg.bot.twitch.model.AccessToken;
import tv.wlg.bot.twitch.model.UserListDetails;
import tv.wlg.bot.twitch.token.RetrieveTokenRequest;
import tv.wlg.bot.twitch.token.UserDetailsRequest;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final RefreshTokenRepository refreshTokenRepository;

    @SuppressWarnings("FieldCanBeLocal")
    private final String response = """
            <html>
              <head>
                <title>[BOT] Stream Assistant</title>
              </head>
              <body style="background-color:#2F3136;font-size:96px">
                <p style="color:white">%s</p>
              </body>
            </html>
            """;

    @GetMapping(value = "auth", produces = MediaType.TEXT_HTML_VALUE)
    public String authenticate(
            @RequestParam(name = "state") String state,
            @RequestParam(name = "code", required = false)  String code,
            @RequestParam(name = "scope", required = false) String scope,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription
    ) {
        if (error != null || errorDescription != null) {
            return String.format(response, "error: " + error + " - " + errorDescription);
        }

        RetrieveTokenRequest tokenRequest = new RetrieveTokenRequest();
        UserDetailsRequest userDetailsRequest = new UserDetailsRequest();

        AccessToken accessToken = tokenRequest.retrieveToken(code);
        UserListDetails userListDetails = userDetailsRequest.getUserDetails(accessToken.getAccess_token());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(accessToken.getRefresh_token());
        refreshToken.setUserId(userListDetails.getFirst().getId());
        refreshToken.setScope(Arrays.toString(accessToken.getScope()));
        refreshTokenRepository.save(refreshToken);

        //run socket chat read after authorized - TEMPORARY
        //TODO remove after implemented reconnect on StartUp of APP
        EventSocket.createSocket(refreshToken);

        return String.format(response, "ok");
    }
}
