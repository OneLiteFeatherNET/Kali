package net.theevilreaper.dungeon.data.floor;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The class contains the structure which describes a floor.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public interface Floor {

    /**
     * Returns a boolean indicator if the floor reference has a name.
     * @return true when the name is not null and is not empty otherwise false
     */
    boolean hasName();

    /**
     * Returns the given id from the floor.
     * @return the underlying id
     */
    int id();

    /**
     * Returns the given {@link UUID} from the floor.
     * @return the underlying uuid
     */
    @NotNull UUID getUUID();

    /**
     * Returns the given name.
     * @return the name
     */
    @NotNull String getName();

    /**
     * Returns the external name.
     * @return null when there is no external name otherwise the given value
     */
    @Nullable String getExternalName();

    @NotNull Material getMaterial();

    /**
     * Returns the {@link ItemStack} which can be used to show the floor in an inventory.
     * @return the given stack
     */
    @NotNull ItemStack getItemStack();
}
