package net.theevilreaper.dungeon.inventory;

import net.minestom.server.inventory.click.Click;
import net.theevilreaper.aves.inventory.GlobalInventoryBuilder;
import net.theevilreaper.aves.inventory.InventoryBuilder;
import net.theevilreaper.aves.inventory.InventoryLayout;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.theevilreaper.aves.inventory.click.ClickHolder;
import net.theevilreaper.dungeon.data.floor.Floor;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.instance.EditInstanceManager;
import net.theevilreaper.dungeon.util.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static net.theevilreaper.dungeon.util.Items.BACK_SLOT;

@SuppressWarnings("java:S3252")
public class RoomSelector {

    private static final int START_SLOT = 9;
    private final GlobalInventoryBuilder builder;
    private final EditInstanceManager editInstanceManager;
    private final Floor floorDTO;
    private final Consumer<EditInstance> editInstanceConsumer;
    private final InventoryBuilder inventoryBuilder;
    private final InventoryBuilder floorBuilder;

    public RoomSelector(@NotNull EditInstanceManager editInstanceManager,
                        @NotNull Floor floorDTO,
                        @NotNull InventoryBuilder floorBuilder,
                        @NotNull Consumer<EditInstance> editInstanceConsumer) {
        this.editInstanceManager = editInstanceManager;
        this.editInstanceConsumer = editInstanceConsumer;
        this.floorBuilder = floorBuilder;
        this.floorDTO = floorDTO;
        this.inventoryBuilder = floorBuilder;
        this.builder = new GlobalInventoryBuilder(Component.text("Select the room to edit"), InventoryType.CHEST_6_ROW);
        var layout = InventoryLayout.fromType(this.builder.getType());

        Items.setDecorationLine(layout, this.builder.getType());
        Items.setDecorationLine(layout, InventoryType.CHEST_1_ROW);

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

    private void handleClose(@NotNull Player player, int slot, @NotNull Click click, @NotNull ItemStack stack, @NotNull Consumer<ClickHolder> result) {
        result.accept(ClickHolder.cancelClick());
        this.callCloseEvent(player);
        player.closeInventory();
        player.openInventory(floorBuilder.getInventory());
    }

    private void callCloseEvent(@NotNull Player player) {
        MinecraftServer.getGlobalEventHandler().call(
                new InventoryCloseEvent(player.getOpenInventory(), player, false));
    }
}
