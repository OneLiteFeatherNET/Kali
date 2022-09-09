package net.theevilreaper.dungeon.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.items.Items;
import org.jetbrains.annotations.NotNull;

import static net.theevilreaper.dungeon.util.Messages.NO_EDIT_INSTANCE;

/**
 * Represents the tool which gives the player to tool item to set up regions.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class ToolCommand extends Command {

    private final Items items;

    public ToolCommand(@NotNull Items items) {
        super("tool", "tl");

        this.items = items;

        setCondition(Conditions::playerOnly);
        addSyntax(this::onTool);
    }

    /**
     * Handles what happen if the player executes this command.
     * @param sender the command sender for the command
     * @param context the context from the command
     */
    private void onTool(@NotNull CommandSender sender, @NotNull CommandContext context) {
        var player = (Player) sender;

        if (!(player.getInstance() instanceof EditInstance)) {
            player.sendMessage(NO_EDIT_INSTANCE);
            return;
        }

        this.items.addRegionTool(player);
    }
}
