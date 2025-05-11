package net.theevilreaper.dungeon.listener;

import net.theevilreaper.aves.util.Broadcaster;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.theevilreaper.dungeon.inventory.region.search.PlayerSearchChangeEvent;

import java.util.function.Consumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since
 **/

public class SearchChangeListener implements Consumer<PlayerSearchChangeEvent> {

    public SearchChangeListener() {

    }

    @Override
    public void accept(PlayerSearchChangeEvent event) {
        Broadcaster.broadcast(Component.text("Changed filter to ").append(event.getSearchOption() == null ? Component.text("NONE") : event.getSearchOption().getItem().get(ItemComponent.CUSTOM_NAME)));
    }
}
