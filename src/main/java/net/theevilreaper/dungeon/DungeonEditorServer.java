package net.theevilreaper.dungeon;

import net.minestom.server.MinecraftServer;

public class DungeonEditorServer {

    public static void main(String[] args) {
        MinecraftServer minecraftServer = new MinecraftServer();
        DungeonEditor dungeonEditor = new DungeonEditor();
        // Initialize the server
        dungeonEditor.initialize();
        minecraftServer.start("localhost", 25565);
    }
}
