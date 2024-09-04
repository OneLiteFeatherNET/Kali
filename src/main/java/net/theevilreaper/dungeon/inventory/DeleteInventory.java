package net.theevilreaper.dungeon.inventory;

import de.icevizion.aves.inventory.GlobalInventoryBuilder;
import de.icevizion.aves.inventory.InventoryLayout;
import de.icevizion.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.data.floor.FloorGetMethod;
import net.theevilreaper.dungeon.event.FloorRemoveEvent;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.util.Messages;
import net.theevilreaper.dungeon.util.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The inventory is used to ask the player if he really wants to delete the floor or not.
 * If there is a confirmation to delete the floor. So this is deleted from the inventory and in the database.
 * The region files themselves will not be deleted. If the deletion is denied nothing further happens
 *
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
@SuppressWarnings("java:S3252")
public class DeleteInventory {

    private static final Component DELETE_TITLE = Component.text("Confirm deletion");
    private static final Component CONFIRM_COMPONENT = Component.text("Confirm", NamedTextColor.GREEN);
    private static final Component ABORT_COMPONENT = Component.text("Abort", NamedTextColor.RED);
    private final GlobalInventoryBuilder builder;
    private final FloorGetMethod floorGetMethod;

    /**
     * Creates a new instance from the delete inventory.
     *
     * @param floorGetMethod the method reference to get a floor by its name
     */
    public DeleteInventory(@NotNull FloorGetMethod floorGetMethod, @NotNull Inventory reOpenInventory) {
        this.floorGetMethod = floorGetMethod;
        this.builder = new GlobalInventoryBuilder(DELETE_TITLE, InventoryType.CHEST_3_ROW);
        var layout = InventoryLayout.fromType(this.builder.getType());
        layout.setNonClickItems(LayoutCalculator.quad(0, layout.getContents().length - 1), Items.DECORATION);

        layout.setItem(12, ItemStack.builder(Material.LIME_DYE).displayName(CONFIRM_COMPONENT), this::handleClick);
        layout.setItem(14, ItemStack.builder(Material.RED_DYE).displayName(ABORT_COMPONENT), this::handleAbortClick);

        this.builder.setLayout(layout);

        this.builder.setCloseFunction(event -> event.getEntity().openInventory(reOpenInventory));
        this.builder.register();
    }

    /**
     * Handles what happened when a player abort the deletion of a floor.
     *
     * @param player    the player who is involved
     * @param clickType the clickType as {@link ClickType} reference
     * @param slotID    the involved slot
     * @param result    the result
     */
    private void handleAbortClick(@NotNull Player player, int slotID, @NotNull ClickType clickType, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        MinecraftServer.getGlobalEventHandler().call(new InventoryCloseEvent(player.getOpenInventory(), player));
        player.closeInventory();
        player.removeTag(Tags.FLOOR_ID);
    }

    /**
     * Handles the click logic to delete a floor.
     *
     * @param player    the player who is involved
     * @param clickType the clickType as {@link ClickType} reference
     * @param slotID    the involved slot
     * @param result    the result
     */
    private void handleClick(@NotNull Player player, int slotID, @NotNull ClickType clickType, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        var item = result.getClickedItem();

        if (item.isAir()) return;

        var event = new InventoryCloseEvent(player.getOpenInventory(), player);

        if (item.material() == Material.LIME_DYE) {
            var floorAsName = player.getTag(Tags.FLOOR_ID);
            var floor = floorGetMethod.getFloorById(floorAsName);

            if (floor == null) {
                MinecraftServer.getGlobalEventHandler().call(event);
                player.closeInventory();
                player.removeTag(Tags.FLOOR_ID);
                player.sendMessage(Messages.ERROR_FLOOR_DELETE);
                return;
            }

            EventDispatcher.call(new FloorRemoveEvent(player, floor));
        }
        MinecraftServer.getGlobalEventHandler().call(event);
        player.closeInventory();
        player.removeTag(Tags.FLOOR_ID);
    }

    /**
     * Opens the inventory to delete the given floor.
     *
     * @param player the player who should get the inventory
     * @param floor  the floor which is involved
     */
    public void openInventory(@NotNull Player player, @NotNull UUID floor) {
        player.setTag(Tags.FLOOR_ID, floor);
        player.openInventory(builder.getInventory());
    }
}
