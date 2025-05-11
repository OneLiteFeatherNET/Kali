package net.theevilreaper.dungeon.inventory.floor;

import net.theevilreaper.aves.inventory.InventoryBuilder;
import net.theevilreaper.dungeon.data.floor.Floor;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.instance.EditInstanceManager;
import net.theevilreaper.dungeon.inventory.RoomSelector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Contains only a method to create a new instance ferom an {@link RoomSelector}.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public interface RoomSelectorCreator {

    /**
     * Creates a new instance from the {@link RoomSelector} class.
     * @param floorDTO the floor for the new instance
     * @return the created instance from the {@link RoomSelector}
     */
    default @NotNull RoomSelector getSelector(
            @NotNull EditInstanceManager editInstanceManager,
            @NotNull InventoryBuilder inventoryBuilder,
            @NotNull Floor floorDTO,
            @NotNull Consumer<EditInstance> consumer) {
        return new RoomSelector(editInstanceManager, floorDTO, inventoryBuilder, consumer);
    }
}
