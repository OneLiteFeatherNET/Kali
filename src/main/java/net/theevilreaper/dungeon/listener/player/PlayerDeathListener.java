package net.theevilreaper.dungeon.listener.player;

import net.kyori.adventure.text.Component;
import net.minestom.server.event.player.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class PlayerDeathListener implements Consumer<PlayerDeathEvent> {

    @Override
    public void accept(@NotNull PlayerDeathEvent event) {
        event.setChatMessage(Component.empty());
    }
}
