package net.theevilreaper.dungeon.location;

import com.google.gson.GsonBuilder;
import de.icevizion.aves.file.FileHandler;
import de.icevizion.aves.file.GsonFileHandler;
import de.icevizion.aves.file.gson.PositionGsonAdapter;
import de.icevizion.aves.map.BaseMap;
import de.icevizion.aves.util.Players;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class LocationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationProvider.class);
    private static final String MAP_FILE = "map.json";
    private final Path rootPath;
    private final Pos defaultPos = new Pos(0, 50, 0);
    private final FileHandler fileHandler;
    private BaseMap map;

    public LocationProvider(@NotNull Path rootMapPath) {
        this.rootPath = rootMapPath.resolve("world");
        var posAdapter = new PositionGsonAdapter();
        var gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
                .registerTypeAdapter(Pos.class, posAdapter)
                .registerTypeAdapter(Vec.class, posAdapter).create();
        this.fileHandler = new GsonFileHandler(gson);
    }

    public void load() {
        var loadedMap = this.fileHandler.load(Paths.get(rootPath.toString(), MAP_FILE), BaseMap.class);
        if (loadedMap.isEmpty()) {
            this.map = new BaseMap();
            LOGGER.info("Created empty map. Please setup the map");
        } else {
            this.map = loadedMap.get();
        }
    }

    public boolean save() {
        if (map == null) return false;
        this.fileHandler.save(Paths.get(rootPath.toString(), MAP_FILE), map);
        LOGGER.info("Saved map into the path: {}", rootPath);
        return true;
    }

    public void setSpawn(@NotNull Pos pos) {
        this.map.setSpawn(pos);
    }

    public void teleportToSpawn(@NotNull Player player) {
        Players.hasInstance(player);

        if (map == null || map.getSpawn() == null) {
            player.teleport(defaultPos);
            return;
        }

        player.teleport(this.map.getSpawn());
    }

    public Pos getSpawnPos() {
        if (map == null || map.getSpawn() == null) {
            return defaultPos;
        }

        return this.map.getSpawn();
    }

    public Pos getDefaultPos() {
        return defaultPos;
    }
}
