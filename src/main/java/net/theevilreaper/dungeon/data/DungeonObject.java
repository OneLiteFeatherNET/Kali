package net.theevilreaper.dungeon.data;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Transient;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * The object class describes a generic object for the data structure in the dungeon editor extension.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("java:S3252")
@Deprecated(forRemoval = true)
public class DungeonObject implements Serializable {

    @Id
    private ObjectId objectId;

    @Transient
    private static final Material DEFAULT = Material.STONE;

    @Indexed
    protected String name;
    protected String description;

    protected Material material;

    @Transient
    protected transient ItemStack itemStack;

    /**
     * Creates a new instance of the {@link DungeonObject}.
     * @param name The name of the object
     * @param material The material which is used for the {@link ItemStack}
     * @param description A description for the object
     */
    public DungeonObject(@NotNull String name, @NotNull Material material, @NotNull String description) {
        this.name = name;
        this.material = material;
        this.description = description;
    }

    /**
     * Creates a new instance of the {@link DungeonObject}.
     * @param name The name of the object
     * @param material The material which is used for the {@link ItemStack}
     */
    public DungeonObject(@NotNull String name, @NotNull Material material) {
        this.name = name;
        this.material = material;
    }

    public DungeonObject(@NotNull String name) {
        this.name = name;
        this.material = DEFAULT;
    }

    public void setObjectId(@NotNull ObjectId objectId) {
        this.objectId = objectId;
    }

    /**
     * Add a new description to the object.
     * @param description the description to set
     */
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    /**
     * Set the new material for the {@link ItemStack}.
     * @param material the material to set
     */
    public void setMaterial(@NotNull Material material) {
        this.material = material;
    }

    /**
     * Set a new name for the object.
     * @param name the new name to set
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DungeonObject that = (DungeonObject) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Returns the name from the object.
     * @return the given name
     */

    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Set's the underlying item reference to null.
     */
    public void invalidateStack() {
        this.itemStack = null;
    }

    /**
     * Return the {@link ItemStack} from the object class.
     * The stack can be used as icon for an inventory or something else.
     * When the stack is null the methods creates a new {@link ItemStack}.
     * @return the underlying {@link ItemStack} reference
     */
    @NotNull
    public ItemStack getItemStack() {
        if (itemStack == null) {
            this.itemStack = ItemStack.builder(material == null ? DEFAULT : material)
                    .displayName(LegacyComponentSerializer.legacySection().deserialize(name))
                    .lore(getLore())
                    .meta(itemMetaBuilder -> itemMetaBuilder.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES))
                    .build();
        }
        return itemStack;
    }

    /**
     * Returns an array for the lore in the {@link ItemStack}.
     * @return the array which contains all lines for the lore
     */
    private Component @Nullable [] getLore() {
        if (description == null) return null;

        var array = new Component[2];
        array[0] = Component.empty();
        array[1] = LegacyComponentSerializer.legacyAmpersand().deserialize(description);
        return array;
    }

    public ObjectId getObjectId() {
        return objectId;
    }
}
