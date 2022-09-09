package net.theevilreaper.dungeon.util;

import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

/**
 * @author Joltras
 * @version 1.0.0
 * @since 1.0.0
 */
public class KaliDimension {

    public static final DimensionType KALI_DIMENSION = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
            .fixedTime(6000L)
            .ambientLight(2.0f)
            .build();

    static {
        MinecraftServer.getDimensionTypeManager().addDimension(KALI_DIMENSION);
    }

    private KaliDimension() {}
}
