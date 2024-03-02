package tv.wlg.bot.twitch.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

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

    @GetMapping(value = "/auth", produces = MediaType.TEXT_HTML_VALUE)
    public String authenticate(
            @Param("code")  String code,
            @Param("scope") String scope,
            @Param("state") long state,
            @Param("error") String error,
            @Param("error_description") String errorDescription
    ) {
        if (error != null || errorDescription != null) {
            return String.format(response, "error: " + error + " - " + errorDescription);
        }

        return String.format(response, "ok");
    }
}
