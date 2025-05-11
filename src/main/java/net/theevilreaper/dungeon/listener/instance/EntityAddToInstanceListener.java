package net.theevilreaper.dungeon.listener.instance;

import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.util.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EntityAddToInstanceListener implements Consumer<AddEntityToInstanceEvent> {

    private final Items items;

    public EntityAddToInstanceListener(@NotNull Items items) {
        this.items = items;
    }

    @Override
    public void accept(@NotNull AddEntityToInstanceEvent event) {
        if (event.getInstance() instanceof EditInstance editInstance && event.getEntity() instanceof Player player) {
            editInstance.add(player);
            this.items.setEditItems(player);
        }
    }
}
