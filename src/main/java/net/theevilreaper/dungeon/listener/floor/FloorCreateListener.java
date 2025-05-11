package net.theevilreaper.dungeon.listener.floor;

import net.theevilreaper.dungeon.data.floor.FloorProvider;
import net.theevilreaper.dungeon.event.FloorCreateEvent;
import net.theevilreaper.dungeon.inventory.floor.FloorInventory;
import net.theevilreaper.dungeon.util.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FloorCreateListener implements Consumer<FloorCreateEvent> {

    private final FloorProvider floorProvider;
    private final FloorInventory floorInventory;

    public FloorCreateListener(
            @NotNull FloorProvider floorProvider,
            @NotNull FloorInventory floorInventory
    ) {
        this.floorProvider = floorProvider;
        this.floorInventory = floorInventory;
    }


    @Override
    public void accept(@NotNull FloorCreateEvent event) {
        if (!event.getFloor().hasName()) {
            event.getPlayer().sendMessage(Messages.ABORT_FLOOR_CREATION);
            return;
        }
        this.floorProvider.addFloor(event.getFloor());
        this.floorInventory.updateInventoryLayout();
    }
}
