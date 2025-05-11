package net.theevilreaper.dungeon.listener;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.theevilreaper.aves.util.functional.PlayerConsumer;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.sidebar.SidebarViewer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class PlayerSpawnListener implements Consumer<PlayerSpawnEvent> {

    private final PlayerConsumer teleportToSpawn;
    private final SidebarViewer sidebarViewer;
    private final Items items;

    public PlayerSpawnListener(@NotNull PlayerConsumer teleportToSpawn, @NotNull SidebarViewer sidebarViewer, @NotNull Items items) {
        this.teleportToSpawn = teleportToSpawn;
        this.sidebarViewer = sidebarViewer;
        this.items = items;
    }

    @Override
    public void accept(@NotNull PlayerSpawnEvent event) {
        var player = event.getPlayer();

        if (player.getInstance() instanceof EditInstance) return;

        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> this.items.setFloorItem(player));
        player.setAllowFlying(true);
        player.setFlying(true);

        this.sidebarViewer.add(player);
        this.teleportToSpawn.accept(player);
    }
}
