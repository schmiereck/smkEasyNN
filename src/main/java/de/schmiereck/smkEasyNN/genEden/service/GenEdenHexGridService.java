package de.schmiereck.smkEasyNN.genEden.service;

import de.schmiereck.smkEasyNN.abstractWorld.service.BaseHexGridService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GenEdenHexGridService extends BaseHexGridService<GenEdenServiceContext> {
    private int partCount = 0;
    private List<Part> partList = new ArrayList<>();
    static final Random rnd = new Random();
    private final FieldService fieldService;
    public static boolean threadMode = true;

    public GenEdenHexGridService(final GenEdenServiceContext serviceContext) {
        super(serviceContext);
        this.fieldService = new FieldService();
    }

    @Override
    public void init(final int xSize, final int ySize) {
        super.init(xSize, ySize);

        this.partList = this.serviceContext.getPartService().initParts();
        this.partCount = this.partList.size();
    }

    @Override
    public void calcNext() {
        this.serviceContext.getPartService().calcBeginNext();

        this.calcGrid();

        this.increaseStepCount();
    }

    private final HexGridCalcParallelProcessor proc = new HexGridCalcParallelProcessor();

    /**
     * Calculate the next grid.
     * Expected that Out-Values are set.
     */
    private void calcGrid() {
        this.partCount = 0;
        this.partList.clear();

        if (threadMode) {
            this.partCount = this.proc.processHexGrid(this);
        } else {
            this.partCount = this.calcGrid(0, 0, this.getXGridSize() - 1, this.getYGridSize() - 1);
        }

        this.serviceContext.getPartService().calcParts();
    }

    int calcGrid(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        this.calcGridPartNetInput(xStartPos, yStartPos, xEndPos, yEndPos);
        this.calcGridFieldOutToIn(xStartPos, yStartPos, xEndPos, yEndPos);
        this.calcGridFieldInToOut(xStartPos, yStartPos, xEndPos, yEndPos);
        this.calcGridPartOutIn(xStartPos, yStartPos, xEndPos, yEndPos);
        return this.calcGridOut(xStartPos, yStartPos, xEndPos, yEndPos);
    }

    /**
     * 0. Part: Out -> Net-Input
     */
    public void calcGridPartNetInput(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                final Part outPart = gridNode.getOutPart();
                if (Objects.nonNull(outPart)) {
                    this.serviceContext.getPartService().calcPartInput(gridNode, outPart); // TODO Callback Funktion to GeneticPartService.
                }
            }
        }
    }

    /**
     * 1. Field: Out -> In
     */
    public void calcGridFieldOutToIn(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        //
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);

                this.fieldService.calcGridNodeFieldOutToIn(this, gridNode, xPos, yPos);
            }
        }
    }

    /**
     * 2. Field: In -> Out
     */
    public void calcGridFieldInToOut(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
                final GridNode gridNode = this.retrieveGridNode(xPos, yPos);
                for (final HexDir hexDir : HexDir.values()) {
                    this.fieldService.calcGridNodeFieldInToOut(gridNode, hexDir);
                }
            }
        }
    }

    /**
     * 3. Part: Out -> In
     */
    public void calcGridPartOutIn(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        for (int yPos = yStartPos; yPos <= yEndPos; yPos++) {
            for (int xPos = xStartPos; xPos <= xEndPos; xPos++) {
                final GridNode sourceGridNode = this.retrieveGridNode(xPos, yPos);
                final Part outPart = sourceGridNode.getOutPart();
                if (Objects.nonNull(outPart)) {
                    this.serviceContext.getPartService().calcPartOutToIn(sourceGridNode, outPart);
                }
            }
        }
    }

    /**
     * 4. Part: Reset Out
     *    Part: In -> Out
     *    Part-Field: Out
     */
    public int calcGridOut(final int xStartPos, final int yStartPos, final int xEndPos, final int yEndPos) {
        int retPartCount = 0;
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
                    inPart.setGridNode(gridNode);
                    retPartCount++;
                    this.partList.add(inPart);
                }

                this.fieldService.calcGridNodeOutField(gridNode);
            }
        }
        return retPartCount;
    }

    @Override
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
}
