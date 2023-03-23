package net.theevilreaper.dungeon.inventory.region.search;

import de.icevizion.aves.inventory.GlobalInventoryBuilder;
import de.icevizion.aves.inventory.InventoryLayout;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import org.jetbrains.annotations.NotNull;

/**
 * The inventory allows a player to apply a filter to the {@link net.theevilreaper.dungeon.inventory.RoomSelector} inventory.
 * By selecting a filter, the rooms that are displayed are adjusted.
 * Then only the rooms that match the corresponding filter are displayed.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class SearchInventory {

    private final GlobalInventoryBuilder builder;

    /**
     * Creates a new instance from the {@link SearchInventory}.
     */
    public SearchInventory() {
        this.builder = new GlobalInventoryBuilder(Component.text("Change filter"), InventoryType.CHEST_1_ROW);
        var layout = new InventoryLayout(this.builder.getType());

        var values = SearchOption.getValues();

        for (int i = 0; i < values.length; i++) {
            layout.setItem(i, values[i].getItem(), this::handleClick);
        }

        this.builder.setLayout(layout);
        this.builder.register();
    }

    /**
     * Opens the underlying inventory for a given player
     * @param player the player who should receive the inventory
     */
    public void open(@NotNull Player player) {
        player.openInventory(this.builder.getInventory());
    }

    /**
     * Handles the click logic for the items in the inventory.
     * @param player the player who clicked
     * @param clickType the given {@link ClickType}
     * @param slotID the involved slot as id
     * @param result the {@link InventoryConditionResult}
     */
    private void handleClick(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        player.closeInventory();
        MinecraftServer.getGlobalEventHandler().call(new PlayerSearchChangeEvent(player, slotID));
    }
}
