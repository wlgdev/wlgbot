package tv.wlg.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {
    public static String clientId;
    public static String clientSecret;
    public static String redirectUrl;

    @Value("${app.client.id}")
    public void setClientId(String clientId) {
        Constants.clientId = clientId;
    }

    @Value("${app.client.secret}")
    public void setClientSecret(String clientSecret) {
        Constants.clientSecret = clientSecret;
    }

    @Value("${app.client.redirect}")
    public void setRedirectUrl(String redirectUrl) {
        Constants.redirectUrl = redirectUrl;
    }
}
