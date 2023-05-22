package net.theevilreaper.dungeon.inventory.floor;

import de.icevizion.aves.inventory.GlobalInventoryBuilder;
import de.icevizion.aves.inventory.InventoryLayout;
import de.icevizion.aves.inventory.util.LayoutCalculator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.data.floor.FloorProvider;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.instance.EditInstanceManager;
import net.theevilreaper.dungeon.inventory.DeleteInventory;
import net.theevilreaper.dungeon.inventory.RoomSelector;
import net.theevilreaper.dungeon.inventory.creator.FloorCreateService;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.location.LocationProvider;
import net.theevilreaper.dungeon.util.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import java.util.function.Consumer;

@SuppressWarnings("java:S3252")
public class FloorInventory implements RoomSelectorCreator {

    private final GlobalInventoryBuilder inventoryBuilder;
    private final FloorProvider floorProvider;
    private final DeleteInventory deleteInventory;
    private final Map<ItemStack, RoomSelector> mappedSelectors;
    private final EditInstanceManager editInstanceManager;
    private final FloorCreateService floorCreateService;
    private final LocationProvider locationProvider;
    private final Consumer<EditInstance> consumer;
    private final int[] blankSlots;

    public FloorInventory(@NotNull EditInstanceManager editInstanceManager,
                          @NotNull LocationProvider locationProvider,
                          @NotNull FloorProvider floorProvider,
                          @NotNull FloorCreateService floorCreateService,
                          @NotNull Consumer<EditInstance> consumer) {
        this.editInstanceManager = editInstanceManager;
        this.locationProvider = locationProvider;
        this.floorProvider = floorProvider;
        this.floorCreateService = floorCreateService;
        this.consumer = consumer;
        this.mappedSelectors = new HashMap<>();
        this.inventoryBuilder = new GlobalInventoryBuilder(Component.text("Floors"), InventoryType.CHEST_6_ROW);
        var layout = new InventoryLayout(this.inventoryBuilder.getType());

        Items.setDecorationLine(layout, InventoryType.CHEST_1_ROW);
        Items.setDecorationLine(layout, this.inventoryBuilder.getType());

        this.blankSlots = LayoutCalculator.quad(InventoryType.CHEST_1_ROW.getSize(), InventoryType.CHEST_5_ROW.getSize() - 1);

        layout.setItem(49, ItemStack.builder(Material.DARK_OAK_SIGN).displayName(Component.text("Create floor", NamedTextColor.GREEN)).build(), this::handleCreateClick);

        this.inventoryBuilder.setDataLayoutFunction(inventoryLayout -> {
            inventoryLayout = inventoryLayout == null ? new InventoryLayout(this.inventoryBuilder.getType()) : inventoryLayout;
            inventoryLayout.blank(blankSlots);
            var floors = floorProvider.getFloors();
            var iterator = floors.iterator();
            int counter = 0;
            while(iterator.hasNext() && counter < this.blankSlots.length) {
                var floor = iterator.next();
                inventoryLayout.setItem(this.blankSlots[counter], floor.getItemStack(), this::handleClick);
                counter++;
            }
            return inventoryLayout;
        });

        this.inventoryBuilder.setLayout(layout);
        this.inventoryBuilder.register();
        this.updateInventoryLayout();
        this.deleteInventory = new DeleteInventory(floorProvider, this.inventoryBuilder.getInventory());
    }

    /**
     * Invalidates the data layout to indicates that the layout should be updated.
     */
    public void updateInventoryLayout() {
        this.inventoryBuilder.invalidateDataLayout();
    }

    /**
     * Handles the click for the item which allows the creation of new floors.
     * @param player the player who clicked on the item
     * @param clickType the given {@link ClickType}
     * @param slotID the slot id
     * @param result the {@link InventoryConditionResult} from the click
     */
    private void handleCreateClick(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);
       // if (!player.hasPermission(Permissions.CREATE_FLOOR_PERMISSION)) return;
        player.closeInventory();
        var inventory = this.floorCreateService.getCreateBuilder(player);
        inventory.open();
    }

    /**
     * Handles the click logic for a floor item in the inventory.
     * @param player the player who clicked on the item
     * @param clickType the given {@link ClickType}
     * @param slotID the slot id
     * @param result the {@link InventoryConditionResult} from the click
     */
    private void handleClick(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);

        var item = result.getClickedItem();


        if (clickType == ClickType.RIGHT_CLICK && /*player.hasPermission(Permissions.DELETE_FLOOR_PERMISSION) &&*/ item.getDisplayName() != null) {
            deleteInventory.openInventory(player, item.getTag(Tags.FLOOR_ID));
            return;
        }

        if (clickType == ClickType.LEFT_CLICK) {
            if (item.isAir() || item.material() == Items.DECORATION.material() || !item.hasTag(Tags.FLOOR_ID)) return;
            var floor = floorProvider.getFloorById(item.getTag(Tags.FLOOR_ID));
            if (floor == null) {
                player.sendMessage("the clicked floor is null");
                return;
            }

            var inventory = this.mappedSelectors.computeIfAbsent(item, stack -> getSelector(editInstanceManager, locationProvider, inventoryBuilder, floor, consumer));
            player.closeInventory();
            inventory.open(player);
        }
    }

    /**
     * Opens the inventory for a given player.
     * @param player the player who should receive the inventory
     */
    public void open(@NotNull Player player) {
        player.openInventory(this.inventoryBuilder.getInventory());
    }
}
