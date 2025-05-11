package net.theevilreaper.dungeon.map;

import net.minestom.server.MinecraftServer;
import net.theevilreaper.aves.file.GsonFileHandler;
import net.theevilreaper.aves.map.AbstractMapProvider;
import net.theevilreaper.aves.map.BaseMap;
import net.theevilreaper.aves.map.MapEntry;
import net.theevilreaper.kali.common.MapFilters;
import net.theevilreaper.kali.common.gson.GsonUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public final class EditorMapProvider extends AbstractMapProvider {

    public EditorMapProvider() {
        super(new GsonFileHandler(GsonUtil.DEFAULT_GSON), MapFilters::filterMapsForSetup);

        Path mapFile = MapFilters.ORIGIN_FOLDER.resolve("maps");

        if (!Files.exists(mapFile)) {
            throw new IllegalArgumentException("The map folder must not be null");
        }

        this.loadMapEntries(mapFile);

        if (this.getEntries().isEmpty()) {
            throw new IllegalStateException("No maps found in the map folder");
        }

        MapEntry entry = this.getEntries().getFirst();

        if (!entry.hasMapFile()) {
            throw new IllegalStateException("The map entry does not have a map file");
        }

        this.activeMap = this.fileHandler.load(entry.getMapFile(), BaseMap.class)
                .orElseThrow(() -> new IllegalStateException("Failed to load map file: " + entry.getMapFile()));

        this.activeInstance = MinecraftServer.getInstanceManager().createInstanceContainer();
        this.registerInstance(this.activeInstance, entry);
    }

    public void saveMap(@NotNull Path path, @NotNull BaseMap baseMap) {
        this.fileHandler.save(path, baseMap);
    }
}
