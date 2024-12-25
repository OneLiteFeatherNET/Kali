package net.theevilreaper.dungeon.inventory.region.search;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.util.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The enum contains all available search options which can be applied by a player.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public enum SearchOption {

    ITEM(ItemStack.builder(Material.STICK)
            .customName(Component.text("Item Room"))
            .set(Tags.ITEM_TAGS, (byte) 10)
            .build()
    ),
    BOSS(ItemStack.builder(Material.DRAGON_HEAD)
            .customName(Component.text("Boss Room"))
            .set(Tags.ITEM_TAGS, (byte) 11)
            .build()
    );

    private static final SearchOption[] VALUES = values();
    private final ItemStack item;

    /**
     * Creates a new entry for a {@link SearchOption}.
     * @param item the item which is used for the {@link SearchInventory}
     */
    SearchOption(@NotNull ItemStack item) {
        this.item = item;
    }

    /**
     * Returns the {@link ItemStack} from the option.
     * @return the given {@link ItemStack}
     */
    public @NotNull ItemStack getItem() {
        return item;
    }

    @Nullable
    public static SearchOption getBySlot(int slot) {
        if (slot > VALUES.length) return null;
        return VALUES[slot];
    }

    /**
     * Returns an array which represents a copy of the .values call to reduce object copies.
     * @return the given array
     */
    public static SearchOption[] getValues() {
        return VALUES;
    }
}
