package de.schmiereck.smkEasyNN.genEden.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class HexGridService {
    private HexGrid hexGrid = new HexGrid(30*2, 30*3);
    private int stepCount = 0;
    private int partCount = 0;
    private List<Part> partList = new ArrayList<>();
    static final Random rnd = new Random();
    private final DemoPartService demoPartService;
    private final GeneticPartService geneticPartService;
    public static boolean demoMode = false;

    public HexGridService() {
        this.demoPartService = new DemoPartService(this);
        this.geneticPartService = new GeneticPartService(this);

        if (demoMode) {
            this.partList = this.demoPartService.initDemoParts();
        } else {
            this.partList = this.geneticPartService.initGeneticParts();
        }
        this.partCount = this.partList.size();
    }

    int getYGridSize() {
        return this.hexGrid.ySize;
    }

    int getXGridSize() {
        return this.hexGrid.xSize;
    }

    public int retrieveStepCount() {
        return this.stepCount;
    }

    public int retrievePartCount() {
        return this.partCount;
    }

    public List<Part> retrievePartList() {
        return this.partList;
    }

    public void submitPartList(final List<Part> partList) {
        this.partCount = 0;
        this.partList.clear();
        for (int yPos = 0; yPos < this.getYGridSize(); yPos++) {
            for (int xPos = 0; xPos < this.getXGridSize(); xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                final Part outPart = gridNode.getOutPart();
                if (Objects.nonNull(outPart)) {
                    gridNode.setOutPart(null);
                }
            }
        }
        partList.forEach(part -> {
            final GridNode gridNode = this.retrieveGridNode(rnd.nextInt(this.getXGridSize()), rnd.nextInt(this.getYGridSize()));
            gridNode.setOutPart(part);
            this.partList.add(part);
            this.partCount++;
        });
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
        for (int yPos = 0; yPos < this.getYGridSize(); yPos++) {
            for (int xPos = 0; xPos < this.getXGridSize(); xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                //final InDir inDir = InDir.InDir5; {
                for (final HexDir hexDir : HexDir.values()) {
                    final Field field = gridNode.getField(hexDir);
                    final double[] fieldOutValueArr = field.outValueArr;
                    double fieldOutValue = fieldOutValueArr[0] + fieldOutValueArr[1] + fieldOutValueArr[2];
                    //field.outValue = 0.0D;
                    //if (fieldOutValue > 0.021D) {
                    //if (fieldOutValue > 0.042D) {
                    //if (fieldOutValue > (0.035D * 7.0D * 3)) {
                    if (fieldOutValue > (0.01D * 7.0D * 3)) {
                        //if (fieldOutValue > 0.028D) {
                        {
                            final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, hexDir);
                            if (Objects.isNull(outGridNode.getOutPart())) {
                                calcInField(outGridNode, hexDir, fieldOutValueArr, 1.0D / 7.0D - 0.05D);
                            }
                        }
                        {
                            final HexDir outLHexDir = calcOffDir(hexDir, +1);
                            final GridNode outLGridNode = this.retrieveGridNode(xPos, yPos, outLHexDir);
                            if (Objects.isNull(outLGridNode.getOutPart())) {
                                calcInField(outLGridNode, hexDir, fieldOutValueArr, 3.0D / 7.0D - 0.05D);
                            }
                        }
                        {
                            final HexDir outRHexDir = calcOffDir(hexDir, -1);
                            final GridNode outRGridNode = this.retrieveGridNode(xPos, yPos, outRHexDir);
                            if (Objects.isNull(outRGridNode.getOutPart())) {
                                calcInField(outRGridNode, hexDir, fieldOutValueArr, 3.0D / 7.0D - 0.05D);
                            }
                        }
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        // Field: In -> Out
        for (int yPos = 0; yPos < this.getYGridSize(); yPos++) {
            for (int xPos = 0; xPos < this.getXGridSize(); xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                for (final HexDir hexDir : HexDir.values()) {
                    final Field field = gridNode.getField(hexDir);
                    field.outValueArr[0] = field.inValueArr[0];
                    field.outValueArr[1] = field.inValueArr[1];
                    field.outValueArr[2] = field.inValueArr[2];
                    field.inValueArr[0] = 0.0D;
                    field.inValueArr[1] = 0.0D;
                    field.inValueArr[2] = 0.0D;
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        // Part: Out -> In
        for (int yPos = 0; yPos < this.getYGridSize(); yPos++) {
            for (int xPos = 0; xPos < this.getXGridSize(); xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                if (demoMode) {
                    this.calcDemoPart(gridNode);
                } else {
                    this.calcGeneticPart(gridNode);
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        // Reset Part: Out
        // Part-Field: Out
        // Part: In -> Out
        this.partCount = 0;
        this.partList.clear();
        for (int yPos = 0; yPos < this.getYGridSize(); yPos++) {
            for (int xPos = 0; xPos < this.getXGridSize(); xPos++) {
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
                        final double[] visibleValueArr = inPart.getVisibleValueArr();
                        final Field field = gridNode.getField((hexDir));
                        field.outValueArr[0] = visibleValueArr[0];
                        field.outValueArr[1] = visibleValueArr[1];
                        field.outValueArr[2] = visibleValueArr[2];
                        //gridNode.getInField(inDir).inValue = 0.0D;
                    }
                    this.partCount++;
                    this.partList.add(inPart);
                }
            }
        }
        if (!demoMode) {
            this.geneticPartService.calc();
        }
    }

    private static void calcInField(final GridNode outGridNode, final HexDir hexDir, final double[] fieldValueArr, final double factor) {
        if (Objects.isNull(outGridNode.getOutPart())) {
            final Field field = outGridNode.getField(hexDir);
            field.inValueArr[0] += fieldValueArr[0] * factor;
            field.inValueArr[1] += fieldValueArr[1] * factor;
            field.inValueArr[2] += fieldValueArr[2] * factor;
        }
    }

    private static HexDir calcOffDir(final HexDir hexDir, final int dirOff) {
        return HexDir.values()[(hexDir.ordinal() + dirOff + HexDir.values().length) % HexDir.values().length];
    }

    static HexDir calcRotateDir(final HexDir hexDir, final HexDir rotateHexDir) {
        return calcRotateDir(hexDir, rotateHexDir.ordinal());
    }
    static HexDir calcRotateDir(final HexDir hexDir, final int dirRotate) {
        return HexDir.values()[(hexDir.ordinal() + dirRotate + HexDir.values().length) % HexDir.values().length];
    }

    static HexDir calcOppositeDir(final HexDir hexDir) {
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

    private void calcGeneticPart(final GridNode sourceGridNode) {
        final Part part = sourceGridNode.getOutPart();
        if (Objects.nonNull(part)) {
            if (part instanceof GeneticPart) {
                this.geneticPartService.calcPart(sourceGridNode, (GeneticPart) part);
            } else {
                throw new RuntimeException("Unexpected Part-Type \"%s\".".formatted(part.getClass().getSimpleName()));
            }
        }
    }

    private void calcDemoPart(final GridNode sourceGridNode) {
        final Part part = sourceGridNode.getOutPart();
        if (Objects.nonNull(part)) {
            if (part instanceof DemoPart) {
                this.demoPartService.calcPart(sourceGridNode, (DemoPart) part);
            } else {
                throw new RuntimeException("Unexpected Part-Type \"%s\".".formatted(part.getClass().getSimpleName()));
            }
        }
    }
}
