package de.schmiereck.smkEasyNN.genEden.service;

import java.util.Objects;
import java.util.Random;

public class HexGridService {
    private HexGrid hexGrid = new HexGrid(30*2, 30*3);
    private int stepCount = 0;
    private int partCount = 0;
    private static int MAX_MOVE_CNT = 4;
    private static final Random rnd = new Random();

    public HexGridService() {
        this.hexGrid.getGridNode(2, 7).setOutPart(new Part());
        this.hexGrid.getGridNode(12, 14).setOutPart(new Part());
        final int partCount = (hexGrid.xSize * hexGrid.ySize) / 30;
        for (int pos = 0; pos < partCount; pos++) {
            final int xPos = rnd.nextInt(this.hexGrid.xSize/3);
            final int yPos = rnd.nextInt(this.hexGrid.ySize);
            final GridNode targetGridNode = this.hexGrid.getGridNode(xPos, yPos);
            if (Objects.isNull(targetGridNode.getOutPart())) {
                final Part part = new Part();
                part.moveCnt = rnd.nextInt(MAX_MOVE_CNT);
                targetGridNode.setOutPart(part);
            }
        }
    }

    public int retrieveStepCount() {
        return this.stepCount;
    }

    public int retrievePartCount() {
        return this.partCount;
    }

    public HexGrid retrieveHexGrid() {
        return this.hexGrid;
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

    public void calcNext() {
        this.calcGrid();

        //this.calcNextCellArrPos();
        //this.clearNextGrid();

        this.stepCount++;
    }

    /**
     * Calculate the next grid.
     * Expected that Out-Values are set.
     */
    private void calcGrid() {
        //--------------------------------------------------------------------------------------------------------------
        // Field: Out -> In
        for (int yPos = 0; yPos < this.hexGrid.ySize; yPos++) {
            for (int xPos = 0; xPos < this.hexGrid.xSize; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                final Part part = gridNode.getOutPart();
                //if (Objects.isNull(part))
                {
                    //final InDir inDir = InDir.InDir5; {
                    for (final HexDir hexDir : HexDir.values()) {
                        final Field field = gridNode.getField(hexDir);
                        final double fieldOutValue = field.outValue / 7.0D; // 4.0D;
                        //field.outValue = 0.0D;
                        //if (fieldOutValue > 0.021D) {
                        //if (fieldOutValue > 0.042D) {
                        if (fieldOutValue > 0.035D) {
                        //if (fieldOutValue > 0.028D) {
                            {
                                final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, hexDir);
                                calcInField(outGridNode, hexDir, fieldOutValue*1.0D);
                            }
                            {
                                final HexDir outHexDir = calcOffDir(hexDir, +1);
                                final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, outHexDir);
                                calcInField(outGridNode, hexDir, fieldOutValue*3.0D);
                            }
                            {
                                final HexDir outHexDir = calcOffDir(hexDir, -1);
                                final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, outHexDir);
                                calcInField(outGridNode, hexDir, fieldOutValue*3.0D);
                            }
                        }
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        // Field: In -> Out
        for (int yPos = 0; yPos < this.hexGrid.ySize; yPos++) {
            for (int xPos = 0; xPos < this.hexGrid.xSize; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                for (final HexDir hexDir : HexDir.values()) {
                    final Field field = gridNode.getField(hexDir);
                    field.outValue = field.inValue;
                    field.inValue = 0.0D;
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        // Part: Out -> In
        for (int yPos = 0; yPos < this.hexGrid.ySize; yPos++) {
            for (int xPos = 0; xPos < this.hexGrid.xSize; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                this.calcPart(gridNode);
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        // Reset Part: Out
        // Part-Field: Out
        // Part: In -> Out
        this.partCount = 0;
        for (int yPos = 0; yPos < this.hexGrid.ySize; yPos++) {
            for (int xPos = 0; xPos < this.hexGrid.xSize; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                final Part outPart = gridNode.getOutPart();
                if (Objects.nonNull(outPart)) {
                    gridNode.setOutPart(null);
                }
                final Part inPart = gridNode.getInPart();
                if (Objects.nonNull(inPart)) {
                    gridNode.setInPart(null);
                    gridNode.setOutPart(inPart);

                    //final InDir inDir = InDir.InDir5; {
                    for (final HexDir hexDir : HexDir.values()) {
                        //final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, hexDir);
                        //final Field field = outGridNode.getField((hexDir));
                        final Field field = gridNode.getField((hexDir));
                        field.outValue = 1.0D;
                        //gridNode.getInField(inDir).inValue = 0.0D;
                    }
                    this.partCount++;
                }
            }
        }
    }

    private static void calcInField(final GridNode outGridNode, final HexDir hexDir, final double fieldValue) {
        if (Objects.isNull(outGridNode.getOutPart())) {
            final Field field = outGridNode.getField(hexDir);
            field.inValue += fieldValue;
        }
    }

    private static HexDir calcOffDir(HexDir hexDir, int dirOff) {
        return HexDir.values()[(hexDir.ordinal() + dirOff + HexDir.values().length) % HexDir.values().length];
    }

    private HexDir calcOppositeDir(final HexDir hexDir) {
        final HexDir outDir =
                switch (hexDir) {
                    case InDir0 -> HexDir.InDir3;
                    case InDir1 -> HexDir.InDir4;
                    case InDir2 -> HexDir.InDir5;
                    case InDir3 -> HexDir.InDir0;
                    case InDir4 -> HexDir.InDir1;
                    case InDir5 -> HexDir.InDir2;
                };
        return outDir;
    }

    private void calcPart(final GridNode sourceGridNode) {
        final Part part = sourceGridNode.getOutPart();
        if (Objects.nonNull(part)) {
            final GridNode targetGridNode;
            if (part.moveCnt >= MAX_MOVE_CNT) {
                //double targetFieldValue = 0.0D;//Double.MAX_VALUE;
                double targetFieldValue = Double.MAX_VALUE;
                HexDir targetDir = null;
                final int startDirOrdinal;
                if (Objects.isNull(part.lastDir)) {
                    startDirOrdinal = rnd.nextInt(HexDir.values().length);
                } else {
                    startDirOrdinal = part.lastDir.ordinal();
                }
                for (final HexDir posHexDir : HexDir.values()) {
                    final HexDir hexDir = HexDir.values()[(startDirOrdinal + posHexDir.ordinal()) % HexDir.values().length];
                    final GridNode moveGridNode = this.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), hexDir);
                    if (Objects.isNull(moveGridNode.getInPart()) && Objects.isNull(moveGridNode.getOutPart())) {
                        final HexDir oppositeHexDir = calcOppositeDir(hexDir);
                        final double dirFieldValue = moveGridNode.getField(oppositeHexDir).outValue;
                        if (dirFieldValue < targetFieldValue) {
                            targetFieldValue = dirFieldValue;
                            targetDir = hexDir;
                        }
                    }
                }
                final HexDir moveDir;
                if (Objects.isNull(targetDir)) {
                    moveDir = HexDir.values()[rnd.nextInt(HexDir.values().length)];
                } else {
                    moveDir = targetDir;
                }

                part.lastDir = moveDir;
                part.moveCnt = 0;
                targetGridNode = this.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), moveDir);
            } else {
                part.moveCnt++;
                targetGridNode = sourceGridNode;
            }

            if (Objects.isNull(targetGridNode.getInPart())) {
                targetGridNode.setInPart(part);
            }
        }
    }
}
