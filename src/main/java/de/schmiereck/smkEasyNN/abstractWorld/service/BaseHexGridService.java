package de.schmiereck.smkEasyNN.abstractWorld.service;

import de.schmiereck.smkEasyNN.genEden.service.GridNode;
import de.schmiereck.smkEasyNN.genEden.service.HexDir;
import de.schmiereck.smkEasyNN.genEden.service.HexGrid;

public abstract class BaseHexGridService<$ServiceContext extends BaseServiceContext> {
    private HexGrid hexGrid;
    private int stepCount = 0;
    protected final $ServiceContext serviceContext;

    public BaseHexGridService(final $ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    public void init(final int xSize, final int ySize) {
        this.hexGrid = new HexGrid(xSize, ySize);
    }

    public abstract void calcNext();

    protected void increaseStepCount() {
        this.stepCount++;
    }

    public abstract int retrievePartCount();

    public int getYGridSize() {
        return this.hexGrid.ySize;
    }

    public int getXGridSize() {
        return this.hexGrid.xSize;
    }

    public int retrieveStepCount() {
        return this.stepCount;
    }

    public HexGrid retrieveHexGrid() {
        return this.hexGrid;
    }

    public void submitStepCount(final int stepCount) {
        this.stepCount = stepCount;
    }

    public GridNode retrieveGridNode(final int xPos, final int yPos) {
        return this.hexGrid.getGridNode(xPos, yPos);
    }

    public GridNode retrieveGridNode(final int xPos, final int yPos, final HexDir hexDir) {
        final int xOff;
        final int yOff;
        if (yPos % 2 == 0) {
            switch (hexDir) {
                case InDir0:
                    xOff = 0;
                    yOff = -2;
                    break;
                case InDir1:
                    xOff = 0;
                    yOff = -1;
                    break;
                case InDir2:
                    xOff = 0;
                    yOff = 1;
                    break;
                case InDir3:
                    xOff = 0;
                    yOff = 2;
                    break;
                case InDir4:
                    xOff = -1;
                    yOff = 1;
                    break;
                case InDir5:
                    xOff = -1;
                    yOff = -1;
                    break;
                default: throw new IllegalStateException("Unexpected inDir \"%s\".".formatted(hexDir));
            }
        } else {
            switch (hexDir) {
                case InDir0:
                    xOff = 0;
                    yOff = -2;
                    break;
                case InDir1:
                    xOff = 1;
                    yOff = -1;
                    break;
                case InDir2:
                    xOff = 1;
                    yOff = 1;
                    break;
                case InDir3:
                    xOff = 0;
                    yOff = 2;
                    break;
                case InDir4:
                    xOff = 0;
                    yOff = 1;
                    break;
                case InDir5:
                    xOff = 0;
                    yOff = -1;
                    break;
                default: throw new IllegalStateException("Unexpected inDir \"%s\".".formatted(hexDir));
            }
        }
        return this.hexGrid.getGridNode(xPos + xOff, yPos + yOff);
    }
}