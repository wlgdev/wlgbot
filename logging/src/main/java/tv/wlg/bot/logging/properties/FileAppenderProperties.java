package tv.wlg.bot.logging.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("app.logging.file")
public class FileAppenderProperties {
    /**
     * Enables logging to the file.
     */
    private boolean enabled = false;

    /**
     * Updates application's file name for the logging.
     */
    private String name = "app.log";
}
