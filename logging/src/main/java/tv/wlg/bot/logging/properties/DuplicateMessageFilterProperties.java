package tv.wlg.bot.logging.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties("app.logging.filter.duplicate-message-filter")
public class DuplicateMessageFilterProperties {
    public static final String DEFAULT_MARKERS = "FILTERED";
    public static final int DEFAULT_ALLOWED_REPETITIONS = 5;
    public static final int DEFAULT_EXPIRE_AFTER_WRITE_SECONDS = 60;
    public static final int DEFAULT_CACHE_SIZE = 100;
    public static final int DEFAULT_MAX_KEY_LENGTH = 100;

    /**
     * Enables tv.wlg.bot.logging.filter.DuplicateMessageFilter.
     */
    private boolean enabled = false;

    /**
     * Marker that will be used to Filter same log message on repeats.
     */
    private List<String> markers = List.of(DEFAULT_MARKERS);

    /**
     * Number of repeats allowed for log to be filtered.
     */
    private int repeats = DEFAULT_ALLOWED_REPETITIONS;

    /**
     * Seconds after which log will be counted back for repeats.
     */
    private int expire = DEFAULT_EXPIRE_AFTER_WRITE_SECONDS;

    /**
     * How much unique messages can be stored in cache.
     */
    private int cacheSize = DEFAULT_CACHE_SIZE;
}
