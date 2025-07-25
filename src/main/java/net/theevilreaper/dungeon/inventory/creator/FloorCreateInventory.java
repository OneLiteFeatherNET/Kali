package net.theevilreaper.dungeon.inventory.creator;

import net.minestom.server.inventory.click.Click;
import net.theevilreaper.aves.inventory.InventoryLayout;
import net.theevilreaper.aves.inventory.PersonalInventoryBuilder;
import net.theevilreaper.aves.inventory.click.ClickHolder;
import net.theevilreaper.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.theevilreaper.dungeon.data.floor.Floor;
import net.theevilreaper.dungeon.data.floor.FloorDTO;
import net.theevilreaper.dungeon.data.floor.FloorMetaDataSetter;
import net.theevilreaper.dungeon.event.FloorCreateEvent;
import net.theevilreaper.dungeon.util.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
@SuppressWarnings("java:S3252")
public class FloorCreateInventory implements FloorMetaDataSetter {

    private static final Tag<Integer> CLOSE = Tag.Integer("close");
    private static final Component INV_TITLE = Component.text("Create floor");
    private static final Component SETUP_TITLE = Component.text("Enter a value");
    private static final ItemStack CHANGE_NAME = ItemStack.builder(Material.OAK_SIGN)
            .customName(Component.text("Change name")).build();
    private static final ItemStack CHANGE_EXTERNAL = ItemStack.builder(Material.CRIMSON_SIGN)
            .customName(Component.text("Change external name")).build();
    private static final ItemStack CHANGE_ID = ItemStack.builder(Material.CLOCK)
            .customName(Component.text("Change id")).build();
    private static final ItemStack CHANGE_ICON = ItemStack.builder(Material.LECTERN)
            .customName(Component.text("Change icon")).build();
    private static final ItemStack SAVE = ItemStack.builder(Material.GREEN_STAINED_GLASS_PANE)
            .customName(Component.text("Save", NamedTextColor.GREEN)).build();

    private static final ItemStack NAME_TAG = ItemStack.builder(Material.NAME_TAG).build();
    private final PersonalInventoryBuilder inventory;
    private final PersonalInventoryBuilder inputGui;
    private String name;
    private int clickedSlot;
    private final FloorDTO.Builder builder;

    public FloorCreateInventory(@NotNull Player owningPlayer) {
        this.builder = Floor.builder();
        this.inventory = new PersonalInventoryBuilder(INV_TITLE, InventoryType.CHEST_3_ROW, owningPlayer);

        var layout = InventoryLayout.fromType(this.inventory.getType());

        layout.setItems(LayoutCalculator.quad(0, layout.getContents().length - 1), Items.DECORATION);
        layout.setItem(METADATA_NAME_SLOT, CHANGE_NAME, this::handleCreateClick);
        layout.setItem(METADATA_EXTERNAL_NAME_SLOT, CHANGE_EXTERNAL, this::handleCreateClick);
        layout.setItem(METADATA_ID_SLOT, CHANGE_ID, this::handleCreateClick);
        layout.setItem(METADATA_MATERIAL_SLOT, CHANGE_ICON, this::handleCreateClick);
        layout.setItem(layout.getContents().length - 1, SAVE, this::handleCloseClick);

        this.inventory.setLayout(layout);
        this.inventory.setCloseFunction(event -> {
            var player = event.getPlayer();

            if (player.hasTag(CLOSE)) {
                player.removeTag(CLOSE);
                EventDispatcher.call(new FloorCreateEvent(player, this.builder.build()));
            }
        });

        this.inventory.register();

        this.inputGui = new PersonalInventoryBuilder(SETUP_TITLE, InventoryType.ANVIL, owningPlayer);
        var createLayout = InventoryLayout.fromType(this.inputGui.getType());
        createLayout.setItem(0, NAME_TAG);
        createLayout.setItem(2, NAME_TAG, this::handleInputClick);
        this.inputGui.setLayout(createLayout);
        this.inputGui.setCloseFunction(event -> {
            if (this.clickedSlot == METADATA_NAME_SLOT || this.clickedSlot == METADATA_EXTERNAL_NAME_SLOT) {
                this.setName(name, builder, owningPlayer, layout, this.clickedSlot == METADATA_EXTERNAL_NAME_SLOT);
            } else if (this.clickedSlot == METADATA_ID_SLOT) {
                this.setFloorId(name, builder, owningPlayer, layout);
            } else {
                this.setMaterial(name, builder, owningPlayer, layout);
            }
            inventory.invalidateLayout();
            inventory.open();
        });
        this.inputGui.register();
    }

    private void handleInputClick(@NotNull Player player, int slot, @NotNull Click click, @NotNull ItemStack stack, @NotNull Consumer<ClickHolder> result) {
        result.accept(ClickHolder.cancelClick());
        player.closeInventory();
        MinecraftServer.getGlobalEventHandler().call(new InventoryCloseEvent(this.inputGui.getInventory(), player, false));
    }

    private void handleCreateClick(@NotNull Player player, int slot, @NotNull Click click, @NotNull ItemStack stack, @NotNull Consumer<ClickHolder> result) {
        result.accept(ClickHolder.cancelClick());
        this.clickedSlot = slot;
        player.closeInventory();
        this.inputGui.open();
    }

    private void handleCloseClick(@NotNull Player player, int slot, @NotNull Click click, @NotNull ItemStack stack, @NotNull Consumer<ClickHolder> result) {
        player.setTag(CLOSE, 1);
        result.accept(ClickHolder.cancelClick());
        player.closeInventory();
        MinecraftServer.getGlobalEventHandler().call(new InventoryCloseEvent(this.inventory.getInventory(), player, false));
    }

    public void unregister() {
        this.inventory.unregister();
        this.inputGui.unregister();
    }

    public void updateName(@NotNull String name) {
        this.name = name;
    }

    public void open() {
        this.inventory.open();
    }
}
