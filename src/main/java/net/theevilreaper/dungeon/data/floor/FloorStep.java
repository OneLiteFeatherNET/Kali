package net.theevilreaper.dungeon.data.floor;

/**
 * The enum contains all possible steps needed to create a floor.
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.0.0
 **/
@Deprecated
public enum FloorStep {

    NAME(0),
    EXTERNAL(1),
    ID(2),
    MATERIAL(3);

    private final int id;

    private static final FloorStep[] VALUES = values();

    /**
     * Creates a new floor step with an id.
     * @param id the id for the step
     */
    FloorStep(int id) {
        this.id = id;
    }

    /**
     * Returns the id from the step.
     * @return the underlying id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the underlying cached value array from the enum.
     * @return the given array
     */
    public static FloorStep[] getValues() {
        return VALUES;
    }
}
