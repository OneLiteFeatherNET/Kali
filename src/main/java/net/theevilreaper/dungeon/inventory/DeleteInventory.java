package net.theevilreaper.dungeon.inventory;

import de.icevizion.aves.inventory.GlobalInventoryBuilder;
import de.icevizion.aves.inventory.InventoryLayout;
import de.icevizion.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.theevilreaper.dungeon.data.floor.FloorProvider;
import net.theevilreaper.dungeon.event.FloorRemoveEvent;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.util.Messages;
import net.theevilreaper.dungeon.util.Tags;
import org.jetbrains.annotations.NotNull;

/**
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
    private final FloorProvider floorProvider;

    public DeleteInventory(@NotNull FloorProvider floorProvider) {
        this.floorProvider = floorProvider;
        this.builder = new GlobalInventoryBuilder(DELETE_TITLE, InventoryType.CHEST_3_ROW);
        var layout = new InventoryLayout(this.builder.getType());
        layout.setNonClickItems(LayoutCalculator.quad(0, layout.getContents().length - 1), Items.DECORATION);

        layout.setItem(12, ItemStack.builder(Material.LIME_DYE).displayName(CONFIRM_COMPONENT), this::handleClick);
        layout.setItem(14, ItemStack.builder(Material.RED_DYE).displayName(ABORT_COMPONENT), this::handleClick);

        this.builder.setLayout(layout);
    }

    private void handleClick(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        var item = result.getClickedItem();

        if (item.isAir()) return;

        if (item.material() == Material.LIME_DYE) {
            var floorAsName = player.getTag(Tags.DELETE_FLOOR);
            var floor = floorProvider.getFloor(floorAsName);

            if (floor == null) {
                player.closeInventory();
                player.removeTag(Tags.DELETE_FLOOR);
                player.sendMessage(Messages.ERROR_FLOOR_DELETE);
                return;
            }

            EventDispatcher.call(new FloorRemoveEvent(player, floor));
        }

        player.closeInventory();
        player.removeTag(Tags.DELETE_FLOOR);
    }

    public void openInventory(@NotNull Player player, @NotNull String floor) {
        player.setTag(Tags.DELETE_FLOOR, floor);
        player.openInventory(builder.getInventory());
    }
}
