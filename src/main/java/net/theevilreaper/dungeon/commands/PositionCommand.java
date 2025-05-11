package net.theevilreaper.dungeon.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.theevilreaper.aves.map.MapProvider;
import org.jetbrains.annotations.NotNull;

import static net.theevilreaper.dungeon.util.Messages.buildPrefixedComponent;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class PositionCommand extends Command {

    private final Component defaultComponent =  buildPrefixedComponent(Component.text("Please use: ", NamedTextColor.GRAY)
            .append(Component.text("/pos <spawn, save>", NamedTextColor.RED)));

    private final MapProvider provider;

    public PositionCommand(@NotNull MapProvider provider) {
        super("position", "pos");

        this.provider = provider;

        setCondition(Conditions::playerOnly);
        setDefaultExecutor((sender, context) -> sender.sendMessage(defaultComponent));

        var typeArg = ArgumentType.Word("type").from("spawn", "save");
        addSyntax(this::handleCommand, typeArg);
    }

    private void handleCommand(@NotNull CommandSender sender, @NotNull CommandContext context) {
        var player = (Player) sender;
        var argument = (String) context.get("type");

        //TODO: Fix me
       /* if ("save".equals(argument) && this.provider.saveMap()) {
            player.sendMessage(buildPrefixedComponent(Component.text("Successfully saved the map", NamedTextColor.RED)));
            return;
        }

        if ("spawn".equals(argument)) {
            this.provider.setSpawn(player.getPosition());
            player.sendMessage(buildPrefixedComponent(Component.text("Set spawn pos", NamedTextColor.RED)));
        }*/
    }
}
