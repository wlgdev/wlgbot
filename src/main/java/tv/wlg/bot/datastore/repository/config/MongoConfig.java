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
        String propertiesFile = "application-" + parseProperty("RUNTIME_ENV", "local") + ".yml";

        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource(propertiesFile));
        Properties properties = factoryBean.getObject();
        if (properties == null) {
            throw new RuntimeException("cannot initialize: property file not found: " + propertiesFile);
        }

        System.out.println("SET MONGO PROPERTIES FROM: " + propertiesFile);
        String database = resolveValue(properties.getProperty("MONGODB_AUTH_DATABASE"), "default");
        String hostname = resolveValue(properties.getProperty("MONGODB_HOST_NAME"), "localhost");
        int mongoPort = Integer.parseInt(resolveValue(properties.getProperty("MONGODB_PORT_NUMBER"), "27015"));
        String rootUser = resolveValue(properties.getProperty("MONGODB_ROOT_USER"), "rootuser");
        char[] rootPassword = resolveValue(properties.getProperty("MONGODB_ROOT_PASSWORD"), "some_pwd").toCharArray();
        String authDB = resolveValue(properties.getProperty("MONGODB_AUTH_DATABASE"), "authdb");

        System.out.println(database + "\n" + hostname + "\n" + mongoPort + "\n" + rootUser + "\n" + String.valueOf(rootPassword) + "\n" + authDB);

        MongoProperties mongoProperties = new MongoProperties();
        mongoProperties.setDatabase(database);
        mongoProperties.setHost(hostname);
        mongoProperties.setPort(mongoPort);
        mongoProperties.setUsername(rootUser);
        mongoProperties.setPassword(rootPassword);
        mongoProperties.setAuthenticationDatabase(authDB);

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

    @SuppressWarnings("SameParameterValue")
    private static String parseProperty(String propertyName, String defaultValue) {
        String systemProperty = System.getProperty(propertyName);
        String environmentProperty = System.getenv(propertyName);

        String result;
        if (systemProperty != null) {
            result = systemProperty;
        } else if (environmentProperty != null) {
            result = environmentProperty;
        } else {
            return defaultValue;
        }

        return resolveValue(result, defaultValue);
    }

    private static String resolveValue(String property, String defaultValue) {
        if (property.startsWith("${")) {
            String systemValue = System.getProperty(property.substring(2, property.length() - 1));
            String envValue = System.getenv(property.substring(2, property.length() - 1));

            if (systemValue != null) {
                return systemValue;
            } else if (envValue != null) {
                return envValue;
            } else {
                return defaultValue;
            }
        }

        return property;
    }
}