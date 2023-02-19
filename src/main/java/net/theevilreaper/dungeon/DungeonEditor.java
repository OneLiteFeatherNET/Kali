package net.theevilreaper.dungeon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.icevizion.aves.file.GsonFileHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.instance.AddEntityToInstanceEvent;
import net.minestom.server.event.instance.RemoveEntityFromInstanceEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.BlockManager;
import net.theevilreaper.canis.BannerHandler;
import net.theevilreaper.canis.BeaconHandler;
import net.theevilreaper.canis.SignHandler;
import net.theevilreaper.canis.SkullHandler;
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
import net.theevilreaper.dungeon.inventory.FloorInventory;
import net.theevilreaper.dungeon.inventory.RegionInventory;
import net.theevilreaper.dungeon.inventory.creator.FloorCreateService;
import net.theevilreaper.dungeon.util.Items;
import net.theevilreaper.dungeon.listener.*;
import net.theevilreaper.dungeon.location.LocationProvider;
import net.theevilreaper.dungeon.sidebar.SidebarViewer;
import net.theevilreaper.dungeon.util.KaliDimension;
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
public class DungeonEditor extends Extension {

    private static final String DATABASE_FILE = "database.json";
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonEditor.class);
    public static final Path ROOT_PATH = Paths.get("");
    private FloorProvider floorProvider;
    private FloorInventory floorInventory;
    private final EditInstanceManager editInstanceManager;
    private final Items items;
    private final LocationProvider locationProvider;
    private final RegionInventory regionInventory;
    private final SidebarViewer sidebarViewer;
    private MongoDatabase mongoDatabase;

    private Instance defaultInstance;

    private final Consumer<EditInstance> containerConsumer;

    private FloorCreateService floorCreateService;

    public DungeonEditor() {
        this.editInstanceManager = new EditInstanceManager();
        this.items = new Items();
        this.locationProvider = new LocationProvider(ROOT_PATH);
        this.locationProvider.load();
        this.containerConsumer = instanceContainer -> {
            instanceContainer.setLocked(true);
            if (!instanceContainer.getPlayers().isEmpty()) {
                for (Player player : instanceContainer.getPlayers()) {
                    player.setInstance(defaultInstance, locationProvider.getSpawnPos());
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

    @Override
    public void initialize() {
        this.initDirectories();
        var jsonObject = new GsonFileHandler(GSON)
                .load(getDataDirectory().resolve(DATABASE_FILE), JsonObject.class);
        jsonObject.ifPresent(object -> this.mongoDatabase = new MongoDatabase(jsonObject.get()));
        this.floorProvider = new FloorProvider(mongoDatabase);
        this.floorInventory = new FloorInventory(editInstanceManager, locationProvider, floorProvider, floorCreateService, containerConsumer);
        this.floorCreateService = new FloorCreateService();
        var created = false;
        if (MinecraftServer.getInstanceManager().getInstances().isEmpty()) {
            LOGGER.info("Found no existing instance. Creating a new instance");
            this.defaultInstance = MinecraftServer.getInstanceManager().createInstanceContainer(KaliDimension.KALI_DIMENSION);
            created = true;
        } else {
            LOGGER.info("Found existing instance. Fetching first instance");
            this.defaultInstance = MinecraftServer.getInstanceManager().getInstances().iterator().next();
        }
        this.defaultInstance.setTime(6_000);
        this.defaultInstance.setTimeRate(0);
        this.registerCommands();
        this.registerEvents(created);
        this.registerBlockHandlers();
        LOGGER.info("Successfully loaded the editor extension for the dungeon");

        var loader = new AnvilLoader("world");
        loader.loadInstance(this.defaultInstance);
    }

    @Override
    public void terminate() {
        if (this.mongoDatabase != null) {
            this.mongoDatabase.disconnect();
            LOGGER.info("Disconnecting from the database!");
        }
    }

    private void initDirectories() {
        if (!Files.exists(getDataDirectory())) {
            try {
                Files.createDirectories(getDataDirectory());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        var roomsFolder = getDataDirectory().resolve("rooms");

        if (!Files.exists(roomsFolder)) {
            try {
                Files.createDirectories(roomsFolder);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Registers some handlers for some blocks.
     */
    private void registerBlockHandlers() {
        BlockManager blockManager = MinecraftServer.getBlockManager();
        blockManager.registerHandler("minecraft:skull", SkullHandler::new);
        blockManager.registerHandler("minecraft:sign", SignHandler::new);
        blockManager.registerHandler("minecraft:banner", BannerHandler::new);
        blockManager.registerHandler("minecraft:beacon", BeaconHandler::new);
    }

    /**
     * Registers some listener as global listener into the server event node.
     */
    private void registerEvents(boolean created) {
        Consumer<CancellableEvent> cancelConsumer = event -> event.setCancelled(true);
        var eventHandler = MinecraftServer.getGlobalEventHandler();
        if (created) {
            eventHandler.addListener(PlayerLoginEvent.class, event -> event.setSpawningInstance(defaultInstance));
            LOGGER.info("Add own login listener");
        }

        eventHandler.addListener(PlayerSpawnEvent.class, new PlayerSpawnListener(locationProvider, sidebarViewer, items));
        eventHandler.addListener(PlayerBlockBreakEvent.class, new BlockBreakListener(regionInventory));
        eventHandler.addListener(PlayerChatEvent.class, new PlayerChatListener());
        eventHandler.addListener(PlayerBlockPlaceEvent.class, cancelConsumer::accept);

        eventHandler.addListener(AddEntityToInstanceEvent.class, event -> {
           if (event.getInstance() instanceof EditInstance editInstance && event.getEntity() instanceof Player player) {
               editInstance.add(player);
               this.items.setEditItems(player);
           }
        });

        eventHandler.addListener(RemoveEntityFromInstanceEvent.class, event -> {
            if (event.getInstance() instanceof EditInstance editInstance && event.getEntity() instanceof Player player) {
                editInstance.remove(player);
                player.getInventory().clear();
                editInstance.switchOwner();
            }
        });

        eventHandler.addListener(ItemDropEvent.class, cancelConsumer::accept);
        eventHandler.addListener(PickupItemEvent.class, cancelConsumer::accept);
        eventHandler.addListener(PlayerDeathEvent.class, event -> event.setChatMessage(Component.empty()));
        eventHandler.addListener(PlayerDisconnectEvent.class, new PlayerDisconnectListener(this.sidebarViewer,this.floorCreateService));
        eventHandler.addListener(PlayerUseItemEvent.class, new ItemListener(floorInventory, locationProvider, defaultInstance));
        eventHandler.addListener(PlayerBlockInteractEvent.class, new BlockInteractListener(regionInventory));

        eventHandler.addListener(FloorCreateEvent.class, floorCreateEvent -> {
            this.floorProvider.addFloor(floorCreateEvent.getFloor());
            this.floorInventory.updateInventoryLayout();
        });

        eventHandler.addListener(FloorRemoveEvent.class, floorRemoveEvent -> {
            this.floorProvider.removeFloor(floorRemoveEvent.getFloor());
            this.floorInventory.updateInventoryLayout();
        });
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
        commandManager.register(new PositionCommand(locationProvider));
    }
}
