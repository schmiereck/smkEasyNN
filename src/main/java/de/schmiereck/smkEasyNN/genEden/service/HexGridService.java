package de.schmiereck.smkEasyNN.genEden.service;

import java.util.Objects;
import java.util.Random;

public class HexGridService {
    private HexGrid hexGrid = new HexGrid(10*2, 10*3+2);
    private int stepCount = 0;
    private int partCount = 0;

    public HexGridService() {
        this.hexGrid.getGridNode(2, 7).setPart(new Part());
        this.hexGrid.getGridNode(12, 14).setPart(new Part());
//        for (int pos = 0; pos < 20; pos++) {
//            final int xPos = rnd.nextInt(this.hexGrid.xSize);
//            final int yPos = rnd.nextInt(this.hexGrid.ySize);
//            final GridNode targetGridNode = this.hexGrid.getGridNode(xPos, yPos);
//            if (Objects.isNull(targetGridNode.getPart())) {
//                targetGridNode.setPart(new Part());
//            }
//            targetGridNode.setPart(new Part());
//        }
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

    public GridNode retrieveGridNode(final int xPos, final int yPos, final InDir inDir) {
        final int xOff;
        final int yOff;
        if (yPos % 2 == 0) {
            switch (inDir) {
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
                default: throw new IllegalStateException("Unexpected inDir \"%s\".".formatted(inDir));
            }
        } else {
            switch (inDir) {
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
                default: throw new IllegalStateException("Unexpected inDir \"%s\".".formatted(inDir));
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

    private void calcGrid() {
        for (int yPos = 0; yPos < this.hexGrid.ySize; yPos++) {
            for (int xPos = 0; xPos < this.hexGrid.xSize; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                final Part part = gridNode.getPart();
                if (Objects.isNull(part)) {
                    //final InDir inDir = InDir.InDir5; {
                    for (final InDir inDir : InDir.values()) {
                        Field field = gridNode.getInField(inDir);
                        final double value = field.outValue / 7.0D; // 4.0D;
                        field.outValue = 0.0D;
                        if (value > 0.021D) {
                            {
                                final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, inDir);
                                //gridNode1.getInField(inDir).inValue += value;
                                calcInField(outGridNode, inDir, value*1.0D);
                            }
                            {
                                final InDir outInDir = calcOffDir(inDir, +1);
                                final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, outInDir);
                                calcInField(outGridNode, inDir, value*3.0D);
                            }
                            {
                                final InDir outInDir = calcOffDir(inDir, -1);
                                final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, outInDir);
                                calcInField(outGridNode, inDir, value*3.0D);
                            }
                        }
                    }
                }
            }
        }
        for (int yPos = 0; yPos < this.hexGrid.ySize; yPos++) {
            for (int xPos = 0; xPos < this.hexGrid.xSize; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                for (final InDir inDir : InDir.values()) {
                    Field inField = gridNode.getInField(inDir);
                    inField.outValue = inField.inValue;
                    inField.inValue = 0.0D;
                }
            }
        }
        for (int yPos = 0; yPos < this.hexGrid.ySize; yPos++) {
            for (int xPos = 0; xPos < this.hexGrid.xSize; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                final Part part = gridNode.getPart();
                if (Objects.nonNull(part)) {
                    this.calcPart(gridNode, part);
                }
            }
        }
        for (int yPos = 0; yPos < this.hexGrid.ySize; yPos++) {
            for (int xPos = 0; xPos < this.hexGrid.xSize; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                final Part part = gridNode.getPart();
                if (Objects.nonNull(part)) {
                    //final InDir inDir = InDir.InDir5; {
                    for (final InDir inDir : InDir.values()) {
                        final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, inDir);
                        final Field inField = outGridNode.getInField((inDir));
                        inField.outValue = 1.0D;
                        //gridNode.getInField(inDir).inValue = 0.0D;
                    }
                }
            }
        }
    }

    private static void calcInField(final GridNode outGridNode, final InDir inDir, final double value) {
        final Field inField = outGridNode.getInField(inDir);
        inField.inValue += value;
    }

    private static InDir calcOffDir(InDir inDir, int dirOff) {
        return InDir.values()[(inDir.ordinal() + dirOff + InDir.values().length) % InDir.values().length];
    }

    private InDir calcOppositeDir(final InDir inDir) {
        final InDir outDir =
                switch (inDir) {
                    case InDir0 -> InDir.InDir3;
                    case InDir1 -> InDir.InDir4;
                    case InDir2 -> InDir.InDir5;
                    case InDir3 -> InDir.InDir0;
                    case InDir4 -> InDir.InDir1;
                    case InDir5 -> InDir.InDir2;
                };
        return outDir;
    }

    private static final Random rnd = new Random();

    private void calcPart(final GridNode sourceGridNode, final Part part) {
        double field = 0.0D;//Double.MAX_VALUE;
        InDir targetDir = null;
        final int startDirOrdinal = rnd.nextInt(InDir.values().length);
        for (final InDir posInDir : InDir.values()) {
            final InDir inDir = InDir.values()[(startDirOrdinal + posInDir.ordinal()) % InDir.values().length];
            final GridNode targetGridNode = this.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), inDir);
            if (Objects.isNull(targetGridNode.getPart())) {
                if (sourceGridNode.getInField(inDir).outValue > field) {
                    field = sourceGridNode.getInField(inDir).outValue;
                    targetDir = inDir;
                }
            }
        }
        final InDir moveDir;
        if (Objects.isNull(targetDir)) {
            moveDir = InDir.values()[rnd.nextInt(InDir.values().length)];
        } else {
            moveDir = targetDir;
        }

        final GridNode targetGridNode = this.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), moveDir);
        if (Objects.isNull(targetGridNode.getPart())) {
            sourceGridNode.setPart(null);
            targetGridNode.setPart(part);
        }
    }
}
