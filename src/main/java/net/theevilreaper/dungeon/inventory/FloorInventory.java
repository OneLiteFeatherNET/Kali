package net.theevilreaper.dungeon.inventory;

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
import net.theevilreaper.dungeon.data.floor.Floor;
import net.theevilreaper.dungeon.data.floor.FloorProvider;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.instance.EditInstanceManager;
import net.theevilreaper.dungeon.inventory.creator.FloorCreateService;
import net.theevilreaper.dungeon.items.Items;
import net.theevilreaper.dungeon.location.LocationProvider;
import net.theevilreaper.dungeon.util.Permissions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("java:S3252")
public class FloorInventory {

    private static final int START_POS = InventoryType.CHEST_1_ROW.getSize();
    private static final int MAX_ROOM_SLOT = InventoryType.CHEST_5_ROW.getSize();
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
        this.deleteInventory = new DeleteInventory(floorProvider);
        this.consumer = consumer;
        this.mappedSelectors = new HashMap<>();
        this.inventoryBuilder = new GlobalInventoryBuilder(Component.text("Floors"), InventoryType.CHEST_6_ROW);
        var layout = new InventoryLayout(this.inventoryBuilder.getType());

        Items.setDecorationLine(layout, InventoryType.CHEST_1_ROW);
        Items.setDecorationLine(layout, this.inventoryBuilder.getType());

        this.blankSlots = LayoutCalculator.quad(InventoryType.CHEST_1_ROW.getSize(), InventoryType.CHEST_5_ROW.getSize() - 1);

        layout.setItem(49, ItemStack.builder(Material.DARK_OAK_SIGN)
                .displayName(Component.text("Create floor", NamedTextColor.GREEN)).build(), (player, clickType, slot, condition) -> {
            condition.setCancel(true);
            if (!player.hasPermission(Permissions.CREATE_FLOOR_PERMISSION)) return;
            player.closeInventory();
            var inventory = this.floorCreateService.getCreateBuilder(player);
            inventory.open();
        });
        this.inventoryBuilder.setLayout(layout);
        this.inventoryBuilder.register();
        this.updateInventoryLayout();
    }

    public void updateInventoryLayout() {
        InventoryLayout inventoryLayout = this.inventoryBuilder.getLayout();
        Set<Floor> floors = floorProvider.getFloors();

        if (inventoryLayout == null || floors.isEmpty()) return;

        inventoryLayout.blank(blankSlots);

        int posCount = START_POS;
        var iterator = floors.iterator();

        while (iterator.hasNext() && posCount < MAX_ROOM_SLOT) {
            var floor = iterator.next();
            inventoryLayout.setItem(posCount, floor.getItemStack(), this::handleClick);
            posCount++;
        }

        this.inventoryBuilder.invalidateLayout();
    }

    private void handleClick(@NotNull Player player, @NotNull ClickType clickType, int slotID, @NotNull InventoryConditionResult result) {
        result.setCancel(true);

        var item = result.getClickedItem();

        if (clickType == ClickType.RIGHT_CLICK && player.hasPermission(Permissions.DELETE_FLOOR_PERMISSION) && item.getDisplayName() != null) {
            deleteInventory.openInventory(player, PlainTextComponentSerializer.plainText().serialize(item.getDisplayName()));
        }

        if (clickType == ClickType.LEFT_CLICK) {
            if (item.isAir() || item.material() == Items.DECORATION.material() || !item.hasTag(Floor.NAME_TAG)) return;

            var floor = floorProvider.getFloor(item.getTag(Floor.NAME_TAG));

            if (floor == null) {
                player.sendMessage("the clicked floor is null");
                return;
            }

            var inventory = this.mappedSelectors.computeIfAbsent(item, stack -> getSelector(floor));
            player.closeInventory();
            inventory.open(player);
        }
    }

    /**
     * Creates a new instance from the {@link RoomSelector} class.
     * @param floor the floor for the new insance
     * @return the created instance from the {@link RoomSelector}
     */
    @Contract("_ -> new")
    private @NotNull RoomSelector getSelector(@NotNull Floor floor) {
       return new RoomSelector(editInstanceManager, locationProvider, floor, this.inventoryBuilder, consumer);
    }

    /**
     * Opens the inventory for a given player.
     * @param player the player who should receive the inventory
     */
    public void open(@NotNull Player player) {
        player.openInventory(this.inventoryBuilder.getInventory());
    }
}
