package net.theevilreaper.dungeon.data.floor;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Contract;
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
     * Creates a new instance from the {@link FloorBuilder}.
     * @return the created instance
     */
    @Contract(pure = true)
    static @NotNull Builder builder() {
        return new FloorBuilder();
    }

    /**
     * Creates a new instance from the {@link FloorBuilder} to update a given {@link FloorDTO} object
     * @param floor the floor to update
     * @return the created instance
     */
    @Contract("_ -> new")
    static @NotNull Builder builder(@NotNull FloorDTO floor) {
        return new FloorBuilder(floor);
    }

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

    /**
     * The interface contains all methods which are relevant to build a new object instance from a {@link FloorDTO}.
     * @author theEvilReaper
     * @since 1.0.0
     * @version 1.0.0
     */
    sealed interface Builder permits FloorBuilder {

        /**
         * Set the name for the floor.
         * @param name the name to set
         * @return the builder instance
         */
        @NotNull Builder setName(@NotNull String name);

        /**
         * Set the externalName for the floor.
         * @param externalName the externalName to set
         * @return the builder instance
         */
        @NotNull Builder setExternalName(@NotNull String externalName);

        /**
         * Set the material for the floor.
         * @param material the material as string to set
         * @return the builder instance
         */
        @NotNull Builder setMaterial(@NotNull Material material);

        /**
         * Set the id for the floor.
         * @param id the id to set
         * @return the builder instance
         */
        @NotNull Builder setFloorID(int id);

        /**
         * Set the uuid for the floor object.
         * @param uuid the uuid to set
         * @return the builder instance
         */
        @NotNull Builder setUUID(@NotNull UUID uuid);

        /**
         * Creates a new instance from an {@link FloorDTO}.
         * @return the created instance
         */
        @NotNull FloorDTO build();
    }
}
