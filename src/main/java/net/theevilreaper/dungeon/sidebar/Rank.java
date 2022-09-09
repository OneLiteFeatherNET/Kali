package net.theevilreaper.dungeon.sidebar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public enum Rank {

    ADMIN("Admin", TextColor.fromHexString("#fb071e"), Component.text("Admin").append(Component.space()), NamedTextColor.RED),
    BUILDER("Content", TextColor.fromHexString("#40fbfd"), Component.text("Content").append(Component.space()), NamedTextColor.YELLOW),
    REVIEWER("Viewer", TextColor.fromHexString("#7aee18"), Component.text("Viewer").append(Component.space()), NamedTextColor.GREEN);

    private static final Rank[] VALUES = values();

    private final String name;

    private final TextColor textColor;

    private final Component prefix;
    private final NamedTextColor teamColor;

    Rank(@NotNull String name, @NotNull TextColor textColor, @NotNull Component prefix, @NotNull NamedTextColor teamColor) {
        this.name = name;
        this.textColor = textColor;
        this.prefix = prefix;
        this.teamColor = teamColor;
    }

    /**
     * Returns the name from the rank entry.
     * @return the name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Returns the given {@link TextColor} from the rank
     * @return the given color
     */
    public TextColor getTextColor() {
        return textColor;
    }

    /**
     * Returns the {@link Component} which represents the prefix.
     * @return the given prefix
     */
    @NotNull
    public Component getPrefix() {
        return prefix;
    }

    /**
     * Returns the {@link NamedTextColor} which represents the team color.
     * @return the given team color
     */
    @NotNull
    public NamedTextColor getTeamColor() {
        return teamColor;
    }

    /**
     * Getter for the underlying value cache.
     * @return the cache value
     */
    public static Rank[] getValues() {
        return VALUES;
    }
}
