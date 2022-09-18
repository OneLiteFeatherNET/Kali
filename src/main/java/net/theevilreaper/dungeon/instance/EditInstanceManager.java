package net.theevilreaper.dungeon.instance;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The class manages all current active {@link EditInstance}'s.
 * Each room can only have one active {@link EditInstance}
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
public class EditInstanceManager {
    private static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    private final Map<UUID, EditInstance> editInstanceMap;
    private final Map<ItemStack, UUID> stackToUUID;

    /**
     * Creates a new instance from the manager.
     */
    public EditInstanceManager() {
        this.editInstanceMap = new HashMap<>();
        this.stackToUUID = new HashMap<>();
    }

    /**
     * Add a new {@link EditInstance} reference to the underling map.
     * @param editInstance the instance to add
     */
    public void add(@NotNull EditInstance editInstance, @NotNull ItemStack stack) {
        this.editInstanceMap.putIfAbsent(editInstance.getUniqueId(), editInstance);
        this.stackToUUID.putIfAbsent(stack, editInstance.getUniqueId());
    }

    /**
     * Removes a {@link EditInstance} by the given uuid.
     * @param uuid the uuid from the instance
     * @return the removed {@link EditInstance} otherwise false
     */
    @Nullable
    public EditInstance remove(@NotNull UUID uuid) {
        var instance = editInstanceMap.remove(uuid);

        if (instance == null) return null;

        INSTANCE_MANAGER.unregisterInstance(instance);
        return instance;
    }

    private boolean canRemove(@NotNull UUID uuid) {
        if (this.stackToUUID.isEmpty()) return false;

        for (var entry : this.stackToUUID.entrySet()) {
            if (entry.getValue().equals(uuid)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Unregisters a given {@link EditInstance} from the given cache and {@link InstanceManager}.
     * @param editInstance the instance to unregister
     */
    public void remove(@NotNull EditInstance editInstance) {
        if (this.editInstanceMap.remove(editInstance.getUniqueId()) != null) {
            INSTANCE_MANAGER.unregisterInstance(editInstance);
        }
    }

    /**
     * Returns the {@link EditInstance} which matches with the given {@link UUID}.
     * Note: The uuid argument of this method is the uuid from the instance itself and not an uuid from a {@link Player}
     * @param uuid the uuid to get the instance
     * @return the fetched {@link EditInstance} otherwise false
     */
    @Nullable
    public EditInstance get(@NotNull UUID uuid) {
        return editInstanceMap.get(uuid);
    }
}
