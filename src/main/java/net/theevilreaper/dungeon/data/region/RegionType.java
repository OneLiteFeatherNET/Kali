package net.theevilreaper.dungeon.data.region;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.data.region.parameter.RegionIntegerParameter;
import net.theevilreaper.dungeon.data.region.parameter.RegionParameter;
import net.theevilreaper.dungeon.data.region.parameter.RegionStringParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The enum represents all region that can be used in a room.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 */
public enum RegionType {

    COMBAT(Material.DIAMOND_SWORD, Component.text("Combat").color(TextColor.fromHexString("#F80346")), List.of(
            new RegionStringParameter("Mob", Material.ENDERMAN_SPAWN_EGG, "Set the mob type", "random"),
            new RegionIntegerParameter("Amount", Material.EMERALD, "Set the amount of mobs", 5, 1)
    )),
    LOOT(Material.CHEST, Component.text("Loot").color(TextColor.fromHexString("#85F2AD")), List.of(
            new RegionStringParameter("Type", Material.GOLD_INGOT, "Set the loot type", "random")
    )),
    COINS(Material.GOLD_NUGGET, Component.text("Coins").color(TextColor.fromHexString("#EFC752")), List.of(
            new RegionIntegerParameter("Coins", Material.GOLD_NUGGET, "Set the coin amount", 1, 100)
    )),
    SHOP(Material.ENDER_CHEST, Component.text("Shop").color(TextColor.fromHexString("#9ACEEB")), null),
    AMBIENT(Material.LIGHT, Component.text("Ambient").color(TextColor.fromHexString("#e698ed")), List.of(
            new RegionIntegerParameter("", Material.ALLIUM, "", 0, 100)
    )),
    BOSS(Material.DRAGON_EGG, Component.text("Boss"), List.of(
            new RegionStringParameter("Type", Material.DROWNED_SPAWN_EGG, "Create a region for a boss","random")
    ));

    private static final RegionType[] VALUES = values();

    private final Material material;
    private final Component name;
    private final List<RegionParameter> regionParameter;

    /**
     * Creates a new type with the given values.
     * @param material the material for the region
     * @param name the name for the region
     * @param regionParameter a list which contains all {@link RegionParameter} from a region
     */
    RegionType(@NotNull Material material, @NotNull Component name, @NotNull List<RegionParameter> regionParameter) {
        this.material = material;
        this.name = name;
        this.regionParameter = regionParameter;
    }

    /**
     * Returns the material from a type.
     * @return the given material
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * Return the give name as {@link Component} from a type.
     * @return the name as {@link Component}
     */
    @NotNull
    public Component getName() {
        return name;
    }

    /**
     * Returns the list which contains the {@link RegionData}.
     * @return the given list
     */
    public @NotNull List<RegionParameter> getRegionParameter() {
        return regionParameter;
    }

    /**
     * Returns the right RegionType based on the given {@link Material}.
     * @param material the material to determine the typ
     * @return The determined type otherwise null
     */
    @Nullable
    public static RegionType getByMaterial(@NotNull Material material) {
        for (int i = 0; i < VALUES.length; i++) {
            if (VALUES[i].material == material) {
                return VALUES[i];
            }
        }
        return null;
    }

    /**
     * Returns an array which contains all region types.
     * @return the given array
     */
    @NotNull
    public static RegionType[] getValues() {
        return VALUES;
    }
}
