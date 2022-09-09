package net.theevilreaper.dungeon.data.region.parameter;

import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class RegionBooleanParameter extends RegionParameter {

    private boolean value;

    public RegionBooleanParameter(@NotNull String name, @NotNull Material material, @Nullable String documentation, boolean value) {
        super(name, material, documentation);
        this.value = value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }
}
