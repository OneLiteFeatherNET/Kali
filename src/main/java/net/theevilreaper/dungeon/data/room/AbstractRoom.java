package net.theevilreaper.dungeon.data.room;

import net.minestom.server.item.ItemStack;
import net.theevilreaper.dungeon.data.region.BlockRegion;
import net.theevilreaper.dungeon.data.region.RegionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AbstractRoom {

    private static final int DEFAULT_SCALE = 2;

    private int xPos;
    private int yPos;
    private RegionType roomType;

    private int scaleX;
    private int scaleY;

    private transient ItemStack itemStack;

    private final List<BlockRegion> regions;

    public AbstractRoom(@NotNull RegionType roomType, int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.roomType = roomType;
        this.regions = new ArrayList<>();
    }

    public AbstractRoom() {
        this.regions = new ArrayList<>();
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public void setRoomType(RegionType roomType) {
        this.roomType = roomType;
    }

    public void addRegion(@NotNull BlockRegion blockRegion) {
        this.regions.add(blockRegion);
    }

    public boolean containsRegion(@NotNull BlockRegion blockRegion) {
        if (this.regions.isEmpty()) {
            return false;
        }

        boolean equals = false;

        for (int i = 0; i < this.regions.size() && !equals; i++) {
            var currentRegion = this.regions.get(i);
            equals = currentRegion.isEqual(blockRegion);
        }

        return equals;
    }

    public void setScaleX(int scaleX) {
        if (scaleX == 0 || scaleX % DEFAULT_SCALE != 0) {
            throw new IllegalArgumentException("The given scaleX value is zero or can't be divided by 2 without a residual");
        }
        this.scaleX = scaleX;
    }

    public void setScaleY(int scaleY) {
        if (scaleY == 0 || scaleY % DEFAULT_SCALE != 0) {
            throw new IllegalArgumentException("The given scaleY value is zero or can't be divided by 2 without a residual");
        }
        this.scaleY = scaleY;
    }

    public boolean isSpecialRoom() {
        return false;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public int getScaleX() {
        return scaleX;
    }

    public int getScaleY() {
        return scaleY;
    }

    public RegionType getRoomType() {
        return roomType;
    }

    public ItemStack getItemStack() {
        if (this.itemStack == null) {
            this.itemStack = ItemStack.builder(getRoomType().getMaterial()).displayName(getRoomType().getName()).build();
        }
        return this.itemStack;
    }
}
