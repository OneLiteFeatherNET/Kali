package net.theevilreaper.dungeon.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.theevilreaper.dungeon.util.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link SpeedCommand} allows player to change his flying or movement speed.
 * The command changes accordingly if the player flies or not the corresponding variable for the speed
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class SpeedCommand extends Command {

    private static final Component VALUE_IS_TO_HIGH =
            Messages.PREFIX.append(Component.text("The given speed is to high. The maximum is 10", NamedTextColor.RED));
    private static final Component RESET_TO_DEFAULT =
            Messages.PREFIX.append(Component.text("Speed has been reset to default", NamedTextColor.GREEN));
    private static final float DEFAULT_SPEED = 0.1f;
    private static final float DEFAULT_FLY_SPEED = 0.05f;
    private static final String COMMAND_NAME = "speed";

    /**
     * Creates a new instance from the command class.
     */
    public SpeedCommand() {
        super(COMMAND_NAME, "s");
        setCondition(Conditions::playerOnly);

        var speedAttribute = new ArgumentFloat(COMMAND_NAME);
        addSyntax(this::onReset);
        addSyntax(this::onSelf, speedAttribute);
    }

    /**
     * Executes the reset of the speed values.
     * @param sender the sender who executes the command
     * @param context the involved context
     */
    public void onReset(@NotNull CommandSender sender, @NotNull CommandContext context) {
        resetSpeed((Player) sender);
    }

    /**
     * Executes the logic to change the speed value for the executor of the command.
     * @param sender the sender who executes the command
     * @param context the involved context
     */
    public void onSelf(@NotNull CommandSender sender, @NotNull CommandContext context) {
        var player = (Player) sender;

        float playerSpeed = context.get(COMMAND_NAME);

        if (playerSpeed > 10) {
            player.sendMessage(VALUE_IS_TO_HIGH);
            return;
        }

        playerSpeed = playerSpeed / 10;
        updateInternalSpeedValue(player, playerSpeed);
        player.sendMessage(Messages.PREFIX.append(Component.text("Changed speed value")));
    }

    /**
     * Resets the walking or fly speed back to the default values.
     * @param player the player to reset the value
     */
    private void resetSpeed(@NotNull Player player) {
        updateInternalSpeedValue(player, player.isFlying() ? DEFAULT_FLY_SPEED : DEFAULT_SPEED);
        player.sendMessage(RESET_TO_DEFAULT);
    }

    /**
     * Updates the internal data value for the fly or movement speed.
     * @param player the player to change the value
     * @param speedValue the speed value to set
     */
    private void updateInternalSpeedValue(@NotNull Player player, float speedValue) {
        if (player.isFlying()) {
            player.setFlyingSpeed(speedValue);
        } else {
            player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speedValue);
        }
    }
}
