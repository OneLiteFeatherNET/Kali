package net.theevilreaper.dungeon.listener;

import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.inventory.region.RegionInventory;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.util.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
@SuppressWarnings("java:S3252")
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

        if (item.isAir() || !item.hasTag(Tags.ITEM_TAGS)) return;

        byte tagValue = item.getTag(Tags.ITEM_TAGS);

        if (tagValue == Items.REGION_ITEM && instance.setSecondPos(event.getBlockPosition())) {
            regionInventory.open(event.getPlayer());
        }
    }
}
