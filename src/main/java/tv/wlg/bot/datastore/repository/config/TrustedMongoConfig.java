package tv.wlg.bot.datastore.repository.config;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "tv.wlg.bot.datastore.repository",
        mongoTemplateRef = "trustedMongoTemplate"
)
@DependsOn("applicationContextProvider")
public class TrustedMongoConfig {
    @Primary
    @Bean(name = "trustedMongoProperties")
    @ConfigurationProperties(prefix = "mongodb.trusted")
    public MongoProperties getTrustedDB() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "trustedMongoClient")
    public MongoClient trustedMongoClient(
            @Qualifier("trustedMongoProperties") MongoProperties properties
    ) {
        return MongoConfig.createClient(properties);
    }

    @Primary
    @Bean(name = "trustedDatabaseFactory")
    public MongoDatabaseFactory trustedDatabaseFactory(
            @Qualifier("trustedMongoClient") MongoClient mongoClient,
            @Qualifier("trustedMongoProperties") MongoProperties mongoProperties
    ) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }

    @Primary
    @Bean(name = "trustedMongoTemplate")
    public MongoTemplate trustedMongoTemplate(
            @Qualifier("trustedDatabaseFactory") MongoDatabaseFactory databaseFactory
    ) {
        return new MongoTemplate(databaseFactory);
    }
}