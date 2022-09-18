package net.theevilreaper.dungeon.instance;

import com.google.gson.Gson;
import de.icevizion.aves.file.GsonFileHandler;
import de.icevizion.aves.util.Strings;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.packet.server.play.TimeUpdatePacket;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.PacketUtils;
import net.theevilreaper.dungeon.DungeonEditor;
import net.theevilreaper.dungeon.data.room.AbstractRoom;
import net.theevilreaper.dungeon.util.KaliDimension;
import net.theevilreaper.dungeon.util.Messages;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class EditInstance extends InstanceContainer {

    public static final Tag<Byte> RESET_TAG = Tag.Byte("reset");
    private static final Component RESET_COMPONENT = Messages.PREFIX.append(
            LegacyComponentSerializer.legacySection().deserialize("§cThis instance will be deleted in §65 §cMinutes ")
                    .append(Component.text("[", NamedTextColor.GRAY)
                            .append(Component.text("Reset").color(TextColor.fromHexString("#99ff33"))))
                            .append(Component.text("]", NamedTextColor.GRAY))
                            .clickEvent(ClickEvent.runCommand("/reset")));
    private static final Component SELECT_PREFIX = Component.text("[", NamedTextColor.GRAY)
            .append(Component.text("!").color(TextColor.fromHexString("#F8FF12"))).append(Component.text("] ", NamedTextColor.GRAY));
    private static final Component FIRST_POS = SELECT_PREFIX.append(Component.text("First position: ", NamedTextColor.WHITE));
    private static final Component SECOND_POS = SELECT_PREFIX.append(Component.text("Second position: ", NamedTextColor.WHITE));
    private static final long MAX_TIME = 6_000;
    private static final long START_TIME = 5_000;
    private static final long SECONDS_AS_LONG = 1000;
    private static final int MAXIMUM_ALIVE = 1800;
    private final BossBar bossBar;
    private Path originPath;
    private final GsonFileHandler fileHandler;
    private Player owner;

    private int currentCounter;
    private long nextTick;
    private long nextResetMessageTick;
    private boolean locked;
    private Point firstPos;
    private Point secondPos;
    private AbstractRoom abstractRoom;
    private AnvilLoader anvilLoader;
    private boolean send;
    private final Consumer<EditInstance> containerConsumer;

    public EditInstance(@NotNull Consumer<EditInstance> containerConsumer) {
        super(UUID.randomUUID(), KaliDimension.KALI_DIMENSION);
        //TODO: Add a factory to provide this path
        this.originPath = DungeonEditor.ROOT_PATH.resolve("rooms");
        //TODO: Provide one single gson file writer to write the or read the room structure
        this.fileHandler = new GsonFileHandler(new Gson());
        this.bossBar = BossBar.bossBar(Component.empty(), BossBar.MAX_PROGRESS, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        this.currentCounter = MAXIMUM_ALIVE;
        this.nextResetMessageTick = System.currentTimeMillis() + (SECONDS_AS_LONG * 1560);
        this.calculateNextTick();
        this.setTime(START_TIME);
        this.containerConsumer = containerConsumer;
        //TODO: Rework the anvilloader binding
        /*

        var path = this.originPath.resolve("test_room");
        this.anvilLoader = new AnvilLoader(Path.of(path.toString(), "test_room"));

        setChunkLoader(new AnvilLoader(path));*/
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        if (getTime() > MAX_TIME) {
            setTime(START_TIME);
            PacketUtils.sendGroupedPacket(this.getPlayers(), new TimeUpdatePacket(getWorldAge(), getTime()));
        }

        var timestamp = System.currentTimeMillis();

        if (timestamp > nextTick) {
            currentCounter--;
            updateTitle();
            this.calculateNextTick();
        }

        if (timestamp > nextResetMessageTick && !send) {
            owner.sendMessage(RESET_COMPONENT);
            owner.setTag(RESET_TAG, (byte)1);
            send = true;
        }

        if (currentCounter == 0) {
            this.containerConsumer.accept(this);
        }
    }

    public boolean setFirstPos(@NotNull Point firstPos) {
        this.firstPos = firstPos;
        owner.sendMessage(FIRST_POS.append(transformPos(firstPos)));
        return this.createRoom();
    }

    public boolean setSecondPos(@NotNull Point secondPos) {
        this.secondPos = secondPos;
        owner.sendMessage(SECOND_POS.append(transformPos(secondPos)));
        return this.createRoom();
    }

    public void setRegionType() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean createRoom() {
        if (this.firstPos == null || this.secondPos == null) {
            return false;
        }

        this.secondPos = null;
        this.firstPos = null;
        return true;
    }

    public void saveRegion() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setOwner(@NotNull Player owner) {
        this.owner = owner;
    }

    public void switchOwner() {
        Player newOwner = null;
        if (getPlayers().size() - 1 != 0) {
            var iterator = getPlayers().iterator();
            while(iterator.hasNext() && newOwner == null) {
                var nextPlayer = iterator.next();
                if (owner.getUuid().equals(nextPlayer.getUuid())) continue;
                newOwner = nextPlayer;
            }
        }
        this.owner = newOwner;
    }

    /**
     * Calculates the next update tick for the bossbar timer.
     */
    private void calculateNextTick() {
        this.nextTick = System.currentTimeMillis() + SECONDS_AS_LONG;
    }

    /**
     * Resets the counter to the start value.
     */
    public void resetCounter() {
        this.currentCounter = MAXIMUM_ALIVE;
        this.nextResetMessageTick = System.currentTimeMillis() + (SECONDS_AS_LONG * 1560);
        send = false;
        this.updateTitle();
    }

    @Contract("_ -> new")
    private @NotNull Component transformPos(@NotNull Point pos) {
        return Component.text("(" + pos.blockX() + ", " + pos.blockX() + ", " + pos.blockZ() + ")", NamedTextColor.GRAY);
    }

    /**
     * Updates the bossbar title with the current second counter in the format MM:SS.
     */
    public void updateTitle() {
        this.bossBar.name(Component
                .text("Instance will be deleted in ", NamedTextColor.RED).append(
                        Component.text(Strings.getTimeString(currentCounter), NamedTextColor.YELLOW)));
    }

    /**
     * Updates the locked variable from the instance.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Add a player to the instance.
     * @param player the player to add
     */
    public void add(@NotNull Player player) {
        player.showBossBar(bossBar);
    }

    /**
     * Remove a player from the instance.
     * @param player the player to remove
     */
    public void remove(@NotNull Player player) {
        player.hideBossBar(bossBar);
    }

    /**
     * Checks if a player can join into the instance if they instance is locked.
     * @param player The player to check
     * @return True if the player can join otherwise false
     */
    public boolean canJoin(@NotNull Player player) {
        if (!isLocked()) return true;
        return isLocked();
    }

    /**
     * Checks if a given uuid is equal to the uuid of the owning player.
     * @param uuid The uuid to check
     * @return True if the uuid's are equal otherwise false
     */
    public boolean hasSameUUID(@NotNull UUID uuid) {
        return owner != null && owner.getUuid().equals(uuid);
    }

    /**
     * Returns the owning player of the instance.
     * @return the player who owns the instance
     */
    @Nullable
    public Player getOwner() {
        return owner;
    }

    /**
     * Returns if the instance is locked or not
     * @return True if locked otherwise false
     */
    public boolean isLocked() {
        return locked;
    }
}
