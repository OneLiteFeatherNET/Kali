package net.theevilreaper.dungeon.data.floor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FloorTestModel {

    @Contract(pure = true)
    public static @NotNull FloorDTO getModel(int id, String name) {
        return getModel(id, name, null);
    }

    @Contract(pure = true)
    public static @NotNull FloorDTO getModel(int id, String name, UUID uuid) {
        return Floor.builder()
                .setFloorID(id)
                .setName(name == null ? "Dummy" : name)
                .setUUID(uuid)
                .build();
    }

    @Contract(pure = true)
    public static @NotNull FloorDTO getModel() {
        return Floor.builder()
                .build();
    }
}
