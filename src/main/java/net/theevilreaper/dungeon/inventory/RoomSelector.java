package net.theevilreaper.dungeon.inventory;

import de.icevizion.aves.inventory.GlobalInventoryBuilder;
import de.icevizion.aves.inventory.InventoryBuilder;
import de.icevizion.aves.inventory.InventoryLayout;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.theevilreaper.dungeon.data.floor.Floor;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.instance.EditInstanceManager;
import net.theevilreaper.dungeon.items.Items;
import net.theevilreaper.dungeon.location.LocationProvider;
import net.theevilreaper.dungeon.util.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("java:S3252")
public class RoomSelector {

    public static final Tag<UUID> UUID_TAG = Tag.UUID("id");
    private static final int START_SLOT = 9;
    private final GlobalInventoryBuilder builder;
    private final EditInstanceManager editInstanceManager;
    private final Floor floor;
    private final Pos defaultPos;
    private final Consumer<EditInstance> editInstanceConsumer;
    private final InventoryBuilder inventoryBuilder;
    private final InventoryBuilder floorBuilder;

    public RoomSelector(@NotNull EditInstanceManager editInstanceManager,
                        @NotNull LocationProvider locationProvider,
                        @NotNull Floor floor,
                        @NotNull InventoryBuilder floorBuilder,
                        @NotNull Consumer<EditInstance> editInstanceConsumer) {
        this.editInstanceManager = editInstanceManager;
        this.editInstanceConsumer = editInstanceConsumer;
        this.floorBuilder = floorBuilder;
        this.defaultPos = locationProvider.getDefaultPos().add(0, 20, 0);
        this.floor = floor;
        this.inventoryBuilder = floorBuilder;
        this.builder = new GlobalInventoryBuilder(Component.text("Select the room to edit"), InventoryType.CHEST_6_ROW);
        var layout = new InventoryLayout(this.builder.getType());

        Items.setDecorationLine(layout, this.builder.getType());
        Items.setDecorationLine(layout, InventoryType.CHEST_1_ROW);

        var closeItem = ItemStack.builder(Material.RED_STAINED_GLASS_PANE).displayName(Component.text("Back", NamedTextColor.RED)).build();
        layout.setItem(layout.getContents().length - 1, closeItem, this::handleClose);
        layout.setItem(12, ItemStack.builder(Material.GRAY_BANNER).build(), this::handleClick);

        this.builder.setCloseFunction(event -> event.setNewInventory(floorBuilder.getInventory()));
        this.builder.setLayout(layout);
        this.builder.register();
    }

    public void updateRoomItems() {
        if (this.floor == null || this.floor.getRooms().isEmpty()) return;

        var layout = this.inventoryBuilder.getLayout();

        if (layout == null) return;

        int counter = START_SLOT;

        for (var roomEntry : this.floor.getRooms().entrySet()) {
            counter++;
        }

        this.inventoryBuilder.invalidateLayout();
    }

    public void open(@NotNull Player player) {
        player.openInventory(this.builder.getInventory());
    }

    /**
     * Handles the internal click logic for each item in the inventory
     */
    private void handleClick(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);

        var clickedItem = result.getClickedItem();

        if (clickedItem.material() == Material.AIR) return;

        if (clickedItem.hasTag(UUID_TAG)) {
            var instance = this.editInstanceManager.get(clickedItem.getTag(UUID_TAG));

            if (instance == null) return;

            if (instance.isLocked()) {
                player.sendMessage(Messages.LOCKED_INSTANCE);
                return;
            }

            player.setInstance(instance);
            return;
        }

        EditInstance editInstance = new EditInstance(editInstanceConsumer);
        editInstance.setOwner(player);

        MinecraftServer.getInstanceManager().registerInstance(editInstance);

        var updatedStack = result.getClickedItem().withTag(UUID_TAG, editInstance.getUniqueId());

        this.editInstanceManager.add(editInstance, updatedStack);

        result.setClickedItem(updatedStack);
        player.closeInventory();
        player.setInstance(editInstance, defaultPos);
    }

    private void handleClose(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
        this.callCloseEvent(player);
        player.closeInventory();
        player.openInventory(floorBuilder.getInventory());
    }

    private void callCloseEvent(@NotNull Player player) {
        MinecraftServer.getGlobalEventHandler().call(
                new InventoryCloseEvent(player.getOpenInventory(), player));
    }
}
