package net.theevilreaper.dungeon;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.instance.block.BlockManager;
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
import net.theevilreaper.dungeon.inventory.region.search.PlayerSearchChangeEvent;
import net.theevilreaper.dungeon.listener.configuration.AsyncPlayerConfigurationListener;
import net.theevilreaper.dungeon.map.EditorMapProvider;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.listener.*;
import net.theevilreaper.dungeon.sidebar.SidebarViewer;
import net.theevilreaper.dungeon.util.Messages;
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
public class DungeonEditor {

    private static final String DATABASE_FILE = "database.json";
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
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
        this.registerBlockHandlers();
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

    private void registerBlockHandlers() {
        BlockManager blockManager = MinecraftServer.getBlockManager();
        /*blockManager.registerHandler("minecraft:skull", SkullHandler::new);
        blockManager.registerHandler("minecraft:sign", SignHandler::new);
        blockManager.registerHandler("minecraft:banner", BannerHandler::new);
        blockManager.registerHandler("minecraft:beacon", BeaconHandler::new);*/
    }

    /**
     * Registers some listener as global listener into the server event node.
     */
    private void registerEvents() {
        Consumer<CancellableEvent> cancelConsumer = event -> event.setCancelled(true);
        var node = MinecraftServer.getGlobalEventHandler();
        node.addListener(AsyncPlayerConfigurationEvent.class, new AsyncPlayerConfigurationListener(
                this.mapProvider.getActiveInstance()
        ));

        PlayerConsumer teleportToSpawn = player -> {
            this.mapProvider.teleportToSpawn(player, true);
        };

        node.addListener(PlayerSpawnEvent.class, new PlayerSpawnListener(teleportToSpawn, sidebarViewer, items));
        node.addListener(PlayerBlockBreakEvent.class, new BlockBreakListener(regionInventory));
        node.addListener(PlayerChatEvent.class, new PlayerChatListener());
        node.addListener(PlayerBlockPlaceEvent.class, cancelConsumer::accept);

        node.addListener(AddEntityToInstanceEvent.class, event -> {
           if (event.getInstance() instanceof EditInstance editInstance && event.getEntity() instanceof Player player) {
               editInstance.add(player);
               this.items.setEditItems(player);
           }
        });

        node.addListener(RemoveEntityFromInstanceEvent.class, event -> {
            if (event.getInstance() instanceof EditInstance editInstance && event.getEntity() instanceof Player player) {
                editInstance.remove(player);
                player.getInventory().clear();
                editInstance.switchOwner();
            }
        });

        node.addListener(ItemDropEvent.class, cancelConsumer::accept);
        node.addListener(PickupItemEvent.class, cancelConsumer::accept);
        node.addListener(PlayerDeathEvent.class, event -> event.setChatMessage(Component.empty()));
        node.addListener(PlayerDisconnectEvent.class, new PlayerDisconnectListener(this.sidebarViewer,this.floorCreateService));
        node.addListener(PlayerUseItemEvent.class, new ItemListener(floorInventory, teleportToSpawn));
        node.addListener(PlayerBlockInteractEvent.class, new BlockInteractListener(regionInventory));

        node.addListener(FloorCreateEvent.class, event -> {
            if (!event.getFloor().hasName()) {
                event.getPlayer().sendMessage(Messages.ABORT_FLOOR_CREATION);
                return;
            }
            this.floorProvider.addFloor(event.getFloor());
            this.floorInventory.updateInventoryLayout();
        });

        node.addListener(FloorRemoveEvent.class, floorRemoveEvent -> {
            this.floorProvider.removeFloor(floorRemoveEvent.getFloor());
            this.floorInventory.updateInventoryLayout();
        });

        node.addListener(PlayerSearchChangeEvent.class, new SearchChangeListener());
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
