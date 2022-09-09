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
import net.theevilreaper.dungeon.items.Items;
import org.jetbrains.annotations.NotNull;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class DeleteInventory {

    private final Tag<String> deleteFloor = Tag.String("deleteFloor");

    private final GlobalInventoryBuilder builder;

    private final FloorProvider floorProvider;

    public DeleteInventory(@NotNull FloorProvider floorProvider) {
        this.floorProvider = floorProvider;
        var layout = new InventoryLayout(InventoryType.CHEST_3_ROW);
        layout.setNonClickItems(LayoutCalculator.quad(0, InventoryType.CHEST_3_ROW.getSize() - 1), Items.DECORATION);

        layout.setItem(12, ItemStack.builder(Material.LIME_DYE).displayName(Component.text("Confirm", NamedTextColor.GREEN)), this::handleClick);
        layout.setItem(14, ItemStack.builder(Material.RED_DYE).displayName(Component.text("Abort", NamedTextColor.RED)), this::handleClick);

        this.builder = new GlobalInventoryBuilder(Component.text("Confirm deletion"), InventoryType.CHEST_3_ROW);
        this.builder.setLayout(layout);
    }

    private void handleClick(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);

        var item = result.getClickedItem();

        if (item.isAir() || item.material() == Items.DECORATION.material()) return;

        if (item.material() == Material.LIME_DYE) {
            var floorAsName = player.getTag(deleteFloor);

            player.sendMessage(floorAsName);

            var floor = floorProvider.getFloor(floorAsName);

            if (floor == null) {

                player.closeInventory();
                player.removeTag(deleteFloor);
                player.sendMessage("Something is wrong");
                return;
            }

            EventDispatcher.call(new FloorRemoveEvent(player, floor));
        }

        player.closeInventory();
        player.removeTag(deleteFloor);
    }

    public void openInventory(@NotNull Player player, @NotNull String floor) {
        player.setTag(deleteFloor, floor);
        player.openInventory(builder.getInventory());
    }
}
