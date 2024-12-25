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
import net.theevilreaper.dungeon.data.floor.Floor;
import net.theevilreaper.dungeon.data.floor.FloorDTO;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.instance.EditInstanceManager;
import net.theevilreaper.dungeon.inventory.region.search.PlayerSearchChangeEvent;
import net.theevilreaper.dungeon.inventory.region.search.SearchInventory;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.location.LocationProvider;
import net.theevilreaper.dungeon.util.Messages;
import net.theevilreaper.dungeon.util.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static net.theevilreaper.dungeon.util.Items.BACK_SLOT;

@SuppressWarnings("java:S3252")
public class RoomSelector {

    private static final int START_SLOT = 9;
    private final GlobalInventoryBuilder builder;
    private final EditInstanceManager editInstanceManager;
    private final Floor floorDTO;
    private final Pos defaultPos;
    private final Consumer<EditInstance> editInstanceConsumer;
    private final InventoryBuilder inventoryBuilder;
    private final InventoryBuilder floorBuilder;

    public RoomSelector(@NotNull EditInstanceManager editInstanceManager,
                        @NotNull LocationProvider locationProvider,
                        @NotNull Floor floorDTO,
                        @NotNull InventoryBuilder floorBuilder,
                        @NotNull Consumer<EditInstance> editInstanceConsumer) {
        this.editInstanceManager = editInstanceManager;
        this.editInstanceConsumer = editInstanceConsumer;
        this.floorBuilder = floorBuilder;
        this.defaultPos = locationProvider.getDefaultPos().add(0, 20, 0);
        this.floorDTO = floorDTO;
        this.inventoryBuilder = floorBuilder;
        this.builder = new GlobalInventoryBuilder(Component.text("Select the room to edit"), InventoryType.CHEST_6_ROW);
        var layout = InventoryLayout.fromType(this.builder.getType());

        Items.setDecorationLine(layout, this.builder.getType());
        Items.setDecorationLine(layout, InventoryType.CHEST_1_ROW);

        layout.setItem(49, ItemStack.builder(Material.HOPPER).customName(Component.text("Filter", NamedTextColor.YELLOW)).lore(Messages.FILTER_LORE).build(), (player, i, clickType, inventoryConditionResult) -> {
            inventoryConditionResult.setCancel(true);

            if (clickType == ClickType.RIGHT_CLICK) {
                MinecraftServer.getGlobalEventHandler().call(new PlayerSearchChangeEvent(player, null));
                return;
            }

            if (clickType == ClickType.LEFT_CLICK) {
                new SearchInventory().open(player);
            }

        });

        layout.setItem(layout.getContents().length - 1, BACK_SLOT, this::handleClose);

        var item = ItemStack.builder(Material.PAPER).build();

        for (int i = 0; i < 25; i++) {
            layout.setItem(i, item);
        }

        layout.setItem(20, ItemStack.builder(Material.DRAGON_HEAD).build());

        this.builder.setCloseFunction(event -> event.setNewInventory(floorBuilder.getInventory()));
        this.builder.setLayout(layout);
        this.builder.register();
    }

    public void open(@NotNull Player player) {
        player.openInventory(this.builder.getInventory());
    }

    /**
     * Handles the internal click logic for each item in the inventory
     */
    private void handleClick(@NotNull Player player, int slotID, @NotNull ClickType clickType, @NotNull InventoryConditionResult result) {
        result.setCancel(true);

        var clickedItem = result.getClickedItem();

        if (clickedItem.material() == Material.AIR) return;

        if (clickedItem.hasTag(Tags.UUID_TAG)) {
            var instance = this.editInstanceManager.get(clickedItem.getTag(Tags.UUID_TAG));

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

        var updatedStack = result.getClickedItem().withTag(Tags.UUID_TAG, editInstance.getUniqueId());

        this.editInstanceManager.add(editInstance, updatedStack);

        result.setClickedItem(updatedStack);
        player.closeInventory();
        player.setInstance(editInstance, defaultPos);
    }

    private void handleClose(@NotNull Player player, int slotId, @NotNull ClickType clickType, @NotNull InventoryConditionResult result) {
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
