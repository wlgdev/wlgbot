package tv.wlg.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import tv.wlg.bot.config.ApplicationContextProvider;
import tv.wlg.bot.model.Token;
import tv.wlg.bot.repository.TokenRepository;
import tv.wlg.bot.repository.config.MongoConfig;

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
        TokenRepository tokenRepository = ApplicationContextProvider.getContext().getBean(TokenRepository.class);

        Token token = new Token();
        token.setUserId("4");
        token.setScope("test");
        token.setRefreshToken("123");

        tokenRepository.save(token);

        List<Token> returned = tokenRepository.findTokensByUserId("4");
        Token returned2 = tokenRepository.findFirstByUserId("4");
        Token returned3 = tokenRepository.findByUserId("4");
    }
}
