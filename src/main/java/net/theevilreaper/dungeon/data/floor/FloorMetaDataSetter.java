package net.theevilreaper.dungeon.data.floor;

import de.icevizion.aves.inventory.InventoryLayout;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.util.Messages;
import org.jetbrains.annotations.NotNull;

import static net.theevilreaper.dungeon.util.Messages.buildPrefixedComponent;

@SuppressWarnings("java:S3252")
public interface FloorMetaDataSetter {

    int METADATA_NAME_SLOT = 10;
    int METADATA_EXTERNAL_NAME_SLOT = 12;
    int METADATA_ID_SLOT = 14;
    int METADATA_MATERIAL_SLOT = 16;

    default void setName(@NotNull String input,
                         @NotNull Floor.Builder builder,
                         @NotNull Player player,
                         @NotNull InventoryLayout layout,
                         boolean externalName) {
        var slot = layout.getSlot(externalName ? METADATA_EXTERNAL_NAME_SLOT : METADATA_NAME_SLOT);
        if (externalName) {
            builder.setExternalName(input);
            layout.update(METADATA_EXTERNAL_NAME_SLOT, update(slot.getItem(), input), slot.getClick());
            player.sendMessage(buildPrefixedComponent(Component.text("The external name is set to: ")
                    .append(Component.text(input, NamedTextColor.GOLD))));
            return;
        }

        builder.setName(input);
        layout.update(METADATA_NAME_SLOT, update(slot.getItem(), input), slot.getClick());
        player.sendMessage(buildPrefixedComponent(Component.text("The name is set to: ")
                .append(Component.text(input, NamedTextColor.GOLD))));
    }

    default void setFloorId(@NotNull String input, @NotNull Floor.Builder builder, @NotNull Player player, @NotNull InventoryLayout layout) {
        try {
            int id = Integer.parseInt(input);
            builder.setFloorID(id);
            var slot = layout.getSlot(METADATA_ID_SLOT);
            layout.update(METADATA_ID_SLOT, update(slot.getItem(), input), slot.getClick());
        } catch (NumberFormatException exception) {
            player.sendMessage(Messages.NO_NUMBER);
        }
    }

    default void setMaterial(@NotNull String input, @NotNull Floor.Builder builder, @NotNull Player player, @NotNull InventoryLayout layout) {
        var material = Material.fromNamespaceId("minecraft:" + input);

        if (material == null) {
            player.sendMessage(buildPrefixedComponent(Component.text(input, NamedTextColor.RED)
                    .append(Component.text(" is not a valid material", NamedTextColor.GRAY))));
            material = Material.STONE;
        }

        builder.setMaterial(material);
        var slot = layout.getSlot(METADATA_MATERIAL_SLOT);
        layout.update(METADATA_MATERIAL_SLOT, update(slot.getItem(), input), slot.getClick());
    }

    /**
     * Updates the lore from a given {@link ItemStack}.
     * @param itemStack the item to update
     * @param input the data for the lore
     * @return the updated item
     */
    private @NotNull ItemStack update(@NotNull ItemStack itemStack, @NotNull String input) {
        var newItem = ItemStack.builder(itemStack.material()).customName(itemStack.get(ItemComponent.CUSTOM_NAME));
        newItem.lore(Component.empty(), Component.text("Value: " + input));
        return newItem.build();
    }
}
