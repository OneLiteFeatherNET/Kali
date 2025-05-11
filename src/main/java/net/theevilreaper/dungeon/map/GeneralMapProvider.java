package net.theevilreaper.dungeon.map;

import net.theevilreaper.aves.file.FileHandler;
import net.theevilreaper.aves.file.GsonFileHandler;
import net.theevilreaper.aves.map.AbstractMapProvider;
import net.theevilreaper.aves.map.BaseMap;
import net.theevilreaper.aves.util.functional.PlayerConsumer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.utils.validate.Check;
import net.theevilreaper.dungeon.util.MapFilters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public final class GeneralMapProvider extends AbstractMapProvider {

    private final InstanceContainer mainInstance;
    private final BaseMap baseMap;

    public GeneralMapProvider(@NotNull Path rootPath, @NotNull FileHandler fileHandler) {
        super(fileHandler, MapFilters::filterMapsForSetup);
        Check.argCondition((!(fileHandler instanceof GsonFileHandler)), "FileHandler must be an instance of GsonFileHandler");
        this.mainInstance = MinecraftServer.getInstanceManager().createInstanceContainer();

        Path lobbyMap = rootPath.resolve("maps").resolve("lobby");

        if (!Files.exists(lobbyMap)) {
            throw new IllegalArgumentException("The lobby map must not be null");
        }


        this.baseMap = fileHandler.load(lobbyMap, BaseMap.class).orElseThrow();
        this.mainInstance.setChunkLoader(new AnvilLoader(lobbyMap));
        this.mainInstance.enableAutoChunkLoad(true);
        this.mainInstance.setTimeRate(0);

        MinecraftServer.getInstanceManager().registerInstance(this.mainInstance);
    }

    @Override
    public void saveMap(@NotNull Path path, @NotNull BaseMap baseMap) {
        throw new UnsupportedOperationException("Not supported by this implementation");
    }

    @Override
    public void teleportToSpawn(@NotNull Player player, boolean instanceSet) {
        // TODO: update pos
        Pos spawnPos = this.baseMap.getSpawnOrDefault(Pos.ZERO);
        if (instanceSet) {
            player.setInstance(this.mainInstance, spawnPos);
        } else {
            player.teleport(spawnPos);
        }
    }

    @Override
    public @NotNull Supplier<@Nullable Instance> getActiveInstance() {
        return () -> this.mainInstance;
    }
}
