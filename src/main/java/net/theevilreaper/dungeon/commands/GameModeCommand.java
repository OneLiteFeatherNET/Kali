package net.theevilreaper.dungeon.commands;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;
import net.theevilreaper.dungeon.DungeonEditor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since
 **/
public class GameModeCommand extends Command {

    private static final Component NO_PLAYER = DungeonEditor.PREFIX
            .append(Component.text("No player", NamedTextColor.RED));

    private static final Component NO_PLAYER_FOUND = DungeonEditor.PREFIX
            .append(Component.text("No player found", NamedTextColor.RED));

    private static final Component USAGE = DungeonEditor.PREFIX
            .append(Component.text("Usage: /gamemode <gm> [targets]", NamedTextColor.RED));

    public GameModeCommand() {
        super("gamemode", "gm");

        setCondition(Conditions::playerOnly);

        ArgumentEnum<GameMode> gamemode = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        ArgumentEntity playerArgument = ArgumentType.Entity("targets").onlyPlayers(true);

        setDefaultExecutor((sender, context) -> sender.sendMessage(USAGE));

        //Command Syntax for /gamemode <gamemode>
        addSyntax((sender, context) -> {
            //Limit execution to players only
            if (!(sender instanceof Player player)) {
                sender.sendMessage(NO_PLAYER);
                return;
            }
            GameMode mode = context.get(gamemode);

            //Set the gamemode for the sender
            executeSelf(player, mode);
        }, gamemode);

        //Command Syntax for /gamemode <gamemode> [targets]
        addSyntax((sender, context) -> {
            EntityFinder finder = context.get(playerArgument);
            GameMode mode = context.get(gamemode);

            //Set the gamemode for the targets
            executeOthers(sender, mode, finder.find(sender));
        }, gamemode, playerArgument);
    }

    /**
     * Sets the gamemode for the specified entities, and
     * notifies them (and the sender) in the chat.
     */
    private void executeOthers(@NotNull CommandSender sender, @NotNull GameMode mode, @NotNull List<Entity> entities) {
        if (entities.isEmpty()) {
            sender.sendMessage(NO_PLAYER_FOUND);
        } else for (Entity entity : entities) {
            if (entity instanceof Player player) {
                if (player == sender) {
                    executeSelf((Player) sender, mode);
                } else {
                    player.setGameMode(mode);

                    String gamemodeString = "gameMode." + mode.name().toLowerCase(Locale.ROOT);
                    Component gamemodeComponent = Component.translatable(gamemodeString);
                    Component playerName = player.getDisplayName() == null ? player.getName() : player.getDisplayName();

                    //Send a message to the changed player and the sender
                    player.sendMessage(DungeonEditor.PREFIX.append(Component.text("Changed gamemode to ", NamedTextColor.GRAY).append(gamemodeComponent)));
                    sender.sendMessage(Component.translatable("commands.gamemode.success.other", playerName, gamemodeComponent), MessageType.SYSTEM);
                }
            }
        }
    }

    /**
     * Sets the gamemode for the executing Player, and
     * notifies them in the chat.
     */
    private void executeSelf(Player sender, GameMode mode) {
        sender.setGameMode(mode);

        //The translation keys 'gameMode.survival', 'gameMode.creative', etc.
        //correspond to the translated game mode names.
        String gamemodeString = "gameMode." + mode.name().toLowerCase(Locale.ROOT);
        Component gamemodeComponent = Component.translatable(gamemodeString, NamedTextColor.GREEN);

        //Send the translated message to the player.
        sender.sendMessage(DungeonEditor.PREFIX.append(Component.text("Changed gamemode to ", NamedTextColor.GRAY).append(gamemodeComponent)));
    }
}
