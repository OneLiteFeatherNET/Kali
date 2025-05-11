package net.theevilreaper.dungeon.util;

import net.theevilreaper.aves.inventory.InventoryLayout;
import net.theevilreaper.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * The class includes all items for the dungeon editor.
 * @author theEvilReaper
 * @since 1.0.0
 * @version 1.0.0
 */
@SuppressWarnings("java:S3252")
public class Items {

    public static final byte REGION_ITEM = 0;
    public static final byte FLOOR_ITEM = 1;
    public static final ItemStack DECORATION = ItemStack.builder(Material.GRAY_STAINED_GLASS_PANE)
            .customName(Component.empty()).build();
    public static final ItemStack BACK_SLOT = ItemStack.builder(Material.RED_STAINED_GLASS_PANE)
            .customName(Component.text("Back", NamedTextColor.RED)).build();
    private static final byte DEFAULT_HELD_SLOT = (byte)4;
    private final ItemStack regionTool;
    private final ItemStack floorSelector;

    public Items() {
        this.regionTool = ItemStack.builder(Material.GOLDEN_AXE)
                .customName(Component.text("Regions", NamedTextColor.YELLOW))
                .set(ItemComponent.HIDE_TOOLTIP)
                .set(Tags.ITEM_TAGS, REGION_ITEM)
                .build();
        this.floorSelector = ItemStack.builder(Material.CARTOGRAPHY_TABLE)
                .customName(Component.text("Floors", NamedTextColor.RED))
                .set(Tags.ITEM_TAGS, FLOOR_ITEM)
                .build();
    }

    /**
     * Set's the floor select item into the inventory of the given player.
     * @param player The player to set the item into the inventory
     */
    public void setFloorItem(@NotNull Player player) {
        player.getInventory().clear();
        player.getInventory().setItemStack(4, this.floorSelector);
    }

    /**
     * Set's the edit items for the player.
     * @param player The player who should receive the items
     */
    public void setEditItems(@NotNull Player player) {
        player.getInventory().clear();
        player.getInventory().setItemStack(0, this.regionTool);
        player.setHeldItemSlot(DEFAULT_HELD_SLOT);
    }

    /**
     * Adds the region too to the inventory.
     * @param player The player who should get the region tool
     */
    public void addRegionTool(@NotNull Player player) {
        player.getInventory().setItemStack(0, regionTool);
    }

    /**
     * Fills the given inventory layout with the inventory item.
     * @param layout The layout to set the items into it
     * @param type The given type from the inventory
     */
    public static void setDecorationLine(@NotNull InventoryLayout layout, @NotNull InventoryType type) {
        //Check.argCondition(type.getSize() != layout.getSize(), "The given type size is higher then the size from the inventory");
        layout.setItems(LayoutCalculator.fillRow(type), DECORATION);
    }
}
