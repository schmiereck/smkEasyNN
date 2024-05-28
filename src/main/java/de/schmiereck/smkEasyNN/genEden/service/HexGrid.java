package de.schmiereck.smkEasyNN.genEden.service;

public class HexGrid {
    final int xSize;
    final int ySize;
    private final GridNode[][] grid;
    public HexGrid(final int xSize, final int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.grid = new GridNode[this.xSize][this.ySize];

        for (int posY = 0; posY < this.ySize; posY++) {
            for (int posX = 0; posX < this.xSize; posX++) {
                this.grid[posX][posY] = new GridNode(posX, posY);
            }
        }
    }

    public int getXSize() {
        return this.xSize;
    }

    public int getYSize() {
        return this.ySize;
    }

    public GridNode getGridNode(final int xPos, final int yPos) {
        return this.grid[this.calcXPos(xPos)][this.calcYPos(yPos)];
    }

    private int calcXPos(final int xPos) {
        if (xPos >= 0) {
            return xPos % this.xSize;
        } else {
            return this.xSize + (xPos % this.xSize);
        }
    }

    private int calcYPos(final int yPos) {
        if (yPos >= 0) {
            return yPos % this.ySize;
        } else {
            return this.ySize + (yPos % this.ySize);
        }
    }
}
