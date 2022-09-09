package net.theevilreaper.dungeon.inventory.creator;

import de.icevizion.aves.inventory.InventoryLayout;
import de.icevizion.aves.inventory.PersonalInventoryBuilder;
import de.icevizion.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.theevilreaper.dungeon.data.floor.Floor;
import net.theevilreaper.dungeon.data.floor.FloorProvider;
import net.theevilreaper.dungeon.event.FloorCreateEvent;
import net.theevilreaper.dungeon.items.Items;
import org.jetbrains.annotations.NotNull;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class FloorCreateInventory {

    private static final Tag<Integer> CLOSE = Tag.Integer("close");

    private static final Component INV_TITLE = Component.text("Create floor");
    private static final Component SETUP_TITLE = Component.text("Enter a value");

    private static final ItemStack CHANGE_NAME = ItemStack.builder(Material.OAK_SIGN)
            .displayName(Component.text("Change name")).build();
    private static final ItemStack CHANGE_EXTERNAL = ItemStack.builder(Material.CRIMSON_SIGN)
            .displayName(Component.text("Change external name")).build();
    private static final ItemStack CHANGE_ID = ItemStack.builder(Material.CLOCK)
            .displayName(Component.text("Change id")).build();
    private static final ItemStack CHANGE_ICON = ItemStack.builder(Material.LECTERN)
            .displayName(Component.text("Change icon")).build();

    private static final ItemStack SAVE = ItemStack.builder(Material.GREEN_STAINED_GLASS_PANE)
            .displayName(Component.text("Save", NamedTextColor.GREEN)).build();

    private final PersonalInventoryBuilder createInventory;

    private final PersonalInventoryBuilder inputGui;

    private final Floor floor;

    private String name;

    private int clickedSlot;

    public FloorCreateInventory(@NotNull Player owningPlayer, @NotNull FloorProvider floorProvider) {
        this.floor = new Floor();
        this.createInventory = new PersonalInventoryBuilder(INV_TITLE, InventoryType.CHEST_3_ROW, owningPlayer);

        var layout = new InventoryLayout(this.createInventory.getType());

        layout.setNonClickItems(LayoutCalculator.quad(0, this.createInventory.getType().getSize() - 1), Items.DECORATION);
        layout.setItem(10, CHANGE_NAME, this::handleCreateClick);
        layout.setItem(12, CHANGE_EXTERNAL, this::handleCreateClick);
        layout.setItem(14, CHANGE_ID, this::handleCreateClick);
        layout.setItem(16, CHANGE_ICON, this::handleCreateClick);
        layout.setItem(InventoryType.CHEST_3_ROW.getSize() - 1, SAVE, (player, clickType, slotID, result) -> {
            player.setTag(CLOSE, 1);
            result.setCancel(true);
            player.closeInventory();
            MinecraftServer.getGlobalEventHandler().call(new InventoryCloseEvent(this.createInventory.getInventory(), player));
        });

        this.createInventory.setLayout(layout);
        this.createInventory.setCloseFunction(event -> {
            var player = event.getPlayer();

            if (player.hasTag(CLOSE)) {
                player.removeTag(CLOSE);
                EventDispatcher.call(new FloorCreateEvent(player, floor));
            }
        });

        this.createInventory.register();

        this.inputGui = new PersonalInventoryBuilder(SETUP_TITLE, InventoryType.ANVIL, owningPlayer);
        var createLayout = new InventoryLayout(this.inputGui.getType());
        createLayout.setNonClickItem(0, ItemStack.of(Material.NAME_TAG));
        createLayout.setItem(2, ItemStack.builder(Material.NAME_TAG), (player1, clickType, slotID, condition) -> {
            condition.setCancel(true);
            player1.closeInventory();
            MinecraftServer.getGlobalEventHandler().call(new InventoryCloseEvent(this.inputGui.getInventory(), player1));
        });
        this.inputGui.setLayout(createLayout);
        this.inputGui.setCloseFunction(event -> {
            switch (this.clickedSlot) {
                case 10 -> this.floor.setName(name);
                case 12 -> this.floor.setExternalName(name);
                case 14 -> {
                    try {
                        var id = Integer.parseInt(name);
                        this.floor.setFloorID(id);
                    } catch (NumberFormatException exception) {
                        owningPlayer.sendMessage("NO NUMBER");
                    }
                }
                case 16 -> {
                    var material = Material.fromNamespaceId("minecraft:" + name);

                    if (material == null) material = Material.STONE;

                    this.floor.setMaterial(material);
                }
            }
            createInventory.open();
        });
        this.inputGui.register();
    }

    private void handleCreateClick(@NotNull Player player, @NotNull ClickType clickType, int slot, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        this.clickedSlot = slot;
        player.closeInventory();
        this.inputGui.open();
    }

    public void unregister() {
        this.createInventory.unregister();
        this.inputGui.unregister();
    }

    public void updateName(@NotNull String name) {
        this.name = name;
    }

    public void open() {
        this.createInventory.open();
    }
}
