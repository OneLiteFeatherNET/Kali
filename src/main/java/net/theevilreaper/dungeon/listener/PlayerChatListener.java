package net.theevilreaper.dungeon.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.player.PlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Handles what happens when the player writes a message into chat.
 * The current implementation of the function applies the rank from a player to the chat message
 *
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
public class PlayerChatListener implements Consumer<PlayerChatEvent> {

    private static final Component CHAT_SEPARATOR = Component.space().append(Component.text("»", NamedTextColor.GRAY).append(Component.space()));

    private final Function<PlayerChatEvent, Component> chatFunction = event ->
            event.getPlayer().getDisplayName().append(CHAT_SEPARATOR).append(Component.text(event.getMessage(), NamedTextColor.WHITE));

    /**
     * Applies the custom chat function to the event
     * @param event the input argument
     */
    @Override
    public void accept(@NotNull PlayerChatEvent event) {
        event.setChatFormat(chatFunction);
    }
}
