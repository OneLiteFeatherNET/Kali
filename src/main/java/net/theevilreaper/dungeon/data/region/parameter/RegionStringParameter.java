package net.theevilreaper.dungeon.data.region.parameter;

import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a string parameter.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class RegionStringParameter extends RegionParameter {

    private String value;

    public RegionStringParameter(@NotNull String name, @NotNull Material material, @Nullable String documentation, @NotNull String value) {
        super(name, material, documentation);
        this.value = value;
    }

    public void setValue(@NotNull String value) {
        if (this.value.isEmpty()) {
            throw new IllegalArgumentException("The value can not be empty");
        }
        this.value = value;
    }

    @NotNull
    public String getValue() {
        return value;
    }
}
