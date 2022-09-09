package net.theevilreaper.dungeon.data.floor;

import dev.morphia.annotations.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.theevilreaper.dungeon.data.DungeonObject;
import net.theevilreaper.dungeon.data.room.AbstractRoom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Entity("floors")
public class Floor extends DungeonObject {

    private int floorID = 1;

    public static final Tag<String> NAME_TAG = Tag.String("name");

    @Transient
    private static final List<Component> LORE = List.of(
        Component.empty(),
        Component.text("LeftClick: ", NamedTextColor.GRAY).append(Component.text("Edit").style(Style.style().decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN))),
        Component.empty(),
        Component.text("RightClick: ", NamedTextColor.GRAY).append(Component.text("Delete").style(Style.style().decorate(TextDecoration.BOLD).color(NamedTextColor.RED))),
        Component.empty()
    );

    private transient final Lock lock;

    @Property("external")
    private String externalName;

    private transient HashMap<String, AbstractRoom> rooms;

    public Floor() {
        super("empty", Material.DIRT);
        this.lock = new ReentrantLock();
    }

    public Floor(@NotNull String name, @NotNull String externalName, @NotNull Material material, @NotNull String description, int floorID) {
        super(name, material, description);
        this.externalName = externalName;
        this.rooms = new HashMap<>();
        this.lock = new ReentrantLock();
        this.floorID = floorID;
    }

    public Floor(@NotNull String name, @NotNull String externalName, @NotNull Material material, int floorID) {
        super(name, material);
        this.externalName = externalName;
        this.floorID = floorID;
        this.rooms = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    /**
     * Sets a new id for the floor.
     * @param floorID the id to set
     */
    public void setFloorID(int floorID) {
        this.floorID = floorID;
    }

    /**
     * Set a new external name for the floor.
     * @param externalName the name to set
     */
    public void setExternalName(@NotNull String externalName) {
        this.externalName = externalName;
    }

    public void setRooms(@NotNull HashMap<String, AbstractRoom> rooms) {
        try {
            lock.lock();
            this.rooms = rooms;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Add a new room to the floor.
     * @param name the name from the floor
     * @param abstractRoom the room object
     * @return the added room object
     */
    public AbstractRoom addRoom(@NotNull String name, @NotNull AbstractRoom abstractRoom) {
        try {
            lock.lock();
            return this.rooms.putIfAbsent(name, abstractRoom);
        } finally {
            lock.unlock();
        }
    }

    @Nullable
    public AbstractRoom getRoom(@NotNull String name) {
        try {
            lock.lock();
            return this.rooms.get(name);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes the room from the underlying map.
     * @param name the name from the room to remove
     * @return True if the room is removed
     */
    public boolean removeRoom(@NotNull String name) {
        try {
            lock.lock();
            return this.rooms.remove(name) != null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the icon {@link ItemStack} from the floor.
     * @return the given {@link ItemStack}
     */
    @Override
    public @NotNull ItemStack getItemStack() {
        if (itemStack == null) {
            this.itemStack = ItemStack.of(material)
                    .with(builder -> builder.displayName(Component.text(this.name, NamedTextColor.RED))
                            .lore(LORE)
                            .set(NAME_TAG, externalName));
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
     * Returns the map which contains all registered {@link AbstractRoom} from the floor.
     * @return the given map with the entries
     */
    @NotNull
    public HashMap<String, AbstractRoom> getRooms() {
        try {
            lock.lock();
            return rooms;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the floor id from the object.
     * The default value for the id is one
     * @return the given id
     */
    public int getFloorID() {
        return floorID;
    }

    /**
     * Returns a string representation from the floor.
     * @return the string representation
     */
    @Override
    public String toString() {
        return "Floor{" +
                "lock=" + lock +
                ", externalName='" + externalName + '\'' +
                ", name='" + name + '\'' +
                ", material=" + material +
                '}';
    }
}
