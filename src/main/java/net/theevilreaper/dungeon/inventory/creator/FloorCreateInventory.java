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
import net.theevilreaper.dungeon.data.floor.FloorDTO;
import net.theevilreaper.dungeon.event.FloorCreateEvent;
import net.theevilreaper.dungeon.util.Items;
import org.jetbrains.annotations.NotNull;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
@SuppressWarnings("java:S3252")
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
    private final PersonalInventoryBuilder inventory;
    private final PersonalInventoryBuilder inputGui;
    private String name;
    private int clickedSlot;

    private FloorDTO.Builder builder;

    public FloorCreateInventory(@NotNull Player owningPlayer) {
        this.builder = Floor.builder();
        this.inventory = new PersonalInventoryBuilder(INV_TITLE, InventoryType.CHEST_3_ROW, owningPlayer);

        var layout = new InventoryLayout(this.inventory.getType());

        layout.setNonClickItems(LayoutCalculator.quad(0, layout.getContents().length - 1), Items.DECORATION);
        layout.setItem(10, CHANGE_NAME, this::handleCreateClick);
        layout.setItem(12, CHANGE_EXTERNAL, this::handleCreateClick);
        layout.setItem(14, CHANGE_ID, this::handleCreateClick);
        layout.setItem(16, CHANGE_ICON, this::handleCreateClick);
        layout.setItem(layout.getContents().length - 1, SAVE, (player, clickType, slotID, result) -> {
            player.setTag(CLOSE, 1);
            result.setCancel(true);
            player.closeInventory();
            MinecraftServer.getGlobalEventHandler().call(new InventoryCloseEvent(this.inventory.getInventory(), player));
        });

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
                case 10 -> {
                    this.builder.setName(name);
                    var slot = layout.getSlot(10);
                    layout.update(10, update(slot.getItem(), name), slot.getClick());
                }
                case 12 -> {
                    this.builder.setExternalName(name);
                    var slot = layout.getSlot(12);
                    layout.update(12, update(slot.getItem(), name), slot.getClick());
                }
                case 14 -> {
                    try {
                        var id = Integer.parseInt(name);
                        this.builder.setId(id);
                        var slot = layout.getSlot(14);
                        layout.update(14, update(slot.getItem(), name), slot.getClick());
                    } catch (NumberFormatException exception) {
                        owningPlayer.sendMessage("NO NUMBER");
                    }
                }
                case 16 -> {
                    var material = Material.fromNamespaceId("minecraft:" + name);

                    if (material == null) material = Material.STONE;

                    this.builder.setMaterial(material);
                    var slot = layout.getSlot(16);
                    layout.update(16, update(slot.getItem(), name), slot.getClick());
                }
            }
            inventory.invalidateLayout();
            inventory.open();
        });
        this.inputGui.register();
    }

    private @NotNull ItemStack update(@NotNull ItemStack itemStack, @NotNull String input) {
        var newItem = ItemStack.builder(itemStack.material()).displayName(itemStack.getDisplayName());
        newItem.lore(Component.empty(), Component.text("Value: " + input));
        return newItem.build();
    }

    private void handleCreateClick(@NotNull Player player, @NotNull ClickType clickType, int slot, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        this.clickedSlot = slot;
        player.closeInventory();
        this.inputGui.open();
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
