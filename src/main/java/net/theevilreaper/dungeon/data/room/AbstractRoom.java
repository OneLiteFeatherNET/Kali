package net.theevilreaper.dungeon.data.room;

import net.minestom.server.item.ItemStack;

public class AbstractRoom {

    private static final int DEFAULT_SCALE = 2;

    private int xPos;
    private int yPos;

    private int scaleX;
    private int scaleY;

    private transient ItemStack itemStack;

    public AbstractRoom(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
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
}
