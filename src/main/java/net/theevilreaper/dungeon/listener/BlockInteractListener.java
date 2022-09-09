package net.theevilreaper.dungeon.listener;

import net.minestom.server.event.player.PlayerBlockInteractEvent;
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
public class BlockInteractListener implements Consumer<PlayerBlockInteractEvent> {

    private final RegionInventory regionInventory;

    public BlockInteractListener(@NotNull RegionInventory regionInventory) {
        this.regionInventory = regionInventory;
    }

    @Override
    public void accept(@NotNull PlayerBlockInteractEvent event) {
        event.setCancelled(true);
        if (!(event.getPlayer().getInstance() instanceof EditInstance instance)) return;

        var item = event.getPlayer().getItemInMainHand();

        if (item.isAir()) return;

        if (item.material() == Material.GOLDEN_AXE && instance.setSecondPos(event.getBlockPosition())) {
            regionInventory.open(event.getPlayer());
        }
    }
}
