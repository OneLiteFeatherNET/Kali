package net.theevilreaper.dungeon.data.floor;

import dev.morphia.annotations.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.util.Messages;
import net.theevilreaper.dungeon.util.Tags;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents the floor object which is stored in a databse.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
@Entity("floors")
public class FloorDTO implements Floor {

    @Id
    private ObjectId objectId;
    @Property("id")
    private final UUID uuid;
    @Property("external")
    private final String externalName;
    private final String name;
    private int floorID = 1;

    private final Material material;

    @Transient
    protected ItemStack itemStack;

    /**
     * Creates a new reference from the {@link Floor} class with the given parameters.
     * @param uuid the uuid for the floor
     * @param name the name for the floor
     * @param externalName the external name for the floor
     * @param material the material for the floor
     * @param id the internal floor id
     */
    protected FloorDTO(@NotNull UUID uuid, @NotNull String name, @NotNull String externalName, @NotNull Material material, int id) {
        this.name = name;
        this.uuid = uuid;
        this.externalName = externalName;
        this.material = material;
        this.floorID = id;
    }

    /**
     * Returns a boolean indicator if the floor has a valid name.
     * @return true when the name is not null and not empty otherwise false
     */
    @Override
    public boolean hasName() {
        return !name.trim().isEmpty();
    }

    /**
     * Returns the icon {@link ItemStack} from the floor.
     * @return the given {@link ItemStack}
     */
    @Override
    public @NotNull ItemStack getItemStack() {
        if (itemStack == null) {
            this.itemStack = ItemStack.of(material)
                    .with(builder -> builder.customName(Component.text(this.name, NamedTextColor.RED))
                            .lore(Messages.FLOOR_LORE)
                            .set(Tags.FLOOR_ID, uuid));
        }
        return itemStack;
    }

    /**
     * Returns the external name from the floor.
     * The name is used for the folder name
     * @return the given external name
     */
    @NotNull
    public String getExternalName() {
        return externalName;
    }

    /**
     * Returns the floor id from the object.
     * The default value for the id is one
     * @return the given id
     */
    public int id() {
        return floorID;
    }

    /**
     * Returns the {@link UUID} from the object.
     * @return the given {@link UUID}
     */
    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }

    /**
     * Retrurns the given {@link Material} from the object.
     * @return the underlying material
     */
    @Override
    public @NotNull Material getMaterial() {
        return material;
    }

    /**
     * Returns the given name from the object.
     * @return the name
     */
    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FloorDTO floorDTO = (FloorDTO) o;
        return Objects.equals(uuid, floorDTO.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), uuid);
    }

    /**
     * Returns a string representation from the floor.
     * @return the string representation
     */
    @Override
    public String toString() {
        return "Floor{" +
                ", externalName='" + externalName + '\'' +
                ", name='" + name + '\'' +
                ", material=" + material +
                '}';
    }
}
