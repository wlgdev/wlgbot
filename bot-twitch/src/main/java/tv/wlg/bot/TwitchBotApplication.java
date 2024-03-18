package tv.wlg.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import tv.wlg.bot.config.ApplicationContextProvider;
import tv.wlg.bot.datastore.model.RefreshToken;
import tv.wlg.bot.datastore.repository.RefreshTokenRepository;
import tv.wlg.bot.datastore.repository.config.MongoConfig;
import tv.wlg.bot.twitch.evensub.EventSocket;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class TwitchBotApplication {
    public static void main(String[] args) {
        MongoConfig.configureMongoDB(); //remove when moved to "server" module

        SpringApplication.run(TwitchBotApplication.class, args);
    }

    @EventListener(ApplicationStartedEvent.class)
    //TODO remove when done with configuration
    public void start() {
        RefreshTokenRepository tokenRepository = ApplicationContextProvider.getContext().getBean(RefreshTokenRepository.class);
        tokenRepository.findAll().forEach(EventSocket::createSocket);

//        RefreshTokenRepository tokenRepository = ApplicationContextProvider.getContext().getBean(RefreshTokenRepository.class);
//
//        LocalDateTime now = LocalDateTime.now();
//        for (int i = 2; i < 3; i++) {
//            String userId = i + "";
//            RefreshToken token = new RefreshToken();
//            token.setUserId(userId);
//            token.setScope("test");
//            token.setRefreshToken("123");
//
//            tokenRepository.save(token);
//        }
//        System.out.println(LocalDateTime.now() + " == " + now);
//
//        String userId = "1";
//        RefreshToken token = new RefreshToken();
//        token.setUserId(userId);
//        token.setScope("test");
//        token.setRefreshToken("123");
//
//        tokenRepository.save(token);
//
//        List<RefreshToken> returned = tokenRepository.findTokensByUserId(userId);
//        RefreshToken returned2 = tokenRepository.findFirstByUserId(userId);
//        RefreshToken returned3 = tokenRepository.findByUserId(userId);
//
//        token.setScope("test - extended");
//        tokenRepository.save(token);
//
//        token.setRefreshToken("refreshed");
//        tokenRepository.save(token);
//
//        token.setUserId("changed");
//        tokenRepository.save(token);
    }
}
