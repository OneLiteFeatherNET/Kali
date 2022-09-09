package net.theevilreaper.dungeon.inventory;

import de.icevizion.aves.inventory.GlobalInventoryBuilder;
import de.icevizion.aves.inventory.InventoryLayout;
import de.icevizion.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.items.Items;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RegionInventory {

    private final GlobalInventoryBuilder builder;

    public RegionInventory() {
        this.builder = new GlobalInventoryBuilder(Component.text("Create a region").color(TextColor.fromHexString("#E38421")), InventoryType.CHEST_6_ROW);

        var layout = new InventoryLayout(builder.getType());

        layout.setNonClickItems(LayoutCalculator.frame(0, layout.getContents().length - 1), Items.DECORATION);

        int[] slots = LayoutCalculator.quad(10, 43);

        layout.blank(slots);

        this.builder.setLayout(layout);
        this.builder.register();
    }

    /**
     * Builds an {@link ItemStack} with the given material and name.
     *
     * @param material    The material for the stack
     * @param displayName The {@link Component} which contains the name of the stack
     * @return the created {@link ItemStack}
     */
    @Contract("_, _ -> new")
    private @NotNull ItemStack getStack(@NotNull Material material, @NotNull Component displayName) {
        return ItemStack.builder(material).displayName(displayName).meta(builder1 -> builder1.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES)).build();
    }

    /**
     * Defines the click listener for the inventory items.
     */
    private void handleClick(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        if (!(player.getInstance() instanceof EditInstance editInstance)) {
            return;
        }

        editInstance.setRegionType();
        player.closeInventory();
    }

    /**
     * Opens the region inventory for the given player.
     *
     * @param player the player to open the inventory for it
     */
    public void open(@NotNull Player player) {
        player.openInventory(this.builder.getInventory());
    }
}
