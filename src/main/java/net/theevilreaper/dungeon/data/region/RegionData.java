package net.theevilreaper.dungeon.data.region;

import net.minestom.server.item.Material;
import net.theevilreaper.dungeon.data.DungeonObject;
import net.theevilreaper.dungeon.data.region.parameter.RegionParameter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
public class RegionData extends DungeonObject {

    private final List<RegionParameter> parameterList;

    /**
     * Creates a new region data object
     * @param name
     * @param material
     * @param description
     * @param parameterList
     */
    public RegionData(@NotNull String name,
                      @NotNull Material material,
                      @NotNull String description,
                      @NotNull List<RegionParameter> parameterList) {
        super(name, material, description);
        this.parameterList = parameterList;
    }

    public RegionData(@NotNull String name,
                      @NotNull Material material,
                      @NotNull String description,
                      @NotNull RegionParameter... parameterList) {
        super(name, material, description);
        this.parameterList = Arrays.stream(parameterList).toList();
    }

    public RegionData(@NotNull String name, @NotNull Material material, @NotNull String description) {
        super(name, material, description);
        this.parameterList = new ArrayList<>();
    }

    public <T extends RegionParameter> void addParameter(@NotNull T regionParameter) {
        this.parameterList.add(regionParameter);
    }

    /**
     * Returns the list with the given parameters of a region.
     * @return the list with the parameters
     */
    @NotNull
    public List<RegionParameter> getParameterList() {
        return parameterList;
    }
}