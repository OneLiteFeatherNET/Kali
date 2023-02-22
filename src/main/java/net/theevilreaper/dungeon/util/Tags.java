package net.theevilreaper.dungeon.util;

import net.minestom.server.tag.Tag;

import java.util.UUID;

public final class Tags {

    public static final Tag<Byte> ITEM_TAGS = Tag.Byte("floorItem");
    public static final Tag<Integer> NAME_TAG = Tag.Integer("displayTag");
    public static final Tag<UUID> UUID_TAG = Tag.UUID("id");
    public static final Tag<Byte> RESET_TAG = Tag.Byte("reset");
    public static final Tag<String> DELETE_FLOOR = Tag.String("deleteFloor");

    private Tags() {}
}
