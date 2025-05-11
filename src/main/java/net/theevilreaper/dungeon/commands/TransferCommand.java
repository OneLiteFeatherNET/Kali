package net.theevilreaper.dungeon.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.theevilreaper.dungeon.instance.EditInstance;
import org.jetbrains.annotations.NotNull;

import static net.theevilreaper.dungeon.util.Messages.*;

/**
 * The command allows the current owner of an {@link EditInstance} to give someone else the ownership of the instance.
 * @author theEvilReaper
 * @since 1.0.0
 * @version 1.0.0
 */
public class TransferCommand extends Command {

    private static final Component COMMAND_USAGE =
            buildPrefixedComponent(Component.text("Please use", NamedTextColor.GRAY))
                    .append(Component.text("/transfer <name>", NamedTextColor.YELLOW));
    private final ArgumentString targetArgument;

    public TransferCommand() {
        super("transfer", "tf");
        this.targetArgument = new ArgumentString("target");
        setCondition(Conditions::playerOnly);
        setDefaultExecutor((sender, context) -> sender.sendMessage(COMMAND_USAGE));
        addSyntax(this::handleCommand, targetArgument);
    }

    /**
     * Handles the logic to change the ownership of an {@link EditInstance}.
     * @param sender the involved {@link CommandSender}
     * @param context the given {@link CommandContext} from the command
     */
    private void handleCommand(@NotNull CommandSender sender, @NotNull CommandContext context) {
        var player = (Player) sender;

        if (!(player.getInstance() instanceof EditInstance editInstance)) return;

        if (!editInstance.isOwner(player.getUuid())) {
            player.sendMessage(NO_INSTANCE_OWNER);
            return;
        }

        String target = context.get(targetArgument);
        var newOwner = MinecraftServer.getConnectionManager().findOnlinePlayer(target);

        if (newOwner == null) {
            player.sendMessage(PLAYER_NOT_FOUND);
            return;
        }

        if (newOwner.getUuid().equals(player.getUuid())) {
            player.sendMessage(CANT_TRANSFER_YOURSELF);
            return;
        }

        if (!newOwner.getInstance().getUuid().equals(editInstance.getUuid())) {
            player.sendMessage(NOT_SAME_EDITOR_INSTANCE);
            return;
        }

        editInstance.setOwner(newOwner);
        newOwner.sendMessage(NO_INSTANCE_OWNER);
    }
}
