package net.theevilreaper.dungeon.listener.floor;

import net.theevilreaper.dungeon.data.floor.FloorProvider;
import net.theevilreaper.dungeon.event.FloorRemoveEvent;
import net.theevilreaper.dungeon.inventory.floor.FloorInventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FloorRemoveListener implements Consumer<FloorRemoveEvent> {

    private final FloorProvider floorProvider;
    private final FloorInventory floorInventory;

    public FloorRemoveListener(
            @NotNull FloorProvider floorProvider,
            @NotNull FloorInventory floorInventory
    ) {
        this.floorProvider = floorProvider;
        this.floorInventory = floorInventory;
    }

    @Override
    public void accept(@NotNull FloorRemoveEvent event) {
        this.floorProvider.removeFloor(event.getFloor());
        this.floorInventory.updateInventoryLayout();
    }
}
