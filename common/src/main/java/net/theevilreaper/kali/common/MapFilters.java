package net.theevilreaper.kali.common;

import net.theevilreaper.aves.map.MapEntry;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * The {@link MapFilters} class contains some filter method to filter maps for different conditions.
 * The game module needs another filter to logic than the setup.
 *
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
public final class MapFilters {

    public static final Path ORIGIN_FOLDER = Paths.get("");
    private static final String REGION_FOLDER = "region";
    private static final String MAP_FILE_NAME = "map.json";
    private static final String LOBBY_SUFFIX = "lobby"; // Constant for lobby suffix

    private MapFilters() {

    }

    /**
     * Checks if the given map is a lobby map.
     *
     * @param mapEntry the map entry to check
     * @return true if the map is a lobby map
     */
    public static boolean isLobbyMap(@NotNull MapEntry mapEntry) {
        return mapEntry.getDirectoryRoot().endsWith(LOBBY_SUFFIX);
    }

    /**
     * Filters through the given stream of paths and returns a list of maps which are available for the game.
     *
     * @param mapStream a stream of paths
     * @return a list which contains different maps which are available for the game
     */
    public static @NotNull List<MapEntry> filterMapsForGame(@NotNull Stream<Path> mapStream) {
        return mapStream
                .filter(Files::isDirectory)
                .filter(path -> Files.exists(path.resolve(REGION_FOLDER)))
                .filter(path -> Files.exists(path.resolve(MAP_FILE_NAME)))
                .map(MapEntry::of)
                .toList();
    }

    /**
     * Filters through the given stream of paths and returns a list of maps which are available for the setup.
     *
     * @param mapStream a stream of paths
     * @return a list which contains different maps which are available for the setup
     */
    public static @NotNull List<MapEntry> filterMapsForSetup(@NotNull Stream<Path> mapStream) {
        return mapStream
                .filter(Files::isDirectory)
                .filter(path -> Files.exists(path.resolve(REGION_FOLDER)))
                .map(MapEntry::of)
                .toList();
    }
}
