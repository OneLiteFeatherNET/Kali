package net.theevilreaper.dungeon.data.room;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the given types for a room.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RoomType {

    NORMAL(0),
    DEAD_END(1),
    BOSS_ROO(2),
    ITEM_ROOM(3),
    SHOP_ROOM(4),
    START_ROOM(5);

    private final int id;

    // Cache the values' method to improve performance
    public static final RoomType[] VALUES = values();

    RoomType(int id) {
        this.id = id;
    }

    /**
     * Returns the id from the room.
     * @return the given id
     */
    public int getId() {
        return id;
    }

    /**
     * Maps the given id from a room to his enum equivalent.
     * @param id the id to convert
     * @return the mapped typ if there is one room with the id otherwise false
     */
    @Nullable
    public static RoomType getByID(int id) {
        if (id > VALUES.length) return null;
        return VALUES[id];
    }
}
