package net.theevilreaper.dungeon.data.floor;

import dev.morphia.query.experimental.updates.UpdateOperators;
import net.theevilreaper.dungeon.database.DatabaseEntity;
import net.theevilreaper.dungeon.database.MongoDatabase;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The class handles all operation to delete, add or update a floor in the database.
 * //TODO: Add async method variants
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
public class FloorDatabaseHandler implements DatabaseEntity<Floor> {

    private static final Logger FLOOR_LOGGER = LoggerFactory.getLogger(FloorDatabaseHandler.class);
    private final MongoDatabase mongoDatabase;

    public FloorDatabaseHandler(@NotNull MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public void insert(@NotNull Floor model) {
        this.mongoDatabase.getDatastore().insert(model);
    }

    @Override
    public void delete(@NotNull Floor model) {
        this.mongoDatabase.getDatastore().delete(model);
    }

    @Override
    public void update(@NotNull Floor model) {
        var result = this.mongoDatabase.getDatastore().find(Floor.class)
                .update(UpdateOperators.set(model)).execute();
        if (result.getMatchedCount() == 1 && result.getModifiedCount() == 1) {
            FLOOR_LOGGER.warn("Unable to execute update on floor object {}", model);
        }
     }

    @Override
    public List<Floor> getAllEntries() {
        return this.mongoDatabase.getDatastore().find(Floor.class).stream().toList();
    }
}
