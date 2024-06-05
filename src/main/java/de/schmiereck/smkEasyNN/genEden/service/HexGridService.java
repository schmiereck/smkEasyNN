package de.schmiereck.smkEasyNN.genEden.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static de.schmiereck.smkEasyNN.genEden.service.FieldUtils.*;

public class HexGridService {
    private HexGrid hexGrid;
    private int stepCount = 0;
    private int partCount = 0;
    private List<Part> partList = new ArrayList<>();
    static final Random rnd = new Random();
    private final DemoPartService demoPartService;
    private final GeneticPartService geneticPartService;
    public static boolean demoMode = false;
    public static boolean threadMode = true;

    public HexGridService() {
        this.demoPartService = new DemoPartService(this);
        this.geneticPartService = new GeneticPartService(this);
    }

    public void init(final int xSize, final int ySize) {
        this.hexGrid = new HexGrid(xSize, ySize);

        if (demoMode) {
            this.partList = this.demoPartService.initDemoParts();
        } else {
            for (int xPos = 0; xPos < 10; xPos++) {
                createBlocker(5 + xPos, 10);
            }
            for (int yPos = 0; yPos < 40; yPos += 4) {
                createBlocker(25, 14 + yPos);
            }
            this.partList = this.geneticPartService.initGeneticParts();
        }
        this.partCount = this.partList.size();
    }

    private void createBlocker(final int xPos, final int yPos) {
        final GridNode targetGridNode = this.retrieveGridNode(xPos, yPos);
        if (Objects.isNull(targetGridNode.getOutPart())) {
            final Part part = new BlockerPart(new double[] { 0.5D, 0.5D, 0.5D });
            targetGridNode.setOutPart(part);
        }
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
                if (Objects.nonNull(outPart) && outPart instanceof GeneticPart) {
                    gridNode.setOutPart(null);
                }
            }
        }
        partList.forEach(part -> {
            do {
                final GridNode gridNode = this.retrieveGridNode(rnd.nextInt(this.getXGridSize()), rnd.nextInt(this.getYGridSize()));
                if (Objects.isNull(gridNode.getOutPart())) {
                    gridNode.setOutPart(part);
                    this.partList.add(part);
                    this.partCount++;
                    break;
                }
            } while (true);
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
        this.geneticPartService.calcBeginNext();

        this.calcGrid();

        //this.calcNextCellArrPos();
        //this.clearNextGrid();

        this.stepCount++;
    }

    private final HexGridParallelProcessor proc = new HexGridParallelProcessor();

    /**
     * Calculate the next grid.
     * Expected that Out-Values are set.
     */
    private void calcGrid() {
        if (threadMode) {
            this.proc.processHexGrid(this);
        } else {
            this.calcGrid(0, 0, this.getXGridSize() - 1, this.getYGridSize() - 1);
        }
        if (!demoMode) {
            this.geneticPartService.calc();
        }
    }

    void calcGrid(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        this.calcGridFieldOutToIn(xStartPos, yStartPos, xEndPos, yEndPos);
        this.calcGridFieldInToOut(xStartPos, yStartPos, xEndPos, yEndPos);
        this.calcGridPartOutIn(xStartPos, yStartPos, xEndPos, yEndPos);
        this.calcGridOut(xStartPos, yStartPos, xEndPos, yEndPos);
    }

    public void calcGridOut(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        // Reset Part: Out
        // Part-Field: Out
        // Part: In -> Out
        this.partCount = 0;
        this.partList.clear();
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
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

                        final double[] visibleValueArr = inPart.getValueFieldArr();
                        field.outValueArr[0] = visibleValueArr[0];
                        field.outValueArr[1] = visibleValueArr[1];
                        field.outValueArr[2] = visibleValueArr[2];

                        final double[] comFieldArr = inPart.getComFieldArr();
                        field.outComArr[0] = comFieldArr[0];
                        field.outComArr[1] = comFieldArr[1];
                        field.outComArr[2] = comFieldArr[2];
                    }
                    this.partCount++;
                    this.partList.add(inPart);
                }
            }
        }
    }

    public void calcGridPartOutIn(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        // Part: Out -> In
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                if (demoMode) {
                    this.calcDemoPart(gridNode);
                } else {
                    this.calcGeneticPart(gridNode); // TODO Callback Funktion to GeneticPartService.
                }
            }
        }
    }

    public void calcGridFieldInToOut(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        // Field: In -> Out
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                for (final HexDir hexDir : HexDir.values()) {
                    final Field field = gridNode.getField(hexDir);
                    transferFieldInToOut(field);
                    resetInField(field);
                }
            }
        }
    }

    public void calcGridFieldOutToIn(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        // Field: Out -> In
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                //final InDir inDir = InDir.InDir5; {
                for (final HexDir hexDir : HexDir.values()) {
                    final Field field = gridNode.getField(hexDir);

                    final double[] outValueFieldArr = field.outValueArr;
                    double outValueFieldSum = outValueFieldArr[0] + outValueFieldArr[1] + outValueFieldArr[2];
                    //field.outValue = 0.0D;
                    //if (outValueFieldSum > 0.021D) {
                    //if (outValueFieldSum > 0.042D) {
                    //if (outValueFieldSum > (0.035D * 7.0D * 3)) {
                    if (outValueFieldSum > (0.01D * 7.0D * 3)) {
                        //if (outValueFieldSum > 0.028D) {
                        {
                            final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, hexDir);
                            if (Objects.isNull(outGridNode.getOutPart())) {
                                calcInValueField(outGridNode, hexDir, field, 1.0D / 7.0D - 0.05D);
                            }
                        }
                        {
                            final HexDir outLHexDir = HexDirUtils.calcOffDir(hexDir, +1);
                            final GridNode outLGridNode = this.retrieveGridNode(xPos, yPos, outLHexDir);
                            if (Objects.isNull(outLGridNode.getOutPart())) {
                                calcInValueField(outLGridNode, hexDir, field, 3.0D / 7.0D - 0.05D);
                            }
                        }
                        {
                            final HexDir outRHexDir = HexDirUtils.calcOffDir(hexDir, -1);
                            final GridNode outRGridNode = this.retrieveGridNode(xPos, yPos, outRHexDir);
                            if (Objects.isNull(outRGridNode.getOutPart())) {
                                calcInValueField(outRGridNode, hexDir, field, 3.0D / 7.0D - 0.05D);
                            }
                        }
                    }

                    final double[] outComFieldArr = field.outComArr;
                    double outComFieldSum = outComFieldArr[0] + outComFieldArr[1] + outComFieldArr[2];
                    if (outComFieldSum > (0.01D * 7.0D * 3)) {
                        {
                            final GridNode outGridNode = this.retrieveGridNode(xPos, yPos, hexDir);
                            if (Objects.isNull(outGridNode.getOutPart())) {
                                calcInComField(outGridNode, hexDir, field, 1.0D / 7.0D - 0.05D);
                            }
                        }
                        {
                            final HexDir outLHexDir = HexDirUtils.calcOffDir(hexDir, +1);
                            final GridNode outLGridNode = this.retrieveGridNode(xPos, yPos, outLHexDir);
                            if (Objects.isNull(outLGridNode.getOutPart())) {
                                calcInComField(outLGridNode, hexDir, field, 3.0D / 7.0D - 0.05D);
                            }
                        }
                        {
                            final HexDir outRHexDir = HexDirUtils.calcOffDir(hexDir, -1);
                            final GridNode outRGridNode = this.retrieveGridNode(xPos, yPos, outRHexDir);
                            if (Objects.isNull(outRGridNode.getOutPart())) {
                                calcInComField(outRGridNode, hexDir, field, 3.0D / 7.0D - 0.05D);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void calcInValueField(final GridNode outGridNode, final HexDir hexDir, final Field outField, final double factor) {
        if (Objects.isNull(outGridNode.getOutPart())) {
            final Field inField = outGridNode.getField(hexDir);
            transferValueFieldOutToIn(outField, inField, factor);
        }
    }

    private static void calcInComField(final GridNode outGridNode, final HexDir hexDir, final Field outField, final double factor) {
        if (Objects.isNull(outGridNode.getOutPart())) {
            final Field inField = outGridNode.getField(hexDir);
            transferComFieldOutToIn(outField, inField, factor);
        }
    }

    private void calcGeneticPart(final GridNode sourceGridNode) {
        final Part part = sourceGridNode.getOutPart();
        if (Objects.nonNull(part)) {
            if (part instanceof GeneticPart geneticPart) {
                this.geneticPartService.calcPart(sourceGridNode, geneticPart);
            } else {
                if (part instanceof BlockerPart) {
                    sourceGridNode.setInPart(part);
                } else {
                    if (part instanceof EnergyPart energyPart) {
                        if (energyPart.energie > 0) {
                            this.geneticPartService.consumeEnergie(energyPart, 1);
                            sourceGridNode.setInPart(part);
                        }
                    } else {
                        throw new RuntimeException("Unexpected Part-Type \"%s\".".formatted(part.getClass().getSimpleName()));
                    }
                }
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

    public GeneticPartService getGeneticPartService() {
        return this.geneticPartService;
    }

    public void submitStepCount(final int stepCount) {
        this.stepCount = stepCount;
    }
}
