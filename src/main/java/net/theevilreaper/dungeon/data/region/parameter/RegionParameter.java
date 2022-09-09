package net.theevilreaper.dungeon.data.region.parameter;

import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.data.DungeonObject;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the base structure for a parameter in the region context.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public abstract class RegionParameter extends DungeonObject {

    /**
     * Create a new object from the class.
     * @param name the name for the parameter
     * @param description the documentation for the parameter (can be null)
     */
    public RegionParameter(@NotNull String name, @NotNull Material material, String description) {
        super(name, material, description);
    }
}
