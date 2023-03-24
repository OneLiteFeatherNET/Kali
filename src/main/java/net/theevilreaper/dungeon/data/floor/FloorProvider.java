package net.theevilreaper.dungeon.data.floor;

import net.theevilreaper.dungeon.database.MongoDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class FloorProvider implements FloorGetMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(FloorProvider.class);
    private final Lock lock;
    private Set<Floor> floorDTOS;
    private FloorDatabaseHandler floorDatabaseHandler;

    public FloorProvider(MongoDatabase database) {
        if (database != null) {
            this.floorDatabaseHandler = new FloorDatabaseHandler(database);
        }
        this.lock = new ReentrantLock();
        this.floorDTOS = loadFloors();
    }

    private Set<Floor> loadFloors() {
        this.floorDTOS = new HashSet<>();

        if (this.floorDatabaseHandler == null) {
            return floorDTOS;
        }

        var databaseFloors = this.floorDatabaseHandler.getAllEntries();

        if (!databaseFloors.isEmpty())  {
            this.floorDTOS.addAll(databaseFloors);
            LOGGER.info("Found {} Floor objects in the database", floorDTOS.size());
        }
        return floorDTOS;
    }

    public void addFloor(@NotNull Floor floorDTO) {
        try {
            lock.lock();
            if (this.floorDTOS.add(floorDTO)) {
                this.floorDatabaseHandler.insert(floorDTO);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeFloor(@NotNull Floor floorDTO) {
        try {
            lock.lock();
            if (this.floorDTOS.remove(floorDTO)) {
                this.floorDatabaseHandler.delete(floorDTO);
            }
        } finally {
            lock.unlock();
        }
    }

    public @Nullable Floor getFloor(@NotNull String name) {
        try {
            lock.lock();
            if (floorDTOS.isEmpty()) return null;

            Iterator<Floor> iterator = floorDTOS.iterator();

            Floor floorDTO = null;

            while (iterator.hasNext() && floorDTO == null) {
                var current = iterator.next();
                if (current.getExternalName().equals(name)) floorDTO = current;

            }
            return floorDTO;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public @Nullable Floor getFloorById(@NotNull UUID uuid) {
        try {
            lock.lock();
            if (floorDTOS.isEmpty()) return null;
            Iterator<Floor> iterator = floorDTOS.iterator();

            Floor floorDTO = null;

            while (iterator.hasNext() && floorDTO == null) {
                var current = iterator.next();
                if (current.getUUID().equals(uuid)) floorDTO = current;

            }
            return floorDTO;

        } finally {
            lock.unlock();
        }
    }

    public Set<Floor> getFloors() {
        try {
            lock.lock();
            return floorDTOS;
        } finally {
            lock.unlock();
        }
    }
}
