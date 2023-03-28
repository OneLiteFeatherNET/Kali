package net.theevilreaper.dungeon.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Messages {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final Component PREFIX =
            MINI_MESSAGE.deserialize("<gradient:#ffff0a:#40f9ff>Editor</gradient> ");

    public static final Component NO_NUMBER =
            buildPrefixedComponent(Component.text("The input is not a number", NamedTextColor.RED));

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

    public static final Component NO_PLAYER =
            PREFIX.append(Component.text("The command works only for players", NamedTextColor.RED));

    public static final Component ERROR_FLOOR_DELETE =
            PREFIX.append(Component.text("It seems that the floor doesn't exists anymore", NamedTextColor.RED));

    public static final Component NO_INSTANCE_OWNER =
            PREFIX.append(Component.text(
                    "You can not execute this command because you are not the owner of this instance!",
                    NamedTextColor.RED
            ));

    public static final Component CANT_TRANSFER_YOURSELF =
            PREFIX.append(Component.text("You already own the instance", NamedTextColor.RED));

    public static final Component NOT_SAME_EDITOR_INSTANCE =
            Messages.PREFIX.append(Component.text(
                    "Can't set the owner because the target is not on the same instance!",
                    NamedTextColor.RED
            ));

    public static final Component ABORT_FLOOR_CREATION =
            Messages.PREFIX.append(Component.text(
                    "Unable to create floor because the name is missing", NamedTextColor.RED
            ));

    public static final List<Component> FILTER_LORE = new ArrayList<>();
    public static final List<Component> FLOOR_LORE = new ArrayList<>();

    static {
        FILTER_LORE.add(Component.empty());
        FILTER_LORE.add(Component.text("Click left to apply a filter"));
        FILTER_LORE.add(Component.empty());
        FILTER_LORE.add(Component.text("To remove the current filter please make a right click on the item"));

        FLOOR_LORE.add(Component.empty());
        FLOOR_LORE.add(Component.text("LeftClick: ", NamedTextColor.GRAY)
                .append(Component.text("Edit").style(Style.style().decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN))));
        FLOOR_LORE.add(Component.empty());
        FLOOR_LORE.add(Component.text("RightClick: ", NamedTextColor.GRAY)
                        .append(Component.text("Delete").style(Style.style().decorate(TextDecoration.BOLD).color(NamedTextColor.RED))));
        FLOOR_LORE.add(Component.empty());
    }

    private Messages() {}

    public static @NotNull Component buildPrefixedComponent(@NotNull Component component) {
        return PREFIX.append(component);
    }
}
