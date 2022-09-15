package net.theevilreaper.dungeon.data.floor;

import net.theevilreaper.dungeon.database.MongoDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class FloorProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FloorProvider.class);
    private final Lock lock;
    private Set<Floor> floors;
    private FloorDatabaseHandler floorDatabaseHandler;

    public FloorProvider(MongoDatabase database) {
        if (database != null) {
            this.floorDatabaseHandler = new FloorDatabaseHandler(database);
        }
        this.lock = new ReentrantLock();
        this.floors = loadFloors();
    }

    private Set<Floor> loadFloors() {
        this.floors = new HashSet<>();

        if (this.floorDatabaseHandler == null) {
            return floors;
        }

        var databaseFloors = this.floorDatabaseHandler.getAllEntries();

        if (!databaseFloors.isEmpty())  {
            this.floors.addAll(databaseFloors);
            LOGGER.info("Found {} Floor objects in the database", floors.size());
        }
        return floors;
    }

    public void addFloor(@NotNull Floor floor) {
        try {
            lock.lock();
            if (this.floors.add(floor)) {
                this.floorDatabaseHandler.insert(floor);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeFloor(@NotNull Floor floor) {
        try {
            lock.lock();
            if (this.floors.remove(floor)) {
                this.floorDatabaseHandler.delete(floor);
            }
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    public Floor getFloor(@NotNull String name) {
        try {
            lock.lock();
            if (floors.isEmpty()) return null;

            Iterator<Floor> iterator = floors.iterator();

            Floor floor = null;

            while (iterator.hasNext() && floor == null) {
                var current = iterator.next();
                if (current.getName().equals(name)) floor = current;

            }
            return floor;
        } finally {
            lock.unlock();
        }
    }

    public Set<Floor> getFloors() {
        try {
            lock.lock();
            return floors;
        } finally {
            lock.unlock();
        }
    }
}
