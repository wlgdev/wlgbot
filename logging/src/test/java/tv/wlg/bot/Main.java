package tv.wlg.bot;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import static tv.wlg.bot.logging.Marker.FILTERED;

@Slf4j
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        for (int i = 1; i < 100; i++) {
            log.info(FILTERED, "MESSAGE TO BE TESTED: {}", i);
        }

        log.debug("TEST DEBUG 1");
        log.debug("TEST DEBUG 2");
        log.debug("TEST DEBUG 3");

        Marker EXAMPLE = MarkerFactory.getMarker("EXAMPLE");
        for (int i = 1; i < 100; i++) {
            log.info(EXAMPLE, "MESSAGE TO BE VERIFIED: {}", i);
        }

        try {
            Thread.sleep(61000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i < 100; i++) {
            log.info(FILTERED, "MESSAGE TO BE TESTED: {}", i);
        }
    }
}
