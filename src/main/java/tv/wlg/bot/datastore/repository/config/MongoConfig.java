package tv.wlg.bot.datastore.repository.config;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.core.io.ClassPathResource;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MongoConfig {
    static MongoClient createClient(
            MongoProperties properties
    ) {
        MongoCredential credential = MongoCredential.createCredential(
                properties.getUsername(),
                properties.getAuthenticationDatabase(),
                properties.getPassword()
        );

        return MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(
                                Collections.singletonList(
                                        new ServerAddress(properties.getHost(), properties.getPort())
                                )
                        )
                )
                .credential(credential)
                .build()
        );
    }

    public static void configureMongoDB() {
        String runtimeEnv = System.getProperty("RUNTIME_ENV", "local");
        String propertiesFile = "application-" + runtimeEnv + ".yml";

        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource(propertiesFile));
        Properties properties = factoryBean.getObject();
        if (properties == null) {
            throw new RuntimeException("cannot initialize: property file not found: " + propertiesFile);
        }

        System.out.print("SET MONGO PROPERTIES FROM: " + propertiesFile);
        System.out.println(" : " + properties);
        MongoProperties mongoProperties = new MongoProperties();
        mongoProperties.setDatabase(properties.getProperty("MONGODB_AUTH_DATABASE"));
        mongoProperties.setHost(properties.getProperty("MONGODB_HOST_NAME"));
        mongoProperties.setPort(Integer.parseInt(properties.getProperty("MONGODB_PORT_NUMBER")));
        mongoProperties.setUsername(properties.getProperty("MONGODB_ROOT_USER"));
        mongoProperties.setPassword(properties.getProperty("MONGODB_ROOT_PASSWORD").toCharArray());
        mongoProperties.setAuthenticationDatabase(properties.getProperty("MONGODB_AUTH_DATABASE"));

        BasicDBObject getTrustedUser = new BasicDBObject(
                "usersInfo",
                new BasicDBObject(
                        "user",
                        properties.getProperty("mongodb.trusted.username")
                ).append(
                        "db",
                        "admin"
                )
        );
        BasicDBObject getRemoteUser = new BasicDBObject(
                "usersInfo",
                new BasicDBObject(
                        "user",
                        properties.getProperty("mongodb.remote.username")
                ).append(
                        "db",
                        "admin"
                )
        );

        try(MongoClient client = createClient(mongoProperties)) {
            MongoDatabase db = client.getDatabase(mongoProperties.getDatabase());

            var trustedUser = db.runCommand(getTrustedUser).get("users", List.class);
            var remoteUser = db.runCommand(getRemoteUser).get("users", List.class);
            if (trustedUser.isEmpty()) {
                createDBUser(db, properties.getProperty("mongodb.trusted.username"), properties.getProperty("mongodb.trusted.password"), properties.getProperty("mongodb.trusted.database"));
            }
            if (remoteUser.isEmpty()) {
                createDBUser(db, properties.getProperty("mongodb.remote.username"), properties.getProperty("mongodb.remote.password"), properties.getProperty("mongodb.remote.database"));
            }
        } catch (Exception ignore) {}
    }

    private static void createDBUser(MongoDatabase db, String userName, String password, String database) {
        @SuppressWarnings("unused")
        BasicDBObject dropUser = new BasicDBObject(
                "dropUser",
                userName
        );
        BasicDBObject createUser = new BasicDBObject(
                "createUser",
                userName
        )
                .append("pwd", password)
                .append("roles", Collections.singletonList(
                        new BasicDBObject(
                                "role",
                                "readWrite"
                        ).append("db", database)));

        db.runCommand(createUser);
    }
}