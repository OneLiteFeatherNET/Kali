package net.theevilreaper.dungeon.data.floor;

import net.minestom.server.item.Material;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The class is the implementation of the {@link FloorDTO.Builder} interface.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public final class FloorBuilder implements FloorDTO.Builder {

    private final UUID uuid;
    private int floorID;
    private String name;
    private String externalName;
    private Material material;

    /**
     * Creates a new reference from the builder class.
     */
    public FloorBuilder() {
        this.uuid = UUID.randomUUID();
    }

    /**
     * Creates a new reference from the builder class.
     * This constructor should only be used to update an existing {@link FloorDTO} object
     * @param floor the floor reference to get the data from it
     */
    public FloorBuilder(@NotNull FloorDTO floor) {
        this.name = floor.getName();
        this.externalName = floor.getExternalName();
        this.floorID = floor.id();
        this.uuid = floor.getUUID();
    }

    @Override
    public FloorDTO.@NotNull Builder setName(@NotNull String name) {
        Check.argCondition(name.trim().isEmpty(), "The name can't be empty");
        this.name = name;
        return this;
    }

    @Override
    public FloorDTO.@NotNull Builder setExternalName(@NotNull String externalName) {
        this.externalName = externalName;
        return this;
    }

    @Override
    public FloorDTO.@NotNull Builder setMaterial(@NotNull Material material) {
        this.material = material;
        return this;
    }

    @Override
    public FloorDTO.@NotNull Builder setId(int id) {
        Check.argCondition(id < 0, "The id can't be negative");
        this.floorID = id;
        return this;
    }

    @Override
    public @NotNull FloorDTO build() {
        material = material == null ? Material.STONE : material;
        return new FloorDTO(uuid, name == null ? "" : name, externalName, material, floorID);
    }
}
