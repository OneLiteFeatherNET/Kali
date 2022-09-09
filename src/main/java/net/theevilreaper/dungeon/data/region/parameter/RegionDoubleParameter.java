package net.theevilreaper.dungeon.data.region.parameter;

import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since
 **/

public class RegionDoubleParameter extends RegionParameter {

    private final double maximum;
    private final double minimum;

    private double value;

    public RegionDoubleParameter(@NotNull String name, @NotNull Material material, @Nullable String documentation, double maximum, double minimum) {
        super(name, material, documentation);
        this.maximum = maximum;
        this.minimum = minimum;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isInRange(double value) {
        return value >= minimum && value <= maximum;
    }

    public double getValue() {
        return value;
    }
}
