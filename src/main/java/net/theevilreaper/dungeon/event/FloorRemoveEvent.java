package net.theevilreaper.dungeon.event;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import net.theevilreaper.dungeon.data.floor.Floor;
import org.jetbrains.annotations.NotNull;

/**
 * The event will be called when a floor should be removed.
 * @author theEvilReaper
 * @since 1.0.0
 * @version 1.0.0
 */
@SuppressWarnings("java:S6206")
public final class FloorRemoveEvent implements PlayerEvent {

    private final Player player;
    private final Floor floor;

    /**
     * Create a new instance of the {@link FloorCreateEvent}.
     * @param player the player who is involved into the event
     * @param floor the floor who is involved into the event
     */
    public FloorRemoveEvent(@NotNull Player player, @NotNull Floor floor) {
        this.player = player;
        this.floor = floor;
    }

    /**
     * Returns the involved {@link Floor}.
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
        return this.player;
    }
}
