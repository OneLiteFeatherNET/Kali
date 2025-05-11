package net.theevilreaper.dungeon.listener;

import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.Material;
import net.theevilreaper.aves.util.functional.PlayerConsumer;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.inventory.floor.FloorInventory;
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
public class ItemListener implements Consumer<PlayerUseItemEvent> {

    private final FloorInventory floorInventory;
    private final PlayerConsumer teleportToSpawn;

    public ItemListener(
            @NotNull FloorInventory floorInventory,
            @NotNull PlayerConsumer teleportToSpawn
    ) {
        this.floorInventory = floorInventory;
        this.teleportToSpawn = teleportToSpawn;
    }

    @Override
    public void accept(@NotNull PlayerUseItemEvent event) {
        var item = event.getItemStack();

        if (item.isAir() || !item.hasTag(Tags.ITEM_TAGS)) return;
        var player = event.getPlayer();

        byte tagValue = item.getTag(Tags.ITEM_TAGS);
        if (tagValue == Items.FLOOR_ITEM) {
            this.floorInventory.open(player);
            return;
        }

        if (player.getInstance() instanceof EditInstance && item.material() == Material.BARRIER) {
            player.getInventory().clear();
            this.teleportToSpawn.accept(player);
        }
    }
}
