package net.theevilreaper.dungeon.util;

import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;
import net.theevilreaper.dungeon.sidebar.Rank;
import org.jetbrains.annotations.NotNull;

/**
 * The class includes all permission objects from the editor.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class Permissions {

    public static final Permission DELETE_FLOOR_PERMISSION = new Permission("editor.floor.delete");
    public static final Permission CREATE_FLOOR_PERMISSION = new Permission("editor.floor.create");

    private Permissions() {}

    /**
     * Sets some permissions depends on the rank from the player.
     * @param player the player to add the permissions
     * @param rank the rank from the player
     */
    public static void setPermission(@NotNull Player player, @NotNull Rank rank) {
        if (rank == Rank.ADMIN) {
            player.addPermission(DELETE_FLOOR_PERMISSION);
            player.addPermission(CREATE_FLOOR_PERMISSION);
        }
    }

    /**
     * Removes permissions from a player.
     * @param player the player to remove the permissions
     */
    public static void remove(@NotNull Player player) {
        player.removePermission(DELETE_FLOOR_PERMISSION);
        player.removePermission(CREATE_FLOOR_PERMISSION);
    }
}
