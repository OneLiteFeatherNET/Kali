package net.theevilreaper.dungeon.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.theevilreaper.dungeon.instance.EditInstance;
import org.jetbrains.annotations.NotNull;

/**
 * The commands have the functionality to reset the countdown from an {@link EditInstance}.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class ResetCommand extends Command {

    public ResetCommand() {
        super("reset");
        setCondition(Conditions::playerOnly);
        this.addSyntax(this::onCommand);
    }

    /**
     * Handles the reset functionality for the command.
     * @param sender the command sender for the command
     * @param context the context from the command
     */
    private void onCommand(@NotNull CommandSender sender, @NotNull CommandContext context) {
        var player = (Player) sender;

        if (!(player.getInstance() instanceof EditInstance editInstance)) return;

        if (!player.hasTag(EditInstance.RESET_TAG)) return;


        editInstance.resetCounter();
        player.removeTag(EditInstance.RESET_TAG);
    }
}
