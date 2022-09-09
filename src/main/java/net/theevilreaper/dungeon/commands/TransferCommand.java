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
import net.theevilreaper.dungeon.util.Messages;
import org.jetbrains.annotations.NotNull;

import static net.theevilreaper.dungeon.util.Messages.NO_INSTANCE_OWNER;
import static net.theevilreaper.dungeon.util.Messages.PLAYER_NOT_FOUND;

/**
 * The command allows to transfer to ownership of an instance to another player.
 * @author theEvilReaper
 * @since 1.0.0
 * @version 1.0.0
 */
public class TransferCommand extends Command {

    private static final Component NOT_SAME_EDITOR_INSTANCE =
            Messages.PREFIX.append(Component.text(
                    "Can't set the owner because the target is not on the same instance!",
                    NamedTextColor.RED
            ));

    private static final Component COMMAND_USAGE =
            Messages.PREFIX.append(Component.text("Please use", NamedTextColor.GRAY))
                    .append(Component.text("/transfer <name>", NamedTextColor.YELLOW));


    public TransferCommand() {
        super("transfer", "tf");

        setCondition(Conditions::playerOnly);

        setDefaultExecutor((sender, context) -> sender.sendMessage(COMMAND_USAGE));

        var targetArgument= new ArgumentString("target");

        addSyntax(this::handleCommand, targetArgument);
    }

    private void handleCommand(@NotNull CommandSender sender, @NotNull CommandContext context) {
        var player = (Player) sender;

        String target = context.get("target");

        if (player.getInstance() instanceof EditInstance editInstance) {
            if (editInstance.getOwner() != null && !editInstance.hasSameUUID(player.getUuid())) {
                player.sendMessage(NO_INSTANCE_OWNER);
                return;
            }

            var newOwner = MinecraftServer.getConnectionManager().findPlayer(target);

            if (newOwner == null) {
                player.sendMessage(PLAYER_NOT_FOUND);
                return;
            }

            if (newOwner.getInstance() != null && !newOwner.getInstance().getUniqueId().equals(editInstance.getUniqueId())) {
                player.sendMessage(NOT_SAME_EDITOR_INSTANCE);
                return;
            }

            if (editInstance.hasSameUUID(newOwner.getUuid())) {
                player.sendMessage("Same player");
                return;
            }

            editInstance.setOwner(newOwner);
            newOwner.sendMessage(NO_INSTANCE_OWNER);

            return;
        }

        player.sendMessage(Messages.NO_PERMISSION);

    }
}


