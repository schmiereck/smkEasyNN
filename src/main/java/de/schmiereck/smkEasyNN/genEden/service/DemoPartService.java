package de.schmiereck.smkEasyNN.genEden.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.schmiereck.smkEasyNN.genEden.service.Part.calcNextPartNr;

public class DemoPartService implements PartServiceInterface {
    private static int MAX_MOVE_CNT = 0;

    private final HexGridService hexGridService;

    public DemoPartService(final HexGridService hexGridService) {
        this.hexGridService = hexGridService;
    }

    @Override
    public List<Part> initParts() {
        final List<Part> partList = new ArrayList<>();

        for (int xPos = 0; xPos < 10; xPos++) {
            this.createBlocker(5 + xPos, 10);
        }
        for (int yPos = 0; yPos < 40; yPos += 4) {
            this.createBlocker(25, 24 + yPos);
        }

        this.hexGridService.retrieveGridNode(2, 7).setOutPart(this.createDemoPart());
        this.hexGridService.retrieveGridNode(12, 14).setOutPart(this.createDemoPart());

        final int partCount = (this.hexGridService.getXGridSize() * this.hexGridService.getYGridSize()) / (30);
        for (int pos = 0; pos < partCount; pos++) {
            final int xPos = HexGridService.rnd.nextInt(this.hexGridService.getXGridSize() / 3);
            final int yPos = HexGridService.rnd.nextInt(this.hexGridService.getYGridSize());
            final GridNode targetGridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
            if (Objects.isNull(targetGridNode.getOutPart())) {
                final Part part = this.createDemoPart();
                targetGridNode.setOutPart(part);
                partList.add(part);
            }
        }
        return partList;
    }

    public Part createDemoPart() {
        final double[] visibleValueArr = new double[] {
                0.5D,
                0.5D,
                //HexGridService.rnd.nextDouble(0.25D) + 0.75D,
                //HexGridService.rnd.nextDouble(0.25D) + 0.75D
                1.0D
        };
        final DemoPart part = new DemoPart(calcNextPartNr(), visibleValueArr);
        part.moveCnt = HexGridService.rnd.nextInt(MAX_MOVE_CNT + 1);
        return part;
    }

    private void createBlocker(final int xPos, final int yPos) {
        final GridNode targetGridNode = this.hexGridService.retrieveGridNode(xPos, yPos);
        if (Objects.isNull(targetGridNode.getOutPart())) {
            final Part part = new BlockerPart(calcNextPartNr(), new double[] { 0.5D, 0.5D, 0.5D });
            targetGridNode.setOutPart(part);
        }
    }

    @Override
    public void calcParts() {
    }
    /**
     * 0. Part: Out -> Net-Input
     */
    @Override
    public void calcPartInput(final GridNode sourceGridNode, final Part outPart) {
        if (outPart instanceof DemoPart outDemoPart) {
            //this.calcPartMoveDir(sourceGridNode, outDemoPart);
        }
    }

    /**
     * 3. Part: Out -> In
     */
    @Override
    public void calcPartOutToIn(final GridNode sourceGridNode, final Part outPart) {
        if (outPart instanceof DemoPart outDemoPart) {
            this.calcDemoPartOutToIn(sourceGridNode, outDemoPart);
        } else {
            if (outPart instanceof BlockerPart) {
                final GridNode targetGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos());
                targetGridNode.setInPart(outPart);
            } else {
                throw new RuntimeException("Unexpected Part-Type \"%s\".".formatted(outPart.getClass().getSimpleName()));
            }
        }
    }

    private void calcDemoPartOutToIn(final GridNode sourceGridNode, final DemoPart outDemoPart) {
        final GridNode targetGridNode;
        if (outDemoPart.moveCnt >= MAX_MOVE_CNT) {
            //this.calcPartMoveDirToSmallest(sourceGridNode, outDemoPart);
            this.calcPartMoveDirToSmallest2(sourceGridNode, outDemoPart);
            //this.calcPartMoveDirReflectBiggest(sourceGridNode, outDemoPart);
            outDemoPart.moveCnt = 0;
            targetGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), outDemoPart.moveDir);
        } else {
            outDemoPart.moveCnt++;
            targetGridNode = sourceGridNode;
        }

        //synchronized (targetGridNode)
        {
            if (Objects.isNull(targetGridNode.getInPart())) {
                targetGridNode.setInPart(outDemoPart);
            } else {
                //if (Objects.isNull(sourceGridNode.getInPart()))
                {
                    sourceGridNode.setInPart(outDemoPart); // TODO make Thread-Safe
                }
            }
        }
    }

    private void calcPartMoveDirToSmallest(GridNode sourceGridNode, DemoPart part) {
        //double targetFieldValue = 0.0D;//Double.MAX_VALUE;
        double targetFieldValue = Double.MAX_VALUE;
        //final double oppositeTargetFieldValue;
        HexDir targetDir = null;
        final HexDir startDir;
        final int startDirOrdinal;
        //final HexDir startOppositeDir;
        if (Objects.isNull(part.moveDir)) {
            startDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
        } else {
            startDir = part.moveDir;
        }
        startDirOrdinal = startDir.ordinal();
        {
            //startOppositeDir = HexDirUtils.calcOppositeDir(startDir);
            //final GridNode moveGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), startOppositeDir);
            //oppositeTargetFieldValue = sumFieldArr(moveGridNode.getField(startDir).outValueArr);
        }
        for (final HexDir posHexDir : HexDir.values()) {
            final HexDir hexDir = HexDir.values()[(startDirOrdinal + posHexDir.ordinal()) % HexDir.values().length];
            final GridNode moveGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), hexDir);
            //if (Objects.isNull(moveGridNode.getInPart()) && Objects.isNull(moveGridNode.getOutPart())) {
            if (Objects.isNull(moveGridNode.getOutPart())) {
                final HexDir oppositeHexDir = HexDirUtils.calcOppositeDir(hexDir);
                final double dirFieldValue = sumFieldArr(moveGridNode.getField(oppositeHexDir).outValueArr);
                if (dirFieldValue < targetFieldValue) {
                    targetFieldValue = dirFieldValue;
                    targetDir = hexDir;
                }
            }
        }
        //if ((targetFieldValue == oppositeTargetFieldValue) && (targetDir != startOppositeDir) && (targetDir != startDir)) {
            //targetDir = startOppositeDir;
        //}
        final HexDir moveDir;
        if (Objects.isNull(targetDir)) {
            moveDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
        } else {
            moveDir = targetDir;
        }

        part.moveDir = moveDir;
    }

    private void calcPartMoveDirToSmallest2(GridNode sourceGridNode, DemoPart part) {
        //double targetFieldValue = 0.0D;//Double.MAX_VALUE;
        double targetFieldValue = Double.MAX_VALUE;
        //final double oppositeTargetFieldValue;
        HexDir targetDir = null;
        final HexDir startDir;
        final int startDirOrdinal;
        //final HexDir startOppositeDir;
        if (Objects.isNull(part.moveDir)) {
            startDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
        } else {
            startDir = part.moveDir;
        }
        startDirOrdinal = startDir.ordinal();
        {
            //startOppositeDir = HexDirUtils.calcOppositeDir(startDir);
            //final GridNode moveGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), startOppositeDir);
            //oppositeTargetFieldValue = sumFieldArr(moveGridNode.getField(startDir).outValueArr);
        }
        for (final HexDir posHexDir : HexDir.values()) {
            final HexDir hexDir = HexDir.values()[(startDirOrdinal + posHexDir.ordinal()) % HexDir.values().length];
            final GridNode moveGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), hexDir);
            //if (Objects.isNull(moveGridNode.getInPart()) && Objects.isNull(moveGridNode.getOutPart())) {
            if (Objects.isNull(moveGridNode.getOutPart())) {
                final HexDir oppositeHexDir = HexDirUtils.calcOppositeDir(hexDir);
                final double dirFieldValue = sumFieldArr(moveGridNode.getField(oppositeHexDir).outValueArr);

                final GridNode oppositeMoveGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), oppositeHexDir);
                final double oppositeDirFieldValue = sumFieldArr(oppositeMoveGridNode.getField(hexDir).outValueArr);

                final double diffFieldValue = dirFieldValue - (oppositeDirFieldValue);

                if (diffFieldValue < targetFieldValue) {
                    targetFieldValue = diffFieldValue;
                    targetDir = hexDir;
                }
            }
        }
        //if ((targetFieldValue == oppositeTargetFieldValue) && (targetDir != startOppositeDir) && (targetDir != startDir)) {
            //targetDir = startOppositeDir;
        //}
        final HexDir moveDir;
        if (Objects.isNull(targetDir)) {
            moveDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
        } else {
            moveDir = targetDir;
        }

        part.moveDir = moveDir;
    }

    private void calcPartMoveDirReflectBiggest(GridNode sourceGridNode, DemoPart part) {
        double targetFieldValue = Double.MIN_VALUE;
        HexDir targetDir = null;
        final HexDir startDir;
        final int startDirOrdinal;
        if (Objects.isNull(part.moveDir)) {
            startDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
        } else {
            startDir = HexDirUtils.calcOppositeDir(part.moveDir);
        }
        startDirOrdinal = startDir.ordinal();
        for (final HexDir posHexDir : HexDir.values()) {
            final HexDir hexDir = HexDir.values()[(startDirOrdinal + posHexDir.ordinal()) % HexDir.values().length];
            final GridNode moveGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), hexDir);
            final HexDir oppositeHexDir = HexDirUtils.calcOppositeDir(hexDir);
            final GridNode oppositeMoveGridNode = this.hexGridService.retrieveGridNode(sourceGridNode.getXPos(), sourceGridNode.getYPos(), oppositeHexDir);
            //if (Objects.isNull(moveGridNode.getInPart()) && Objects.isNull(moveGridNode.getOutPart())) {
            if (Objects.isNull(oppositeMoveGridNode.getOutPart())) {
                final double dirFieldValue = sumFieldArr(moveGridNode.getField(oppositeHexDir).outValueArr);
                if (dirFieldValue > targetFieldValue) {
                    targetFieldValue = dirFieldValue;
                    targetDir = oppositeHexDir;
                }
            }
        }
        final HexDir moveDir;
        if (Objects.isNull(targetDir)) {
            moveDir = HexDir.values()[HexGridService.rnd.nextInt(HexDir.values().length)];
        } else {
            moveDir = targetDir;
        }

        part.moveDir = moveDir;
    }

    private static double sumFieldArr(final double[] outValueArr) {
        return outValueArr[0] + outValueArr[1] + outValueArr[2];
    }

    @Override
    public void calcBeginNext() {
    }


    @Override
    public int retrieveGenerationCount() {
        return 0;
    }

    @Override
    public void submitGenerationCount(final int generationCount) {
    }
}
