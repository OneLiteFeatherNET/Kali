package net.theevilreaper.dungeon.data.floor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
@FunctionalInterface
public interface FloorGetMethod {

    @Nullable Floor getFloorById(@NotNull UUID uuid);
}
