package net.theevilreaper.dungeon.database;

import com.google.gson.JsonObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import net.theevilreaper.dungeon.database.codec.MaterialCodec;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * The class handles the database connection to a MongoDB server instance.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class MongoDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDatabase.class);

    private final MongoClient mongoClient;
    private final Datastore morphia;

    public MongoDatabase(@NotNull String host, @NotNull String password, @NotNull String user, @NotNull String database, int port) {
        var credentials = getCredentials(user, password, database);
        var settings = getSettings(host, credentials, port);
        this.mongoClient = MongoClients.create(settings);
        this.morphia = Morphia.createDatastore(mongoClient, database);
    }

    public MongoDatabase(@NotNull JsonObject dataObject) {
        var host = dataObject.get("host").getAsString();
        var port = dataObject.get("port").getAsInt();
        var user = dataObject.get("user").getAsString();
        var password = dataObject.get("password").getAsString();
        var database = dataObject.get("database").getAsString();

        var credentials = getCredentials(user, password, database);
        var settings = getSettings(host, credentials, port);

        this.mongoClient = MongoClients.create(settings);
        this.morphia = Morphia.createDatastore(mongoClient, database);
    }

    /**
     * Creates a new {@link MongoCredential} object.
     * @param userName the username for the credentials
     * @param password the password for the credentials
     * @param database the username for the credentials
     * @return the created {@link MongoCredential} object
     */
    @Contract("_, _, _ -> new")
    private @NotNull MongoCredential getCredentials(@NotNull String userName, @NotNull String password, @NotNull String database) {
        return MongoCredential
                .createCredential(userName, database, password.toCharArray());
    }

    /**
     * Creates a new {@link MongoClientSettings} object with the given data.
     * @param host the host from the server
     * @param mongoCredential the {@link MongoCredential} for the settings
     * @param port the port from the server
     * @return the created {@link MongoClientSettings}
     */
    @Contract("_, _, _ -> new")
    private @NotNull MongoClientSettings getSettings(@NotNull String host, @NotNull MongoCredential mongoCredential, int port) {
        return MongoClientSettings.builder()
                .credential(mongoCredential)
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .codecRegistry(CodecRegistries.fromRegistries(
                            MongoClientSettings.getDefaultCodecRegistry(),
                            CodecRegistries.fromCodecs(new MaterialCodec())
                        )
                )
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .build();
    }

    /**
     * Closes the connection to the database if one exists.
     */
    public void disconnect() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
            LOGGER.info("Closing database connection");
        }
    }

    /**
     * Returns the instance to the created {@link MongoClient}.
     * @return the underlying instance
     */
    @NotNull
    public Datastore getDatastore() {
        return this.morphia;
    }
}
