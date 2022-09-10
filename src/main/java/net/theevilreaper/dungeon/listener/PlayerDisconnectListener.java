package net.theevilreaper.dungeon.listener;

import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.inventory.creator.FloorCreateService;
import net.theevilreaper.dungeon.sidebar.SidebarViewer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class PlayerDisconnectListener implements Consumer<PlayerDisconnectEvent> {

    private final SidebarViewer sidebarViewer;
    private final FloorCreateService floorCreateService;

    public PlayerDisconnectListener(@NotNull SidebarViewer sidebarViewer, @NotNull FloorCreateService floorCreateService) {
        this.sidebarViewer = sidebarViewer;
        this.floorCreateService = floorCreateService;
    }

    @Override
    public void accept(@NotNull PlayerDisconnectEvent event) {
        var player = event.getPlayer();
        if (event.getInstance() instanceof EditInstance editInstance) {
            editInstance.remove(player);

            if (editInstance.hasSameUUID(player.getUuid())) {
                editInstance.switchOwner();
            }
        }
        this.floorCreateService.remove(player);
        this.sidebarViewer.remove(player);
    }
}
