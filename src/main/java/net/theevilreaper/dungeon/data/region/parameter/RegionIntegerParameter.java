package net.theevilreaper.dungeon.data.region.parameter;

import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since
 **/

public class RegionIntegerParameter extends RegionParameter {

    private final int maximum;
    private final int minimum;

    private int value;

    public RegionIntegerParameter(@NotNull String name, @NotNull Material material, @Nullable String documentation, int maximum, int minimum) {
        super(name, material, documentation);
        this.maximum = maximum;
        this.minimum = minimum;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isInRange(int value) {
        return value >= minimum && value <= maximum;
    }

    public int getValue() {
        return value;
    }
}
