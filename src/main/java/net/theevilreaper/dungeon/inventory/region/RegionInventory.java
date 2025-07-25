package net.theevilreaper.dungeon.inventory.region;

import net.minestom.server.inventory.click.Click;
import net.theevilreaper.aves.inventory.GlobalInventoryBuilder;
import net.theevilreaper.aves.inventory.InventoryLayout;
import net.theevilreaper.aves.inventory.click.ClickHolder;
import net.theevilreaper.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.util.Items;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RegionInventory {

    private final GlobalInventoryBuilder builder;

    public RegionInventory() {
        this.builder = new GlobalInventoryBuilder(Component.text("Create a region").color(TextColor.fromHexString("#E38421")), InventoryType.CHEST_6_ROW);
        var layout = InventoryLayout.fromType(builder.getType());

        layout.setItems(LayoutCalculator.frame(0, layout.getContents().length - 1), Items.DECORATION);

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
        return ItemStack.builder(material).customName(displayName)
                //.set(ItemComponent.ENCHANTMENT_GLINT_OVERRIDE, false)
                .build();
    }

    /**
     * Defines the click listener for the inventory items.
     */
    private void handleBlockClick(@NotNull Player player, int slot, @NotNull Click clickType, @NotNull ItemStack stack, @NotNull Consumer<ClickHolder> result) {
        result.accept(ClickHolder.cancelClick());
        if (!(player.getInstance() instanceof EditInstance editInstance)) {
            return;
        }

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
