package net.theevilreaper.dungeon.listener.instance;

import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.theevilreaper.dungeon.instance.EditInstance;

import java.util.function.Consumer;

public class EntityRemoveFromInstanceListener implements Consumer<RemoveEntityFromInstanceEvent> {
    @Override
    public void accept(RemoveEntityFromInstanceEvent event) {
        if (event.getInstance() instanceof EditInstance editInstance && event.getEntity() instanceof Player player) {
            editInstance.remove(player);
            player.getInventory().clear();
            editInstance.switchOwner();
        }
    }
}
