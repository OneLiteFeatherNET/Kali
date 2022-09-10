package net.theevilreaper.dungeon.commands;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.entity.Player;
import net.theevilreaper.dungeon.inventory.RegionInventory;
import org.jetbrains.annotations.NotNull;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class TestCommand extends Command {

    private final RegionInventory regionInventory;

    public TestCommand(@NotNull RegionInventory regionInventory) {
        super("tt");
        this.regionInventory = regionInventory;

        var argument = new ArgumentWord("test").from("region");

        addSyntax(this::onCommand, argument);
    }

    private void onCommand(@NotNull CommandSender commandSender, @NotNull CommandContext context) {
        var player = (Player) commandSender;
        String argument = context.get("test");

        if ("region".contains(argument)) {
            this.regionInventory.open(player);
        }
    }
}
