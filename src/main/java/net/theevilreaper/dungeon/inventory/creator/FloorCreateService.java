package net.theevilreaper.dungeon.inventory.creator;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.network.packet.client.play.ClientNameItemPacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class FloorCreateService {

    private static final int MAX_NAME_SIZE = 25;
    private final Map<UUID, FloorCreateInventory> openSelections;

    public FloorCreateService() {
        this.openSelections = new HashMap<>();
        MinecraftServer.getPacketListenerManager().setListener(ClientNameItemPacket.class, (packet, player) -> {
            if (isAnvilGui(player)) {
                var inputString = packet.itemName();
                if (inputString.length() > MAX_NAME_SIZE) return;
                var gui = openSelections.get(player.getUuid());
                if (gui == null) return;
                gui.updateName(inputString);
            }
        });
    }

    /**
     * Get the {@link FloorCreateInventory} from the service by the player or create a new instance of it.
     * @param player the player to check
     * @return the reference form the inventory or the created reference
     */
    @NotNull
    public FloorCreateInventory getCreateBuilder(@NotNull Player player) {
        return this.openSelections.computeIfAbsent(player.getUuid(), uuid -> new FloorCreateInventory(player));
    }

    /**
     * Removes a player bounded selection inventory from the underlying map.
     * @param player The player to check
     * @return True if the inventory can be removed otherwise false
     */
    public boolean remove(@NotNull Player player) {
        var inventory = this.openSelections.get(player.getUuid());

        if (inventory != null) {
            inventory.unregister();
            return true;
        }
        return false;
    }

    /**
     * Checks if the open inventory is an anvil inventory.
     * @param player the player to check
     * @return True if the open inventory is an anvil gui otherwise false
     */
    private boolean isAnvilGui(@NotNull Player player) {
        return player.getOpenInventory() != null && player.getOpenInventory().getInventoryType() == InventoryType.ANVIL;
    }
}
