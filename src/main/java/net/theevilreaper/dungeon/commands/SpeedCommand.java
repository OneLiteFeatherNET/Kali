package net.theevilreaper.dungeon.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.theevilreaper.dungeon.util.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class SpeedCommand extends Command {

    private static final Component VALUE_IS_TO_HIGH =
            Messages.PREFIX.append(Component.text("The given speed is to high. The maximum is 10", NamedTextColor.RED));

    private static final Component NO_NUMBER =
            Messages.PREFIX.append(Component.text("The given value is not a number", NamedTextColor.RED));

    private static final Component RESET_TO_DEFAULT =
            Messages.PREFIX.append(Component.text("Speed has been reset to default", NamedTextColor.GREEN));

    private static final float DEFAULT_SPEED = 0.25f;
    private static final float DEFAULT_FLY_SPEED = 0.4F;

    public SpeedCommand() {
        super("speed", "s");
        setCondition(Conditions::playerOnly);

        var speedAttribute = new ArgumentString("speed");
        addSyntax(this::onSelf, speedAttribute);
    }

    public void onSelf(@NotNull CommandSender sender, @NotNull CommandContext context) {
        var player = (Player) sender;

        float playerSpeed = 0;
        try {
            playerSpeed = Float.parseFloat(context.get("speed").toString());
        } catch (NumberFormatException exception) {
            player.sendMessage(NO_NUMBER);
            return;
        }

        boolean onGround = player.isFlying();

        var speed = onGround ? DEFAULT_FLY_SPEED : DEFAULT_SPEED;

        if (playerSpeed == 0) {
            if (onGround) {
                player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speed);
            } else {
                player.getAttribute(Attribute.FLYING_SPEED).setBaseValue(speed);
            }
            player.sendMessage(RESET_TO_DEFAULT);
            return;
        }

        if (playerSpeed > 10) {
            player.sendMessage(VALUE_IS_TO_HIGH);
            return;
        }

        speed = playerSpeed / 10;

        if (onGround) {
            player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speed);
        } else {
            player.getAttribute(Attribute.FLYING_SPEED).setBaseValue(speed);
        }
        player.sendMessage(Messages.PREFIX.append(Component.text("Changed speed value")));
    }
}
