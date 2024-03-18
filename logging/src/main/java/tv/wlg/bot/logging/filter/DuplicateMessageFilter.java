package tv.wlg.bot.logging.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static tv.wlg.bot.logging.properties.DuplicateMessageFilterProperties.*;

/**
 * Custom DuplicateMessageFilter that can be used in spring logback.xml file to enable log message filtering.
 * Filter will skip messages that repeats more than allowedRepetitions in expireAfterWriteSeconds time.
 *
 * @see ch.qos.logback.classic.turbo.DuplicateMessageFilter
 */
@Getter
public class DuplicateMessageFilter extends TurboFilter {
    @Setter private int expireAfterWriteSeconds = DEFAULT_EXPIRE_AFTER_WRITE_SECONDS;
    @Setter private int allowedRepetitions = DEFAULT_ALLOWED_REPETITIONS;
    @Setter private String includeMarkers = DEFAULT_MARKERS;
    @Setter private int cacheSize = DEFAULT_CACHE_SIZE;

    @Getter(AccessLevel.NONE) private Set<Marker> includeMarkersList = new HashSet<>();
    @Getter(AccessLevel.NONE) private Cache<String, Integer> cache;

    @Override
    public void start() {
        includeMarkersList = parseMarkers(includeMarkers);
        this.cache = buildCache();
        super.start();
    }

    @Override
    public void stop() {
        cache.invalidateAll();
        cache = null;

        super.stop();
    }

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] objects, Throwable throwable) {
        if (marker != null && includeMarkersList.contains(marker)) {
            return decideBasedOnRepeatsCount(format);
        }

        return FilterReply.NEUTRAL;
    }

    private FilterReply decideBasedOnRepeatsCount(String format) {
        int count = 0;
        if (format != null && !format.isEmpty()) {
            final String key = format.substring(0, Math.min(DEFAULT_MAX_KEY_LENGTH, format.length()));
            final Integer cachedCount = cache.getIfPresent(key);

            if (cachedCount != null) {
                count = cachedCount + 1;
            }
            cache.put(key, count);
        }

        return (count < allowedRepetitions) ? FilterReply.NEUTRAL : FilterReply.DENY;
    }

    private Set<Marker> parseMarkers(String markers) {
        final List<String> parsedMarkers = Arrays.asList(markers.split("[,\\s]"));
        return parsedMarkers.stream().filter(strring -> !strring.equalsIgnoreCase(""))
                .map(String::trim)
                .map(MarkerFactory::getMarker)
                .collect(Collectors.toSet());
    }

    private Cache<String, Integer> buildCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(expireAfterWriteSeconds))
                .initialCapacity(cacheSize)
                .maximumSize(cacheSize)
                .build();
    }
}
