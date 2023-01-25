package net.theevilreaper.dungeon.items;

import de.icevizion.aves.inventory.InventoryLayout;
import de.icevizion.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.validate.Check;
import net.theevilreaper.dungeon.util.Tags;
import org.jetbrains.annotations.NotNull;

/**
 * The class includes all items for the dungeon editor.
 * @author theEvilReaper
 * @since 1.0.0
 * @version 1.0.0
 */
@SuppressWarnings("java:S3252")
public class Items {

    public static final ItemStack DECORATION = ItemStack.builder(Material.GRAY_STAINED_GLASS_PANE)
            .displayName(Component.empty()).build();
    private static final byte DEFAULT_HELD_SLOT = (byte)4;
    private final ItemStack regionTool;
    private final ItemStack floorSelector;

    public Items() {
        this.regionTool = ItemStack.builder(Material.GOLDEN_AXE)
                .displayName(Component.text("Regions", NamedTextColor.YELLOW))
                .meta(builder -> {
                    builder.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES).set(Tags.ITEM_TAGS, (byte)0);
                }).build();
        this.floorSelector = ItemStack.builder(Material.CARTOGRAPHY_TABLE)
                .displayName(Component.text("Floors", NamedTextColor.RED))
                .meta(builder -> builder.setTag(Tags.ITEM_TAGS, (byte)1))
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
        Check.argCondition(type.getSize() != layout.getSize(), "The given type size is higher then the size from the inventory");
        layout.setNonClickItems(LayoutCalculator.fillRow(type), DECORATION);
    }
}
