package net.theevilreaper.dungeon;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.player.*;
import net.theevilreaper.aves.map.MapProvider;
import net.theevilreaper.aves.util.functional.PlayerConsumer;
import net.theevilreaper.dungeon.commands.LockCommand;
import net.theevilreaper.dungeon.commands.PositionCommand;
import net.theevilreaper.dungeon.commands.ResetCommand;
import net.theevilreaper.dungeon.commands.TestCommand;
import net.theevilreaper.dungeon.commands.ToolCommand;
import net.theevilreaper.dungeon.commands.TransferCommand;
import net.theevilreaper.dungeon.data.floor.FloorProvider;
import net.theevilreaper.dungeon.database.MongoDatabase;
import net.theevilreaper.dungeon.event.FloorCreateEvent;
import net.theevilreaper.dungeon.event.FloorRemoveEvent;
import net.theevilreaper.dungeon.instance.EditInstance;
import net.theevilreaper.dungeon.instance.EditInstanceManager;
import net.theevilreaper.dungeon.inventory.floor.FloorInventory;
import net.theevilreaper.dungeon.inventory.region.RegionInventory;
import net.theevilreaper.dungeon.inventory.creator.FloorCreateService;
import net.theevilreaper.dungeon.listener.configuration.AsyncPlayerConfigurationListener;
import net.theevilreaper.dungeon.listener.floor.FloorCreateListener;
import net.theevilreaper.dungeon.listener.floor.FloorRemoveListener;
import net.theevilreaper.dungeon.listener.instance.EntityAddToInstanceListener;
import net.theevilreaper.dungeon.listener.instance.EntityRemoveFromInstanceListener;
import net.theevilreaper.dungeon.listener.player.PlayerDeathListener;
import net.theevilreaper.dungeon.map.EditorMapProvider;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.listener.*;
import net.theevilreaper.dungeon.sidebar.SidebarViewer;
import net.theevilreaper.kali.common.ListenerHandling;
import net.theevilreaper.kali.common.gson.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class DungeonEditor implements ListenerHandling {

    private static final String DATABASE_FILE = "database.json";
    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonEditor.class);
    private FloorProvider floorProvider;
    private FloorInventory floorInventory;
    private final EditInstanceManager editInstanceManager;
    private final Items items;
    private final RegionInventory regionInventory;
    private final SidebarViewer sidebarViewer;
    private final MapProvider mapProvider;
    private MongoDatabase mongoDatabase;

    private final Consumer<EditInstance> containerConsumer;

    private FloorCreateService floorCreateService;

    public DungeonEditor() {
        this.editInstanceManager = new EditInstanceManager();
        this.items = new Items();
        this.mapProvider = new EditorMapProvider();
        this.containerConsumer = instanceContainer -> {
            instanceContainer.setLocked(true);
            if (!instanceContainer.getPlayers().isEmpty()) {
                for (Player player : instanceContainer.getPlayers()) {
                    this.mapProvider.teleportToSpawn(player, true);
                    items.setFloorItem(player);
                }
            }
            if (instanceContainer.getOwner() != null) {
                editInstanceManager.remove(instanceContainer);
            }
        };

        this.regionInventory = new RegionInventory();
        this.sidebarViewer = new SidebarViewer();
    }

    public void initialize() {
        this.initDirectories();
        JsonObject databaseObject = null;

        try (JsonReader reader = new JsonReader(Files.newBufferedReader(Paths.get("").resolve(DATABASE_FILE)))) {
            databaseObject = GsonUtil.DEFAULT_GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            LOGGER.error("Failed to read the database file", e);
        }

        if (databaseObject != null) {
            this.mongoDatabase = new MongoDatabase(databaseObject);
        } else {
            LOGGER.error("Failed to read the database file. The editor will not work without a database.");
            return;
        }
        this.floorProvider = new FloorProvider(mongoDatabase);
        this.floorCreateService = new FloorCreateService();
        this.floorInventory = new FloorInventory(editInstanceManager, floorProvider, floorCreateService, containerConsumer);
        this.registerEvents();
        this.registerCommands();
        this.registerCancelListener(MinecraftServer.getGlobalEventHandler());
        LOGGER.info("Successfully loaded the editor extension for the dungeon");

        MinecraftServer.getSchedulerManager().buildShutdownTask(this::terminate);
    }

    public void terminate() {
        if (this.mongoDatabase != null) {
            this.mongoDatabase.disconnect();
            LOGGER.info("Disconnecting from the database!");
        }
    }

    private void initDirectories() {
        Path path = Paths.get("");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        var roomsFolder = path.resolve("rooms");

        if (!Files.exists(roomsFolder)) {
            try {
                Files.createDirectories(roomsFolder);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Registers some listener as global listener into the server event node.
     */
    private void registerEvents() {
        GlobalEventHandler node = MinecraftServer.getGlobalEventHandler();
        node.addListener(AsyncPlayerConfigurationEvent.class, new AsyncPlayerConfigurationListener(
                this.mapProvider.getActiveInstance()
        ));

        PlayerConsumer teleportToSpawn = player -> this.mapProvider.teleportToSpawn(player, true);

        node.addListener(PlayerSpawnEvent.class, new PlayerSpawnListener(teleportToSpawn, sidebarViewer, items));
        node.addListener(PlayerBlockBreakEvent.class, new BlockBreakListener(regionInventory));
        node.addListener(PlayerChatEvent.class, new PlayerChatListener());

        node.addListener(AddEntityToInstanceEvent.class, new EntityAddToInstanceListener(this.items));
        node.addListener(RemoveEntityFromInstanceEvent.class, new EntityRemoveFromInstanceListener());

        node.addListener(PlayerDeathEvent.class, new PlayerDeathListener());
        node.addListener(PlayerDisconnectEvent.class, new PlayerDisconnectListener(this.sidebarViewer,this.floorCreateService));
        node.addListener(PlayerUseItemEvent.class, new ItemListener(floorInventory, teleportToSpawn));
        node.addListener(PlayerBlockInteractEvent.class, new BlockInteractListener(regionInventory));

        node.addListener(FloorCreateEvent.class, new FloorCreateListener(this.floorProvider, this.floorInventory));
        node.addListener(FloorRemoveEvent.class, new FloorRemoveListener(this.floorProvider, this.floorInventory));
    }

    /**
     * Registers the commands for the editor.
     */
    private void registerCommands() {
        var commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new ToolCommand(this.items));
        commandManager.register(new TransferCommand());
        commandManager.register(new LockCommand());
        commandManager.register(new TestCommand(regionInventory));
        commandManager.register(new ResetCommand());
        commandManager.register(new PositionCommand(this.mapProvider));
    }
}
