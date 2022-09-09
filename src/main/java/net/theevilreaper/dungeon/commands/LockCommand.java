package net.theevilreaper.dungeon.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.util.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class LockCommand extends Command {

    public LockCommand() {
        super("lock", "l");
        setCondition(Conditions::playerOnly);
        addSyntax(this::onCommand);
    }

    private void onCommand(@NotNull CommandSender sender, @NotNull CommandContext context) {
        var player = (Player) sender;

        if (!(player.getInstance() instanceof EditInstance editInstance) || !editInstance.hasSameUUID(player.getUuid())) {
            player.sendMessage(Messages.NO_PERMISSION);
            return;
        }

        editInstance.setLocked(!editInstance.isLocked());

        if (editInstance.isLocked()) {
            player.sendMessage(Messages.INSTANCE_NOW_LOCKED);
            return;
        }

        player.sendMessage(Messages.INSTANCE_NOW_UNLOCKED);
    }
}
