package tv.wlg.bot.twitch.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping(value = "auth", produces = MediaType.TEXT_HTML_VALUE)
    public String authenticate(
            @RequestParam(name = "code")  String code,
            @RequestParam(name = "scope") String scope,
            @RequestParam(name = "state") long state,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_description", required = false) String errorDescription
    ) {
        if (error != null || errorDescription != null) {
            return String.format(response, "error: " + error + " - " + errorDescription);
        }

        return String.format(response, "ok");
    }
}
