package net.theevilreaper.dungeon.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.theevilreaper.dungeon.data.floor.Floor;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player interacts with the save button in the {@link net.theevilreaper.dungeon.inventory.creator.FloorCreateInventory}.
 * The event will only be called when the floor has no invalid data.
 *
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
public final class FloorCreateEvent implements PlayerEvent, CancellableEvent {

    private final Player creator;
    private final Floor floor;
    private boolean cancelled;

    /**
     * Create a new instance of the {@link FloorCreateEvent}.
     * @param creator the player who is involved into the event
     * @param floor the floor who is involved into the event
     */
    public FloorCreateEvent(@NotNull Player creator, @NotNull Floor floor) {
        this.creator = creator;
        this.floor = floor;
    }

    /**
     * Create a new instance of the {@link FloorCreateEvent}.
     * @param creator the player who is involved into the event
     * @param floor the floor who is involved into the event
     * @param cancelled if the event is cancelled or not
     */
    public FloorCreateEvent(@NotNull Player creator, @NotNull Floor floor, boolean cancelled) {
        this.creator = creator;
        this.floor = floor;
        this.cancelled = cancelled;
    }

    /**
     * Set if the event should be cancelled or not
     * @param cancel true if the event should be cancelled, false otherwise
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Returns a boolean indicator if the event is cancelled.
     * @return True if the event is cancelled otherwise false
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Returns the involved {@link FloorDTO}.
     * @return the given floor
     */
    @NotNull
    public Floor getFloor() {
        return this.floor;
    }

    /**
     * Returns the involved {@link Player}.
     * @return the given player
     */
    @Override
    public @NotNull Player getPlayer() {
        return this.creator;
    }
}
