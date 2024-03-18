package tv.wlg.bot.logging.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

public class CommonProperties {
    @Getter
    @Setter
    @Configuration
    @ConfigurationProperties("app.logging.level")
    public static class Level {
        /**
         * Log level for the ROOT.
         */
        private String root;

        /**
         * Log level for the tv.wlg.bot package.
         */
        private String app;
    }
}
