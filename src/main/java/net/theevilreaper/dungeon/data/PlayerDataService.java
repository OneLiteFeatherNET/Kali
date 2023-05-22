package net.theevilreaper.dungeon.data;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since
 **/

public final class PlayerDataService {

    private final Map<UUID, PlayerData> dataMap;

    public PlayerDataService() {
        this.dataMap = new HashMap<>();
    }

    public void remove(@NotNull UUID uuid) {
        this.dataMap.remove(uuid);
    }

    public @NotNull PlayerData get(@NotNull Player player) {
        return this.dataMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }


}
