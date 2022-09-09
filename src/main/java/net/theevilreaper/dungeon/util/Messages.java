package net.theevilreaper.dungeon.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Messages {

    public static final Component PREFIX =
            LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "§7[§eEditor§7] "
            );

    public static final Component LOCKED_INSTANCE =
            PREFIX.append(Component.text("The instance is locked. Abort teleportation", NamedTextColor.RED));


    public static final Component NO_EDIT_INSTANCE =
            PREFIX.append(Component.text("You are not on a edit instance!"));

    public static final Component FLOOR_NOT_FOUND = PREFIX
            .append(Component.text("The clicked floor does not exist anymore", NamedTextColor.RED));
    public static final Component ERROR = PREFIX.append(Component.text("An error occurred", NamedTextColor.RED));

    public static final Component NO_PERMISSION =
            PREFIX.append(Component.text("You don't have the permission to execute the command", NamedTextColor.RED));

    public static final Component INSTANCE_NOW_LOCKED = PREFIX
            .append(Component.text("The instance is now ", NamedTextColor.GRAY))
            .append(Component.text("locked", NamedTextColor.RED));

    public static final Component INSTANCE_NOW_UNLOCKED = PREFIX
            .append(Component.text("The instance is now ", NamedTextColor.GRAY))
            .append(Component.text("unlocked", NamedTextColor.GREEN));

    public static final Component PLAYER_NOT_FOUND =
            PREFIX.append(Component.text(
                    "The specific player is not online!",
                    NamedTextColor.RED
            ));

    public static final Component NO_INSTANCE_OWNER =
            PREFIX.append(Component.text(
                    "You can not execute this command because you are not the owner of this instance!",
                    NamedTextColor.RED
            ));

    private Messages() {}
}
