package net.theevilreaper.dungeon.inventory.region.search;

import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event will be called when a player changes his filter option in the {@link net.theevilreaper.dungeon.inventory.RoomSelector} inventory.
 * The given {@link SearchOption} can be null but only when a player wants to reset the current filter to see all rooms.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class PlayerSearchChangeEvent implements PlayerEvent {

    private final Player player;
    private final SearchOption searchOption;

    /**
     * Creates a new instance from the {@link PlayerSearchChangeEvent} with the given values.
     * This method uses the slot id from the item to determine the new {@link SearchOption}
     * @param player the player who is involved
     * @param slotID the slotID from the item.
     */
    public PlayerSearchChangeEvent(@NotNull Player player, int slotID) {
        this.player = player;
        this.searchOption = SearchOption.getBySlot(slotID);
    }

    /**
     * Creates a new instance from the {@link PlayerSearchChangeEvent} with the given values.
     * @param player the player who is involved
     * @param searchOption the new {@link SearchOption} value
     */
    public PlayerSearchChangeEvent(@NotNull Player player, @Nullable SearchOption searchOption) {
        this.player = player;
        this.searchOption = searchOption;
    }

    /**
     * Returns the new {@link SearchOption} value.
     * @return null when the filter should be reset otherwise a {@link SearchOption} value
     */
    public @Nullable SearchOption getSearchOption() {
        return searchOption;
    }

    /**
     * Returns the player which is involved into the event.
     * @return the involved player
     */
    @Override
    public @NotNull Player getPlayer() {
        return player;
    }
}
