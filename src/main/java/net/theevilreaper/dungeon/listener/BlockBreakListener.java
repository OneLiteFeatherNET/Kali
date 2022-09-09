package net.theevilreaper.dungeon.listener;

import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.inventory.RegionInventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class BlockBreakListener implements Consumer<PlayerBlockBreakEvent> {

    private final RegionInventory regionInventory;

    public BlockBreakListener(@NotNull RegionInventory regionInventory) {
        this.regionInventory = regionInventory;
    }

    @Override
    public void accept(@NotNull PlayerBlockBreakEvent event) {
        var player = event.getPlayer();

        if (!(player.getInstance() instanceof EditInstance editInstance)) {
            event.setCancelled(true);
            return;
        }

        if (player.getItemInMainHand().material() == Material.GOLDEN_AXE) {
            event.setCancelled(true);

            if (editInstance.setFirstPos(event.getBlockPosition())) {
                this.regionInventory.open(player);
            }
        }
    }
}
