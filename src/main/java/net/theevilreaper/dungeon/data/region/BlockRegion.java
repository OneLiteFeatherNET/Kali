package net.theevilreaper.dungeon.data.region;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * @author theEvilReaper
 * @version 1.0.0
 * @since 1.1.0
 **/
public record BlockRegion(@NotNull Vec firstVec, @NotNull Vec secondVec, @NotNull Vec size) {

    public BlockRegion(@NotNull Point one, @NotNull Point two) {
        this(new Vec(
                Math.min(one.blockX(), two.blockX()),
                Math.min(one.blockY(), two.blockY()),
                Math.min(one.blockZ(), two.blockZ())
        ),
        new Vec(
                Math.max(one.blockX(), two.blockX()),
                Math.max(one.blockY(), two.blockY()),
                Math.max(one.blockZ(), two.blockZ())
        ),
        new Vec(
                Math.abs(one.blockX() - two.blockX()) + 1,
                Math.abs(one.blockY() - two.blockY()) + 1,
                Math.abs(one.blockZ() - two.blockZ()) + 1
        ));
    }

    public Vec getCenter() {
        return new Vec(
                (firstVec.blockX() + secondVec.blockX() + 1) / 2F,
                (firstVec.blockY() + secondVec.blockY() + 1) / 2F,
                (firstVec.blockZ() + secondVec.blockZ() + 1) / 2F
        );
    }

    public void iterateOverXZ(@NotNull BiConsumer<Integer, Integer> position) {
        for (var x = firstVec.blockX(); x <= secondVec.blockX(); x++) {
            for (var z = firstVec.blockZ(); x <= secondVec.blockZ(); z++) {
                position.accept(x, z);
            }
        }
    }

    public void iterateXYZ(@NotNull TriConsumer<Integer, Integer, Integer> position) {
        for (var x = firstVec.blockX(); x <= secondVec.blockX(); x++) {
            for (var y = firstVec.blockY(); y <= secondVec.blockY(); y++)
                for (var z = firstVec.blockZ(); x <= secondVec.blockZ(); z++) {
                    position.accept(x, y, z);
                }
        }
    }

    public boolean intersect(@NotNull Point point) {
        return this.intersect(point.x(), point.y(), point.z());
    }

    public boolean intersect(@NotNull Point point, double distance) {
        return this.intersect(point.x(), point.y(), point.z(), distance);
    }

    public boolean intersect(double x, double y, double z) {
        return (x >= firstVec.blockX() && x  <= secondVec.blockX() + 1) &&
                (y >= firstVec.blockY() && y <= secondVec.blockY() + 1) &&
                (z >= firstVec.blockZ() && z <= secondVec.blockZ() + 1);
    }

    public boolean intersect(double x, double y, double z, double distance) {
        return (x + distance >= firstVec.blockX() && x - distance <= secondVec.blockX() + 1) &&
                (y + distance >= firstVec.blockY() && y - distance <= secondVec.blockY() + 1) &&
                (z + distance >= firstVec.blockZ() && z - distance <= secondVec.blockZ() + 1);
    }

    /*public boolean intersect(@NotNull BoundingBox boundingBox) {
        return (firstVec.blockX() <= boundingBox.getMaxX() && firstVec.blockX() + 1 >= boundingBox.getMinX()) &&
                (firstVec.blockY() <= boundingBox.getMaxY() && firstVec.blockY() + 1 >= boundingBox.getMinY()) &&
                (firstVec.blockZ() <= boundingBox.getMaxZ() && firstVec.blockZ() + 1 >= boundingBox.getMinZ());
    }*/

    public boolean isEqual(@NotNull BlockRegion blockRegion) {
        return this.firstVec.equals(blockRegion.firstVec) && this.secondVec.equals(blockRegion.secondVec);
    }
}
