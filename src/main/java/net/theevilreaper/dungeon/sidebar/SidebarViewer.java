package net.theevilreaper.dungeon.sidebar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.TeamManager;
import net.theevilreaper.dungeon.DungeonEditor;
import net.theevilreaper.dungeon.util.Messages;
import net.theevilreaper.dungeon.util.Tags;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class SidebarViewer {

    private static final int GAME_MODE_SWITCHER_LEVEL = 2;
    private static final Component RANK_SPACE = Component.text("⍿ ", NamedTextColor.DARK_GRAY);
    private static final Logger LOGGER = LoggerFactory.getLogger(SidebarViewer.class);
    private static final TeamManager TEAM_MANAGER = MinecraftServer.getTeamManager();
    private static final Component ARROW_COMPONENT = Component.text(" ➟ ", NamedTextColor.GRAY);
    private static final Component TAB_HEADER =
            Messages.withMini("<color:#66ff66><bold>┏                                              <color:#ff6600><bold>┒\n")
                    .append(Component.text("» ").style(Style.style(NamedTextColor.DARK_GRAY, TextDecoration.BOLD)))
                    .append(Component.text("Dungeon Editor"))
                    .append(Component.text(" «").style(Style.style(NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.text("\n"));
    private static final Component TAB_FOOTER = Component.text("\n")
            .append(Component.text("Eat", NamedTextColor.YELLOW)
                    .append(ARROW_COMPONENT)
                    .append(Component.text("sleep", NamedTextColor.AQUA))
                    .append(ARROW_COMPONENT)
                    .append(Component.text("rave", NamedTextColor.WHITE))
                    .append(ARROW_COMPONENT)
                    .append(Component.text("repeat", NamedTextColor.RED))
            ).append(Component.text("\n"))
            .append(Messages.withMini(("<color:#ff6600><bold>┖                                              <color:#66ff66><bold>┛")));

    @Deprecated(forRemoval = true)
    private static final UUID REAPER_UUID = UUID.fromString("6e2f3944-96d2-4e01-9056-dcab7f3937a4");
    @Deprecated(forRemoval = true)
    private static final UUID SEELE_UUID = UUID.fromString("e1dff87a-1059-47f1-bc7d-533f072bf8c7");

    public SidebarViewer() {
        this.initTeams();
    }

    private void initTeams() {
        for (int i = 0; i < Rank.getValues().length; i++) {
            var rank = Rank.getValues()[i];
            var team = TEAM_MANAGER.createTeam(rank.getName(), rank.getPrefix(), rank.getTeamColor(), Component.empty());
            team.setCollisionRule(TeamsPacket.CollisionRule.NEVER);
        }
    }

    public void add(@NotNull Player player) {
        int id = 2;
        if (player.getUuid().equals(REAPER_UUID)) {
            id = 0;
        } else if (player.getUuid().equals(SEELE_UUID)) {
            id = 1;
        }

        var rank = Rank.getValues()[id];

        if (rank == null) {
            LOGGER.warn("Found invalid rank. Falling back to default");
            rank = Rank.REVIEWER;
        }

        if (rank != Rank.REVIEWER) {
            player.setPermissionLevel(GAME_MODE_SWITCHER_LEVEL);
        }

        TEAM_MANAGER.getTeam(rank.getName()).addMember(player.getUsername());
        player.setTag(Tags.NAME_TAG, id);

        var tag = rank.getPrefix().color(rank.getTextColor()).append(RANK_SPACE).append(player.getName().color(TextColor.fromHexString("#FFFAFA")));
        player.setDisplayName(tag);

        player.sendPlayerListHeaderAndFooter(TAB_HEADER, TAB_FOOTER);
    }

    public void remove(@NotNull Player player) {
        player.removeTag(Tags.NAME_TAG);
    }
}
