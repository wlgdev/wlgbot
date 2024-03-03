package tv.wlg.bot.repository.config;

import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        mongoTemplateRef = "remoteMongoTemplate"
)
public class RemoteMongoConfig {


    @Bean(name = "remoteMongoProperties")
    @ConfigurationProperties(prefix = "mongodb.remote")
    public MongoProperties getRemoteDB() {
        return new MongoProperties();
    }


    @Bean(name = "remoteMongoClient")
    public MongoClient remoteMongoClient(
            @Qualifier("remoteMongoProperties") MongoProperties properties
    ) {
        return MongoConfig.createClient(properties);
    }


    @Bean(name = "remoteDatabaseFactory")
    public MongoDatabaseFactory remoteDatabaseFactory(
            @Qualifier("remoteMongoClient") MongoClient mongoClient,
            @Qualifier("remoteMongoProperties") MongoProperties mongoProperties
    ) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoProperties.getDatabase());
    }


    @Bean(name = "remoteMongoTemplate")
    public MongoTemplate remoteMongoTemplate(
            @Qualifier("remoteDatabaseFactory") MongoDatabaseFactory databaseFactory
    ) {
        return new MongoTemplate(databaseFactory);
    }
}